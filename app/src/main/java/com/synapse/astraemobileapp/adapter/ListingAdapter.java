package com.synapse.astraemobileapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.activity.PaymentDetailsActivity;
import com.synapse.astraemobileapp.model.Product;
import com.synapse.astraemobileapp.repository.CartRepository;

import java.util.List;
import java.util.Locale;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {

    private List<Product> products;
    private FirebaseStorage storage;

    private OnProductClickListener productListner;

    private CartRepository cartRepo;

    private static final int SINGLE_PRODUCT_QUANTITY = 1;

    private Context context;

    public ListingAdapter(List<Product> products, Context context, OnProductClickListener productListener){
        this.products = products;
        this.productListner = productListener;
        this.storage = FirebaseStorage.getInstance();
        this.context = context;
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);

        this.cartRepo = new CartRepository(parent.getContext());

        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        Product product = products.get(position);
        loadProductImage(holder.productImage, product.getProductImages().get(0));
        holder.productTitle.setText(product.getTitle());
        loadProductBadges(product.getAttributes(), holder.badgeContainer);
        holder.productPrice.setText(String.format(Locale.US, "%,.2f", product.getPrice()));

        //set click listener for product card image
        holder.productImage.setOnClickListener(view -> {
            productListner.onProductClick(product);
        });

        holder.addToCart.setOnClickListener(view -> {
            cartRepo.addToCart(product, ListingAdapter.SINGLE_PRODUCT_QUANTITY);
        });

        holder.payBtn.setOnClickListener(view -> {
            if (product.getStockCount() <= 0) {
                Toast.makeText(view.getContext(), "Sorry, this product is out of stock.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, PaymentDetailsActivity.class);
            intent.putExtra("productId", product.getProductId());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("quantity", SINGLE_PRODUCT_QUANTITY);
            context.startActivity(intent);
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

    static class ListingViewHolder extends RecyclerView.ViewHolder {

        TextView productTitle, productPrice;
        ImageView productImage, addToCart;
        FlexboxLayout badgeContainer;

        Button payBtn;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);

            productTitle = itemView.findViewById(R.id.item_product_title);
            productImage = itemView.findViewById(R.id.item_product_image);
            productPrice = itemView.findViewById(R.id.item_product_price);
            badgeContainer = itemView.findViewById(R.id.item_product_badge_container);
            addToCart = itemView.findViewById(R.id.item_product_add_to_cart);
            payBtn = itemView.findViewById(R.id.item_product_pay_btn);
        }
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

}
