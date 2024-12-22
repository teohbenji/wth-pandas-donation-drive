package com.example.donation_drive_app.ui.wishlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donation_drive_app.R;
import com.example.donation_drive_app.api.Item;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private ArrayList<Item> itemsArrayList;

    public WishlistAdapter(ArrayList<Item> itemsArrayList) {
        this.itemsArrayList = itemsArrayList;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wishlist_item, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        Item item = itemsArrayList.get(position);
        //later set text individually
        holder.nameTextView.setText(item.getName());
        holder.hostNameTextView.setText(item.getHostName());
        holder.categoryTextView.setText(item.getCategory());
        holder.dateTextView.setText(item.getUploadTime());

        // Load image using Glide
        String imagePath1 = item.getPhotoString();
        FirebaseStorage.getInstance().getReference(imagePath1)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> Glide.with(holder.itemView.getContext())
                        .load(uri.toString())
                        .into(holder.itemImageView)
                );

    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView nameTextView;
        TextView hostNameTextView;
        TextView categoryTextView;
        TextView dateTextView;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            hostNameTextView = itemView.findViewById(R.id.hostNameTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}