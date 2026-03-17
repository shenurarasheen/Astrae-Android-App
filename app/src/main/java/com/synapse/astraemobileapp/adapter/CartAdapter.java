package com.synapse.astraemobileapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.model.CartItem;
import com.synapse.astraemobileapp.model.Product;
import com.synapse.astraemobileapp.repository.CartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    List<CartItem> cartItems;

    private FirebaseStorage storage;
    private CartRepository cartRepo;

    private Context context;

    private ItemRemoveCallback callback;

    private OnQuantityChangedListener quantityListener;
    private OnItemRemoveListener removeListener;

    public void setOnQuantityChangedListener(OnQuantityChangedListener listener) {
        this.quantityListener = listener;
    }

    public void setOnItemRemoveListener(OnItemRemoveListener listener) {
        this.removeListener = listener;
    }

    public CartAdapter(List<CartItem> cartItems, Context context, ItemRemoveCallback callback) {
        this.cartItems = cartItems;
        this.storage = FirebaseStorage.getInstance();
        this.context = context;
        this.cartRepo = new CartRepository(context);
        this.callback = callback;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        cartRepo.getCartItemProduct(item, product -> {
            loadProductImage(holder.cartItemImage, product.getProductImages().get(0));
            holder.itemTitle.setText(product.getTitle());
            holder.itemPrice.setText(String.format(Locale.US, "LKR %,.2f", product.getPrice()));
            loadProductBadges(product.getAttributes(), holder.badgeContainer);

            holder.minusBtn.setOnClickListener(v -> {
                decrementQty(product, item, holder);
            });

            holder.plusBtn.setOnClickListener(v -> {
                incrementQty(product, item, holder);
            });

            holder.itemQty.setText(String.valueOf(item.getQuantity()));

            holder.deleteBtn.setOnClickListener(v -> {
                cartRepo.deleteCartItem(item.getDocumentId());
                callback.callback(position);
                if (removeListener != null) {
                    removeListener.onRemoved();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }


    private void incrementQty(Product product, CartItem item, CartViewHolder holder) {
        if (product.getStockCount() > item.getQuantity()) {

            item.setQuantity(item.getQuantity() + 1);

            holder.itemQty.setText(String.valueOf(item.getQuantity()));
            holder.plusBtn.setEnabled(true);
            holder.minusBtn.setEnabled(true);

            cartRepo.addToCart(product, 1);
            if (quantityListener != null) {
                quantityListener.onChanged();
            }
        } else {
            holder.plusBtn.setEnabled(false);
            Toast.makeText(context, "Maximum quantity exceeded!", Toast.LENGTH_SHORT).show();
        }
    }

    private void decrementQty(Product product, CartItem item, CartViewHolder holder) {
        if (item.getQuantity() > 1) {

            item.setQuantity(item.getQuantity() - 1);

            holder.itemQty.setText(String.valueOf(item.getQuantity()));
            holder.plusBtn.setEnabled(true);
            holder.minusBtn.setEnabled(true);

            cartRepo.addToCart(product, -1);
            if (removeListener != null) {
                quantityListener.onChanged();
            }
        } else {
            holder.minusBtn.setEnabled(false);
            Toast.makeText(context, "Quantity can't be less than 1", Toast.LENGTH_SHORT).show();
        }
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

    static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView cartItemImage, deleteBtn;
        TextView itemTitle, itemPrice, plusBtn, minusBtn, itemQty;
        FlexboxLayout badgeContainer;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemImage = itemView.findViewById(R.id.item_cart_image);
            itemTitle = itemView.findViewById(R.id.item_cart_title);
            itemPrice = itemView.findViewById(R.id.item_cart_price);
            plusBtn = itemView.findViewById(R.id.item_cart_plus_qty_btn);
            minusBtn = itemView.findViewById(R.id.item_cart_minus_qty_btn);
            itemQty = itemView.findViewById(R.id.item_cart_qty);
            badgeContainer = itemView.findViewById(R.id.item_cart_badge_container);
            deleteBtn = itemView.findViewById(R.id.item_cart_delete);
        }
    }

    public interface ItemRemoveCallback{
        void callback(int position);
    }

    public interface OnQuantityChangedListener {
        void onChanged();
    }

    public interface OnItemRemoveListener {
        void onRemoved();
    }
}
