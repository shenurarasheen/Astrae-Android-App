package com.synapse.astraemobileapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.model.Product;

import java.util.List;
import java.util.Locale;

public class SellerItemsAdapter extends RecyclerView.Adapter<SellerItemsAdapter.SellerItemsViewHolder> {

    private List<Product> sellerItems;
    private FirebaseStorage storage;

    public SellerItemsAdapter(List<Product> sellerItems) {
        this.sellerItems = sellerItems;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public SellerItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selling, parent, false);
        return new SellerItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerItemsViewHolder holder, int position) {
        Product product = sellerItems.get(position);
        loadProductImage(holder.productImage, product.getProductImages().get(0));
        holder.title.setText(product.getTitle());
        holder.description.setText(product.getDescription());
        holder.price.setText(String.format(Locale.US, "LKR %,.2f", product.getPrice()));
        holder.status.setText(product.getStatus());
    }

    @Override
    public int getItemCount() {
        return sellerItems.size();
    }

    private void loadProductImage(ImageView imageView, String imageUrl) {
        storage.getReference("/" + imageUrl)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(imageView.getContext())
                            .load(uri)
                            .centerCrop()
                            .into(imageView);
                });
    }

    static class SellerItemsViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView title, description, price, status;

        public SellerItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.item_seller_product_image);
            title = itemView.findViewById(R.id.item_seller_product_title);
            description = itemView.findViewById(R.id.item_seller_product_description);
            price = itemView.findViewById(R.id.item_seller_product_price);
            status = itemView.findViewById(R.id.item_seller_product_status);
        }
    }
}
