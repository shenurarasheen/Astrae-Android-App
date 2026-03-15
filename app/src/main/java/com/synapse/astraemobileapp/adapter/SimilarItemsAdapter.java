package com.synapse.astraemobileapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.model.Product;

import java.util.List;
import java.util.Locale;

public class SimilarItemsAdapter extends RecyclerView.Adapter<SimilarItemsAdapter.SimilarItemViewHolder> {

    private List<Product> products;

    private FirebaseStorage storage;

    private SimilarProductOnClickListener listener;

    public SimilarItemsAdapter(List<Product> products, SimilarProductOnClickListener listener) {
        this.products = products;
        this.storage = FirebaseStorage.getInstance();
        this.listener = listener;
    }

    @NonNull
    @Override
    public SimilarItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new SimilarItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarItemViewHolder holder, int position) {
        Product product = products.get(position);
        loadProductImage(holder.productImage, product.getProductImages().get(0));
        holder.productTitle.setText(product.getTitle());
        loadProductBadges(product.getAttributes(), holder.badgeContainer);
        holder.productPrice.setText(String.format(Locale.US, "%,.2f", product.getPrice()));

        // attach listener for product image
        holder.productImage.setOnClickListener(v -> {
            listener.onSimilarProductClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
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

    //need to set badges for product card here...
    private void loadProductBadges(List<Product.Attribute> attributes, FlexboxLayout badgeContainer) {

        badgeContainer.removeAllViews();

        if (attributes == null || attributes.isEmpty()) return;

        LayoutInflater inflater = LayoutInflater.from(badgeContainer.getContext());

        for (Product.Attribute attribute : attributes) {
            FrameLayout productBadge = (FrameLayout) inflater.inflate(R.layout.item_product_badge, null);
            TextView badgeText = productBadge.findViewById(R.id.item_product_badge);
            badgeText.setText(attribute.getValues().get(0));
            badgeContainer.addView(productBadge);
        }
    }

    static class SimilarItemViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle, productPrice;
        FlexboxLayout badgeContainer;

        public SimilarItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.item_product_image);
            productTitle = itemView.findViewById(R.id.item_product_title);
            productPrice = itemView.findViewById(R.id.item_product_price);
            badgeContainer = itemView.findViewById(R.id.item_product_badge_container);
        }
    }

    public interface SimilarProductOnClickListener {
        void onSimilarProductClick(Product product);
    }
}
