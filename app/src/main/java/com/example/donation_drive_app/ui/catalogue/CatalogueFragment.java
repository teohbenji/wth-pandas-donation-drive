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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class CatalogueFragment extends Fragment {

    // Temp, replace with Firebase later
    private static ArrayList<Item> itemArrayList = new ArrayList<Item>();
    private RecyclerView recyclerView;
    private CatalogueAdapter catalogueAdapter;

    public static void initItems() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, -2);
        Date twoDaysAgo = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, -3);
        Date threeDaysAgo = calendar.getTime();

        itemArrayList.add(new Item(0, "Gen 7th iPad", "Salvation Army", "https://media.karousell.com/media/photos/products/2024/11/5/ipad_9th_gen_64gbwifi_tgn_1730783933_34766178_progressive_thumbnail.jpg", today, "Electronics", "Good condition, 80% battery life", "available", 0));
        itemArrayList.add(new Item(1, "White winter jacket", "Salvation Army", "https://media.karousell.com/media/photos/products/2024/8/28/winter_jacket_1724817890_412a4fd2_progressive.jpg", yesterday, "Clothing", "Excellent condition", "available", 1));
        itemArrayList.add(new Item(2, "Kid bicycle", "Samaritans of Singapore", "https://media.karousell.com/media/photos/products/2024/9/4/kid_bicycle_10902180_1725420174_e927f4f2_progressive_thumbnail.jpg", twoDaysAgo, "Sporting Equipment", "Bad condition", "available", 2));
        itemArrayList.add(new Item(3, "Pink winter jacket", "Salvation Army", "https://media.karousell.com/media/photos/products/2024/8/28/winter_jacket_1724817890_9e33e160_progressive.jpg", threeDaysAgo, "Clothing", "Okay condition", "available", 3));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initItems();
        Log.d("CatalogueFragment", "Items size: " + itemArrayList.size());
        View rootView = inflater.inflate(R.layout.fragment_catalogue, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewItems);
        catalogueAdapter = new CatalogueAdapter(getContext(), itemArrayList);
        recyclerView.setAdapter(catalogueAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        catalogueAdapter.notifyDataSetChanged();

        return rootView;
    }
}