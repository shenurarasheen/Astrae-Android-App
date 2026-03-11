package com.synapse.astraemobileapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.model.Product;

import java.util.List;
import java.util.Locale;

public class RecentlyViewItemAdapter extends RecyclerView.Adapter<RecentlyViewItemAdapter.RecentlyViewItemViewHolder> {

    private List<Product> products;

    public RecentlyViewItemAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public RecentlyViewItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new RecentlyViewItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentlyViewItemViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productTitle.setText(product.getTitle());
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText(String.format(Locale.US, "LKR %,.2f", product.getPrice()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class RecentlyViewItemViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle, productDescription, productPrice;
        public RecentlyViewItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.item_product_image);
            productTitle = itemView.findViewById(R.id.item_product_title);
            productDescription = itemView.findViewById(R.id.item_product_description);
            productPrice = itemView.findViewById(R.id.item_product_price);
        }
    }
}
