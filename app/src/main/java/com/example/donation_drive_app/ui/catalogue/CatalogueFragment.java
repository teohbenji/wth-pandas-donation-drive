package com.example.donation_drive_app.ui.catalogue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class CatalogueFragment extends Fragment {

    private ArrayList<Item> itemsArrayList;
    private RecyclerView recyclerView;
    private CatalogueAdapter catalogueAdapter;
    private FirebaseDatabase database;
    private DatabaseReference itemsRef;

    public CatalogueFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalogue, container, false);

        // init Firebase
        database = FirebaseDatabase.getInstance("https://what-the-hack-2627e-default-rtdb.asia-southeast1.firebasedatabase.app/");
        itemsRef = database.getReference("items");

        itemsArrayList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerViewItems);
        catalogueAdapter = new CatalogueAdapter(getContext(), itemsArrayList);
        recyclerView.setAdapter(catalogueAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchItemsFromFirebase();

        return rootView;
    }


    private void fetchItemsFromFirebase() {
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemsArrayList.clear();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    if (item != null) {
                        itemsArrayList.add(item);
                    }
                }

                catalogueAdapter.notifyDataSetChanged();
                Log.d("CatalogueFragment", "Items size: " + itemsArrayList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Log.e("CatalogueFragment", "Error reading items data", databaseError.toException());
            }
        });
    }
}
