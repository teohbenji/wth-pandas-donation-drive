package com.example.donation_drive_app.ui.wishlist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donation_drive_app.R;
import com.example.donation_drive_app.api.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class WishlistFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference itemsRef;
    private ArrayList<Item> itemsArrayList;
    private RecyclerView recyclerView;
    private WishlistAdapter wishlistAdapter;
    private String currentUserId; // Replace this with the actual user ID logic

    private ArrayList<String> fullNamesArrayList = new ArrayList<>(Arrays.asList(
            "Jane Lim Mei Ling",
            "Bryan Goh Choon Seng",
            "Ng Hui Min",
            "Chong Wei Kiat",
            "Lee Hwee Ling",
            "David Teo Zhi Hao",
            "Wong Kai Ming",
            "Tan Siew Ling",
            "Ahmad Faris",
            "Nurul Huda",
            "Aisyah Binte Rahman",
            "Hafiz Bin Abdullah",
            "Syafiqah Binte Mohamad",
            "Fahmi Bin Rosli",
            "Nurul Aina",
            "Zulkifli Bin Hassan",
            "Aminah Binte Salleh",
            "Hakim Bin Abdul Rahim",
            "Siti Nurhaliza",
            "Rajesh Kumar",
            "Priya Subramaniam",
            "Vijay Anand",
            "Lakshmi Narayanan",
            "Arun Prakash",
            "Deepa Krishnan",
            "Ravi Chandran"
    ));

    public WishlistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);

        // Initialize Firebase and RecyclerView
        database = FirebaseDatabase.getInstance("https://what-the-hack-2627e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        itemsRef = database.getReference("items");

        itemsArrayList = new ArrayList<>();
        fetchItemsFromFirebase();

        // Set up RecyclerView
        recyclerView = rootView.findViewById(R.id.userRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        wishlistAdapter = new WishlistAdapter(itemsArrayList);
        recyclerView.setAdapter(wishlistAdapter);

        Button checkoutButton = rootView.findViewById(R.id.checkoutButton);

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutItemsFromFirebase();
                Toast.makeText(getContext(), "Item(s) checked out!", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void fetchItemsFromFirebase() {
        // Query items where the status is "wishlist"
        itemsRef.orderByChild("status").equalTo("wishlisted").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemsArrayList.clear(); // Clear the current list to avoid duplicates

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    if (item != null) {
                        itemsArrayList.add(item); // Add the item to the list
                    }
                }

                // Notify your adapter about the updated list
                wishlistAdapter.notifyDataSetChanged(); // Replace with the correct adapter
                Log.d("WishlistFragment", "Wishlist items size: " + itemsArrayList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Log.e("WishlistFragment", "Error reading wishlist items", databaseError.toException());
            }
        });
    }

    private void checkoutItemsFromFirebase() {
        // Create a new ArrayList to hold items to be checked out
        ArrayList<Item> checkoutItemsArrayList = new ArrayList<>(itemsArrayList);

        // Create a list to store the names of the items to be checked out
        ArrayList<String> checkoutItemNames = new ArrayList<>();

        // Extract names from checkoutItemsArrayList
        for (Item item : checkoutItemsArrayList) {
            checkoutItemNames.add(item.getName()); // Assuming getName() returns the item's name
        }

        //update the items' status to "reserved"
        for (String name : checkoutItemNames) {
            itemsRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        if (item != null) {
                            // Update the status to "reserved"
                            item.setStatus("reserved");

                            String randomUserId = fullNamesArrayList.get((int) (Math.random() * fullNamesArrayList.size()));
                            item.setReservedUserId(randomUserId);

                            // Update the item in the database
                            itemSnapshot.getRef().setValue(item);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("WishlistFragment", "Error updating item status", databaseError.toException());
                }
            });
        }
    }
}
