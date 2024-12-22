package com.example.donation_drive_app.ui.upload;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.widget.Toast;

import com.example.donation_drive_app.R;
import com.example.donation_drive_app.databinding.FragmentUploadBinding;
import okhttp3.MediaType;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadsFragment extends Fragment {

    private FragmentUploadBinding binding;
    private ExecutorService cameraExecutor;
    private ImageCapture imageCapture;
    private Bitmap capturedBitmap; // Temporarily store the captured bitmap

    private ActivityResultLauncher<String> requestPermissionLauncher;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUploadBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startCamera();
                    } else {
                        binding.captureButton.setVisibility(View.INVISIBLE); // remove capture button
                        binding.retakeButton.setVisibility(View.INVISIBLE); // remove retake button
                        Toast.makeText(requireContext(), "Camera permission denied, Enable permissions in settings", Toast.LENGTH_LONG).show();
                    }
                }
        );

        binding.previewView.post(() -> {
            int width = binding.previewView.getWidth();
            int height = (int) (width * (4.0 / 3.0)); // 3:4 aspect ratio
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.previewView.getLayoutParams();
            layoutParams.height = height;
            binding.previewView.setLayoutParams(layoutParams);
        });

        cameraExecutor = Executors.newSingleThreadExecutor();

        checkCameraPermission();

        binding.captureButton.setOnClickListener(v -> {
            binding.captureButton.setVisibility(View.INVISIBLE); // Hide capture button
            binding.retakeButton.setVisibility(View.VISIBLE); // Show retake button
            binding.nextButton.setVisibility(View.VISIBLE); // Show next button
            captureImage();
        });

        binding.retakeButton.setOnClickListener(v -> {
            binding.retakeButton.setVisibility(View.INVISIBLE); // Hide retake button
            binding.captureButton.setVisibility(View.VISIBLE); // Show capture button
            binding.nextButton.setVisibility(View.INVISIBLE); // Hide next button
            deleteTempFile();
            retakeImage();
        });

        binding.nextButton.setOnClickListener(v -> {
//            deleteTempFile();
//            saveImage();
//            Intent intent = new Intent(getContext(), UploadDetails.class);
            // Optionally, pass any data to the new activity
            if (capturedBitmap != null) {
                File tempFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image.jpg");
                uploadImageToFirebaseAndMove(tempFile);
//                intent.putExtra("image_path", tempFile.getAbsolutePath());
            }

//            // Start the new activity
//            startActivity(intent);
//
//            // Optionally, finish the current activity if you no longer need the fragment
//            requireActivity().finish();
        });



        return root;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                androidx.camera.core.Preview preview = new androidx.camera.core.Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
                int rotation = binding.previewView.getDisplay().getRotation();
                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(rotation)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void captureImage() {
        File tempFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image.jpg");

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(tempFile).build();

        imageCapture.takePicture(
                outputFileOptions,
                cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        requireActivity().runOnUiThread(() -> {
                            try {
                                ExifInterface exif = new ExifInterface(tempFile.getAbsolutePath());
                                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                                Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                                capturedBitmap = rotateBitmap(bitmap, orientation); // Store bitmap temporarily

                                binding.previewView.setVisibility(View.INVISIBLE); // Hide preview
                                binding.capturedImageView.setVisibility(View.VISIBLE); // Show captured image
                                binding.capturedImageView.setImageBitmap(capturedBitmap);

                                Toast.makeText(requireContext(), "Image captured", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Image capture failed: " + error.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                }
        );
    }

    private void saveImage() {
        if (capturedBitmap != null) {
            // Get current time for filename
            File photoFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image.jpg");

            try (FileOutputStream out = new FileOutputStream(photoFile)) {
                capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                Toast.makeText(requireContext(), "Image saved: " + photoFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

                // Hide captured image view and reset UI
                binding.capturedImageView.setVisibility(View.INVISIBLE);
                binding.previewView.setVisibility(View.VISIBLE);
                binding.retakeButton.setVisibility(View.INVISIBLE);
                binding.nextButton.setVisibility(View.INVISIBLE);
                binding.captureButton.setVisibility(View.VISIBLE);

                capturedBitmap = null; // Clear the temporary bitmap
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebaseAndMove(File imageFile) {
        if (imageFile != null) {
            String filepath = "images/temp_image.jpg";
            Uri fileUri = Uri.fromFile(imageFile);

            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(filepath);

            imageRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Firebase upload completed successfully
                        if (isAdded()) { // Ensure fragment is still attached
                            Toast.makeText(requireContext(), "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                            // Now move to the next activity
                            // Get the download URL after successful upload
                            FirebaseStorage.getInstance().getReference(filepath)
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        // Make the HTTP request with the URI
                                        makeHttpRequest(uri, imageFile);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure to get the download URL
                                        if (isAdded()) {
                                            Toast.makeText(requireContext(), "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            if (isAdded()) {
                Toast.makeText(requireContext(), "No image to upload", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makeHttpRequest(Uri imageUri, File imageFile) {
        if (imageUri == null) {
            if (isAdded()) {
                Toast.makeText(requireContext(), "Image URI is null", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Build the JSON request body
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("image_url", imageUri.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            if (isAdded()) {
                Toast.makeText(requireContext(), "Failed to create JSON request body", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Set up OkHttpClient
        OkHttpClient client = new OkHttpClient();

        // Create the request body
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, jsonBody.toString());

        // Build the HTTP request
        Request request = new Request.Builder()
                .url("https://analyze-image-service-112962837866.asia-east1.run.app/analyze-image") // Replace with your actual endpoint
                .post(requestBody)
                .build();

        // Execute the HTTP request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle HTTP request failure
                requireActivity().runOnUiThread(() -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "HTTP Request Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;

                    try {
                        if (response.isSuccessful()) {
                            // Parse the JSON response
                            String responseString = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseString);

                            // Extract data from the response
                            String category = jsonResponse.optString("category", "");
                            String description = jsonResponse.optString("description", "");
                            String item = jsonResponse.optString("item", "");
                            String quality = jsonResponse.optString("quality", "");

                            // Display or use the extracted data
                            requireActivity().runOnUiThread(() -> {
                                // Move to the next activity
                                moveToNextActivity(imageFile, category, description, item, quality);
                            });
                        } else {
                            // Handle unsuccessful response
                            String errorMessage = response.message();
                            requireActivity().runOnUiThread(() -> {
                                if (isAdded()) {
                                    Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (IOException | JSONException e) {
                        // Handle parsing exceptions
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() -> {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "Error processing response", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } finally {
                        // Ensure response body is closed to prevent leaks
                        response.close();
                    }
                });
            }
        });
    }

    private void moveToNextActivity(File imageFile, String category, String description, String item, String quality) {
        Intent intent = new Intent(requireContext(), UploadDetails.class);
        intent.putExtra("image_path", imageFile.getAbsolutePath());
        // Pass the additional details
        intent.putExtra("category", category);
        intent.putExtra("description", description);
        intent.putExtra("item", item);
        intent.putExtra("quality", quality);
        startActivity(intent);
        requireActivity().finish();
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void retakeImage() {
        binding.previewView.setVisibility(View.VISIBLE); // Show preview
        binding.capturedImageView.setVisibility(View.INVISIBLE); // Hide captured image
        capturedBitmap = null; // Clear temporary bitmap
    }

    private void deleteTempFile() {
        File tempFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image.jpg");
        if (tempFile.exists()) {
            boolean deleted = tempFile.delete();
            Toast.makeText(requireContext(), "Deleted temp image", Toast.LENGTH_SHORT).show();
            if (!deleted) {
                Toast.makeText(requireContext(), "Failed to Delete image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        binding = null;
    }
}