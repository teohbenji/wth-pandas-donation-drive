package com.example.donation_drive_app.ui.upload;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
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

import android.Manifest;
import android.widget.Toast;

import com.example.donation_drive_app.databinding.FragmentUploadBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            deleteTempFile();
            saveImage();
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
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File photoFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "captured_image_" + timeStamp + ".jpg");

            try (FileOutputStream out = new FileOutputStream(photoFile)) {
                capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                Toast.makeText(requireContext(), "Image saved: " + photoFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

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