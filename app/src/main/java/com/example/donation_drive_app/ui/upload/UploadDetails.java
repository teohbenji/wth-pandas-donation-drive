package com.example.donation_drive_app.ui.upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.donation_drive_app.HostOrgActivity;
import com.example.donation_drive_app.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class UploadDetails extends AppCompatActivity {
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_upload_details);

        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Handle receiving data from the previous fragment if needed
        String imagePath = getIntent().getStringExtra("image_path"); // Example of passing data

        // Find the ImageView
        ImageView capturedImageView = findViewById(R.id.myImageView);

        if (imagePath != null) {
            // Decode the image from the path and set it to the ImageView
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            // Get image orientation using ExifInterface
            int orientation = getImageOrientation(imagePath);

            // Rotate the bitmap based on its orientation
            Bitmap rotatedBitmap = rotateBitmap(bitmap, orientation);

            // Set the rotated bitmap to the ImageView
            capturedImageView.setImageBitmap(rotatedBitmap);
        } else {
            // If the image path is null, you can set a placeholder or leave the ImageView empty
            capturedImageView.setImageResource(R.drawable.kid_bicycle); // Replace with your placeholder
        }

        // Set up the back button
        findViewById(R.id.backarrow).setOnClickListener(v -> {
            Intent intent = new Intent(this, HostOrgActivity.class);
            intent.putExtra("navigate_to_fragment", true);
            startActivity(intent);
            finish();
        });

        // Set up the confirm button
        findViewById(R.id.confirmButton).setOnClickListener(view -> {
            // Perform the confirm action here
            // Check if the image path is valid
            if (imagePath != null) {
                // Convert the image file path to a Uri (if it's a file)
                Uri fileUri = Uri.fromFile(new File(imagePath));

                // Create a reference to the Firebase Storage location
                StorageReference imageRef = storageRef.child("images/" + fileUri.getLastPathSegment()); // Save in 'images' folder

                // Upload the image to Firebase Storage
                imageRef.putFile(fileUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Successfully uploaded
                            Toast.makeText(UploadDetails.this, "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();

                            // Optionally, get the URL of the uploaded image
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                // You can save this URL in your database or use it elsewhere in the app
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Handle the failure
                            Toast.makeText(UploadDetails.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(UploadDetails.this, "No image to upload", Toast.LENGTH_SHORT).show();
            }
//            Toast.makeText(UploadDetails.this, "Details Confirmed!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, HostOrgActivity.class);
            intent.putExtra("navigate_to_fragment", true);
            startActivity(intent);
            finish(); // Close the activity after confirmation
        });
    }

    // Method to get the image orientation from Exif data
    private int getImageOrientation(String imagePath) {
        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            e.printStackTrace();
            return ExifInterface.ORIENTATION_NORMAL; // Default to normal if there is an error
        }
    }

    // Method to rotate the bitmap based on its orientation
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
                return bitmap; // No rotation needed if the orientation is normal
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
