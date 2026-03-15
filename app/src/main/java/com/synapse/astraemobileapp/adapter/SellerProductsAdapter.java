package com.synapse.astraemobileapp.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.model.Product;

import java.util.List;

public class SellerProductsAdapter extends RecyclerView.Adapter<SellerProductsAdapter.SellerProductsViewHolder> {

    private List<Product> products;

    private FirebaseStorage storage;

    private SellerProductOnClickListener sellerProductListener;

    public SellerProductsAdapter(List<Product> products, SellerProductOnClickListener listener) {
        this.products = products;
        this.storage = FirebaseStorage.getInstance();
        this.sellerProductListener = listener;
    }

    @NonNull
    @Override
    public SellerProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_product, parent, false);
        return new SellerProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerProductsViewHolder holder, int position) {
        Product product = products.get(position);
        loadProductImage(holder.itemImage, product.getProductImages().get(0));

        holder.itemImage.setOnClickListener(v -> {
            sellerProductListener.onSellerProductClick(product);
        });

        holder.viewCount.setText(String.valueOf(product.getViewedCount()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private void loadProductImage(ShapeableImageView imageView, String imageUrl) {
        storage.getReference("/" + imageUrl)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(imageView.getContext())
                            .load(uri)
                            .centerCrop()
                            .into(imageView);
                });
    }

    static class SellerProductsViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView itemImage;
        TextView viewCount;
        public SellerProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.seller_product_item_image);
            viewCount = itemView.findViewById(R.id.seller_product_item_view_count);
        }
    }

    public interface SellerProductOnClickListener {
        void onSellerProductClick(Product product);
    }
}
