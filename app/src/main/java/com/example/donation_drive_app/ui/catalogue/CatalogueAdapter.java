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

import java.util.ArrayList;


public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Item> itemsArrayList;

    // Constructor
    public CatalogueAdapter(Context context, ArrayList<Item> itemsArrayList) {
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_items_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int index1 = position * 2;
        int index2 = index1 + 1;

        // Display first item
        Item item1 = itemsArrayList.get(index1);
        holder.textViewItem1Name.setText(item1.getName());
        holder.textViewItem1HostName.setText(item1.getHostName());
        holder.textViewItem1Category.setText(item1.getCategory());
        holder.textViewItem1Date.setText(item1.getUploadTime());
        Glide.with(context).load(R.drawable.kid_bicycle).into(holder.imageViewItem1);
        //later change to get directly from the backend url

        // Check if second item exists
        if (index2 < itemsArrayList.size()) {
            Item item2 = itemsArrayList.get(index2);
            holder.textViewItem2Name.setText(item2.getName());
            holder.textViewItem2HostName.setText(item2.getHostName());
            holder.textViewItem2Category.setText(item2.getCategory());
            holder.textViewItem2Date.setText(item2.getUploadTime());
            Glide.with(context).load(R.drawable.ipad).into(holder.imageViewItem2);
            //later change to get directly from the backendurl
        } else {
            // Hide the second card if there is no second item
            holder.cardViewItem2.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        // Return the number of rows in the RecyclerView
        return (itemsArrayList.size() + 1) / 2;
    }

    // ViewHolder class for initializing views such as TextView and ImageView
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
    }
}
