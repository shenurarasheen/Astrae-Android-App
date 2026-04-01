package com.synapse.astraemobileapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class RecentlyViewItemAdapter extends RecyclerView.Adapter<RecentlyViewItemAdapter.RecentlyViewItemViewHolder> {

    private List<Product> products;

    private FirebaseStorage storage;

    private int lastPosition = -1;

    public RecentlyViewItemAdapter(List<Product> products) {
        this.products = products;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public RecentlyViewItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new RecentlyViewItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentlyViewItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product product = products.get(position);
        loadProductImage(holder.productImage, product.getProductImages().get(0));
        holder.productTitle.setText(product.getTitle());
        loadProductBadges(product.getAttributes(), holder.badgeContainer);
        holder.productPrice.setText(String.format(Locale.US, "%,.2f", product.getPrice()));

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(
                    holder.itemView.getContext(),
                    R.anim.item_fall_down
            );
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
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

    static class RecentlyViewItemViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle, productPrice;
        FlexboxLayout badgeContainer;

        public RecentlyViewItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.item_product_image);
            productTitle = itemView.findViewById(R.id.item_product_title);
            productPrice = itemView.findViewById(R.id.item_product_price);
            badgeContainer = itemView.findViewById(R.id.item_product_badge_container);
        }
    }
}
