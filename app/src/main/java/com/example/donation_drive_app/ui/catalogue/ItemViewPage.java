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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

            if (itemName != null) {
                // Get reference to the "items" node in the Firebase database
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://what-the-hack-2627e-default-rtdb.asia-southeast1.firebasedatabase.app/");
                DatabaseReference itemsRef = database.getReference("items");

                // Query the items by name (make sure your "name" field is indexed for better performance)
                itemsRef.orderByChild("name").equalTo(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Item found, now update its status to "wishlist"
                            for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                itemSnapshot.getRef().child("status").setValue("wishlisted")
                                        .addOnSuccessListener(aVoid -> {
                                            // Successfully updated the item status
                                            Toast.makeText(ItemViewPage.this, "Item added to wishlist!", Toast.LENGTH_SHORT).show();
                                            finish(); // Finish the activity and return to the catalogue
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure
                                            Toast.makeText(ItemViewPage.this, "Failed to add to wishlist", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            // If no item with the given name is found, handle it here
                            Toast.makeText(ItemViewPage.this, "Item not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle potential errors with the query
                        Toast.makeText(ItemViewPage.this, "Error occurred: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Handle error if item name is not found in Intent
                Toast.makeText(ItemViewPage.this, "Item name is missing", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
