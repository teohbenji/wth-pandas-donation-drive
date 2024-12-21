package com.example.donation_drive_app.ui.catalogue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donation_drive_app.R;
import com.example.donation_drive_app.api.Item;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Item> itemsArrayList;
    private final OnItemClickListener onItemClickListener;

    // Constructor
    public CatalogueAdapter(Context context, ArrayList<Item> itemsArrayList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.itemsArrayList = itemsArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_items_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int index1 = position * 2;
        int index2 = index1 + 1;

        // Display first item
        Item item1 = itemsArrayList.get(index1);
        holder.bind(item1, onItemClickListener);

        // Check if second item exists
        if (index2 < itemsArrayList.size()) {
            Item item2 = itemsArrayList.get(index2);
            holder.bindSecondItem(item2, onItemClickListener);
            holder.cardViewItem2.setVisibility(View.VISIBLE);
        } else {
            holder.cardViewItem2.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (itemsArrayList.size() + 1) / 2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardViewItem1;
        private final ImageView imageViewItem1;
        private final TextView textViewItem1Name;
        private final TextView textViewItem1HostName;
        private final TextView textViewItem1Category;
        private final TextView textViewItem1Date;

        private final CardView cardViewItem2;
        private final ImageView imageViewItem2;
        private final TextView textViewItem2Name;
        private final TextView textViewItem2HostName;
        private final TextView textViewItem2Category;
        private final TextView textViewItem2Date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewItem1 = itemView.findViewById(R.id.cardViewItem1);
            imageViewItem1 = itemView.findViewById(R.id.imageViewItem1);
            textViewItem1Name = itemView.findViewById(R.id.textViewItem1Name);
            textViewItem1HostName = itemView.findViewById(R.id.textViewItem1HostName);
            textViewItem1Category = itemView.findViewById(R.id.textViewItem1Category);
            textViewItem1Date = itemView.findViewById(R.id.textViewItem1Date);

            cardViewItem2 = itemView.findViewById(R.id.cardViewItem2);
            imageViewItem2 = itemView.findViewById(R.id.imageViewItem2);
            textViewItem2Name = itemView.findViewById(R.id.textViewItem2Name);
            textViewItem2HostName = itemView.findViewById(R.id.textViewItem2HostName);
            textViewItem2Category = itemView.findViewById(R.id.textViewItem2Category);
            textViewItem2Date = itemView.findViewById(R.id.textViewItem2Date);
        }

        public void bind(Item item, OnItemClickListener listener) {
            // Load item1 data
            textViewItem1Name.setText(item.getName());
            textViewItem1HostName.setText(item.getHostName());
            textViewItem1Category.setText(item.getCategory());
            textViewItem1Date.setText(item.getUploadTime());
            cardViewItem1.setOnClickListener(v -> listener.onItemClick(item));

            // Load image using Glide
            String imagePath1 = item.getPhotoString();
            FirebaseStorage.getInstance().getReference(imagePath1)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> Glide.with(itemView.getContext())
                            .load(uri.toString())
                            .into(imageViewItem1)
                    );
        }

        public void bindSecondItem(Item item, OnItemClickListener listener) {
            // Load item2 data
            textViewItem2Name.setText(item.getName());
            textViewItem2HostName.setText(item.getHostName());
            textViewItem2Category.setText(item.getCategory());
            textViewItem2Date.setText(item.getUploadTime());
            cardViewItem2.setOnClickListener(v -> listener.onItemClick(item));

            // Load image using Glide
            String imagePath2 = item.getPhotoString();
            FirebaseStorage.getInstance().getReference(imagePath2)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> Glide.with(itemView.getContext())
                            .load(uri.toString())
                            .into(imageViewItem2));
        }
    }

    // Click listener interface
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }
}

