package com.example.donation_drive_app.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.donation_drive_app.R;
import com.example.donation_drive_app.api.Item;
import com.example.donation_drive_app.ui.catalogue.CatalogueAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference itemsRef;
    private ArrayList<Item> itemsArrayList;
    private ArrayList<String> users;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private String currentUserId; // Replace this with the actual user ID logic

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firebase and RecyclerView
        database = FirebaseDatabase.getInstance("https://what-the-hack-2627e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        itemsRef = database.getReference("items");

        itemsArrayList = new ArrayList<>();
        users = new ArrayList<>();
        // Initialize the TextView
        TextView textHome = rootView.findViewById(R.id.text_home);

        // Load data
        fetchItemsFromFirebase(() -> {
            // Update the TextView dynamically after fetching data
            int userCount = users.size();
            textHome.setText(String.format("Welcome!\nYou have %d orders to attend to", userCount));
        });

        // Set up RecyclerView
        recyclerView = rootView.findViewById(R.id.userRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(users);
        recyclerView.setAdapter(userAdapter);

        return rootView;
    }

    private void fetchItemsFromFirebase(Runnable onDataLoadedCallback) {
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemsArrayList.clear();
                users.clear();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    if (item != null) {
                        itemsArrayList.add(item);

                        if ("reserved".equals(item.getStatus()) && item.getReservedUserId() != null && !users.contains(item.getReservedUserId())) {
                            users.add(item.getReservedUserId());
                        }
                    }
                }

                Log.d("HomeFragment", "Items size: " + itemsArrayList.size());
                Log.d("HomeFragment", "User size: " + users.size());

                // Notify adapter and execute the callback
                userAdapter.notifyDataSetChanged();
                if (onDataLoadedCallback != null) onDataLoadedCallback.run();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("HomeFragment", "Error reading items data", databaseError.toException());
            }
        });
    }
}
