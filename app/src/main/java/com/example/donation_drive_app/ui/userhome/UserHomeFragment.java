package com.example.donation_drive_app.ui.userhome;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.donation_drive_app.R;
import com.example.donation_drive_app.api.Item;
import com.example.donation_drive_app.ui.catalogue.CatalogueAdapter;
import com.example.donation_drive_app.ui.catalogue.ItemViewPage;
import com.example.donation_drive_app.ui.home.UserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class UserHomeFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference itemsRef;
    private ArrayList<Item> itemsArrayList;
    private ArrayList<Item> clothesArrayList;
    private RecyclerView recyclerView;
    private CatalogueAdapter clothesAdapter;

    public UserHomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_home, container, false);

        // init Firebase
        database = FirebaseDatabase.getInstance("https://what-the-hack-2627e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        itemsRef = database.getReference("items");

        itemsArrayList = new ArrayList<>();
        clothesArrayList = new ArrayList<>();

        recyclerView = rootView.findViewById(R.id.clothesRecyclerViewItems);
        clothesAdapter = new CatalogueAdapter(getContext(), clothesArrayList, item -> {
            Log.d("CatalogueFragment", "Item id: " + item.getId());
            // Navigate to item details page
            Intent intent = new Intent(getContext(), ItemViewPage.class);
            intent.putExtra("name", item.getName());
            intent.putExtra("hostName", item.getHostName());
            intent.putExtra("category", item.getCategory());
            intent.putExtra("uploadTime", item.getUploadTime());
            intent.putExtra("photoString", item.getPhotoString());
            intent.putExtra("description", item.getDescription());
            startActivity(intent);
        });
        recyclerView.setAdapter(clothesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchItemsFromFirebase();

        // Add click listeners to category buttons
        Button clothesButton = rootView.findViewById(R.id.button_clothes);
        Button booksButton = rootView.findViewById(R.id.button_books);
        Button toysButton = rootView.findViewById(R.id.button_toys);
        Button stationeryButton = rootView.findViewById(R.id.button_stationery);

        clothesButton.setOnClickListener(v -> filterItemsByCategory("Clothes"));
        booksButton.setOnClickListener(v -> filterItemsByCategory("Books"));
        toysButton.setOnClickListener(v -> filterItemsByCategory("Toys"));
        stationeryButton.setOnClickListener(v -> filterItemsByCategory("Stationery"));

        // sort list by date from latest to earliest date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

        itemsArrayList.sort(new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                try {
                    // Parse uploadTime strings into Date objects
                    Date date1 = sdf.parse(item1.getUploadTime());
                    Date date2 = sdf.parse(item2.getUploadTime());

                    // Return comparison result in reverse order (latest to earliest)
                    assert date2 != null;
                    return date2.compareTo(date1);
                } catch (Exception e) {
                    // Handle potential parsing exceptions
                    e.printStackTrace();
                    return 0; // Default return value in case of error
                }
            }
        });

        return rootView;
    }

    private void filterItemsByCategory(String category) {
        clothesArrayList.clear(); // Clear the current list

        for (Item item : itemsArrayList) {
            if (item.getCategory().equals(category)) {
                clothesArrayList.add(item);
            }
        }

        clothesAdapter.notifyDataSetChanged(); // Notify the adapter of the changes
        Log.d("CatalogueFragment", "Filtered items size for " + category + ": " + clothesArrayList.size());
    }


    private void fetchItemsFromFirebase() {
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemsArrayList.clear();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    Log.d("CatalogueFragment", "My Category: " + item.getCategory());
                    if (item != null) {
                        itemsArrayList.add(item);
                    }
                    filterItemsByCategory("Clothes");
                }

                clothesAdapter.notifyDataSetChanged();
                Log.d("CatalogueFragment", "Items size: " + clothesArrayList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Log.e("CatalogueFragment", "Error reading items data", databaseError.toException());
            }
        });
    }
}