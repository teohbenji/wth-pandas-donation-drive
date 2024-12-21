package com.example.donation_drive_app.ui.catalogue;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.donation_drive_app.R;
import com.google.firebase.storage.FirebaseStorage;

public class ItemViewPage extends AppCompatActivity {

    // Declare UI elements
    private ImageButton backButton;
    private ImageView imageView;
    private TextView textViewItemName, textViewItemHostName, textViewItemCategory,
            textViewItemUploadDate, textViewItemDescription;
    private Button buttonWishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view); // Update to the correct layout file name if different

        // Initialize UI elements
        backButton = findViewById(R.id.backButton);
        imageView = findViewById(R.id.imageView);
        textViewItemName = findViewById(R.id.textViewItemName);
        textViewItemHostName = findViewById(R.id.textViewItemHostName);
        textViewItemCategory = findViewById(R.id.textViewItemCategory);
        textViewItemUploadDate = findViewById(R.id.textViewItemUploadDate);
        textViewItemDescription = findViewById(R.id.textViewItemDescription);
        buttonWishlist = findViewById(R.id.buttonWishlist);

        // Get data from the Intent
        String itemName = getIntent().getStringExtra("name");
        String hostName = getIntent().getStringExtra("hostName");
        String category = getIntent().getStringExtra("category");
        String uploadDate = getIntent().getStringExtra("uploadTime");
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("photoString");

        // Populate UI elements with the data
        textViewItemName.setText(itemName);
        textViewItemHostName.setText(hostName);
        textViewItemCategory.setText(category);
        textViewItemUploadDate.setText(uploadDate);
        textViewItemDescription.setText(description);

        Log.d("ItemViewPage", "Image URL: " + imageUrl);
        // Load image using Glide
        FirebaseStorage.getInstance().getReference(imageUrl)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> Glide.with(this)
                        .load(uri.toString())
                        .into(imageView)
                );

        // Set up back button functionality
        backButton.setOnClickListener(view -> finish());

        // Set up wishlist button functionality
        buttonWishlist.setOnClickListener(view -> {
            // Add logic for adding to wishlist (e.g., update backend or local database)
            Toast.makeText(this, "Added to Wishlist", Toast.LENGTH_SHORT).show();
            //Talk to the database
            //bascially update the item with

            finish();
        });
    }
}
