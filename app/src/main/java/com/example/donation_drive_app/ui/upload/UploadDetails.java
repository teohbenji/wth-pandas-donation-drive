package com.example.donation_drive_app.ui.upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.donation_drive_app.HostOrgActivity;
import com.example.donation_drive_app.R;
import com.example.donation_drive_app.api.Item;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class UploadDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_upload_details);

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

        final TextView nameEditText = findViewById(R.id.nameEditText);
        final TextView categoryEditText = findViewById(R.id.categoryEditText);
        final TextView descriptionEditText = findViewById(R.id.descriptionEditText);

        // Set up the confirm button
        findViewById(R.id.confirmButton).setOnClickListener(view -> {
            // retrieve values from EditTexts
            final String itemName = nameEditText.getText().toString();
            final String itemCategory = categoryEditText.getText().toString();
            final String itemDescription = descriptionEditText.getText().toString();

            uploadItemsToFirebase(itemName, itemCategory, itemDescription);

            Toast.makeText(UploadDetails.this, "Details Confirmed!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, HostOrgActivity.class);
            intent.putExtra("navigate_to_fragment", true);
            startActivity(intent);
            finish(); // Close the activity after confirmation
        });
    }

    private void uploadItemsToFirebase(String itemName, String itemCategory, String itemDescription) {
        // Get reference to the "items" node
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://what-the-hack-2627e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference itemsRef = database.getReference("items");

        // Create a new Item object
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        final String uploadTime = sdf.format(System.currentTimeMillis());
        final String hostName = "Salvation Army";
        final String photoString = "test.com";

        final Item newItem = new Item(itemName, hostName, photoString, uploadTime, itemCategory, itemDescription, "available", "");

        DatabaseReference newItemRef = itemsRef.push();

        // push new item to Firebase
        newItemRef.setValue(newItem)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    Toast.makeText(UploadDetails.this, "Item Added Successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(UploadDetails.this, "Failed to add item!", Toast.LENGTH_SHORT).show();
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
