package com.synapse.astraemobileapp.adapter;

import android.content.Context;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.model.Order;
import com.synapse.astraemobileapp.model.Product;
import com.synapse.astraemobileapp.repository.OrderRepository;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.OrderItemsViewHolder> {

    List<Order.OrderItem> orderItems;

    private OrderRepository orderRepo;
    private Context context;
    private Map<String, Product> productMap;

    private FirebaseStorage storage;

    public OrderItemsAdapter(List<Order.OrderItem> orderItems, Map<String, Product> productMap, Context context) {
        this.orderItems = orderItems;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.productMap = productMap;
        orderRepo = new OrderRepository(context);
    }

    @NonNull
    @Override
    public OrderItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemsViewHolder holder, int position) {
        Order.OrderItem orderItem = orderItems.get(position);

        Product product = productMap.get(orderItem.getProductId());
        if (product == null) return;
        loadProductImage(holder.itemImage, product.getProductImages().get(0));
        holder.itemTitle.setText(product.getTitle());
        loadProductBadges(product.getAttributes(), holder.badgeContainer);
        holder.itemPrice.setText(String.format(Locale.US, "LKR %,.2f", product.getPrice()));
        holder.itemQuantity.setText("Qty: " + orderItem.getQuantity());

//        orderRepo.getProductFromId(productId, product -> {
//            if (product == null) return;
//            loadProductImage(holder.itemImage, product.getProductImages().get(0));
//            holder.itemTitle.setText(product.getTitle());
//            loadProductBadges(product.getAttributes(), holder.badgeContainer);
//            holder.itemPrice.setText(String.format(Locale.US, "LKR %,.2f", product.getPrice()));
//            holder.itemQuantity.setText("Qty: " + orderItem.getQuantity());
//
//        });
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

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class OrderItemsViewHolder extends RecyclerView.ViewHolder {

        ShapeableImageView itemImage;

        FlexboxLayout badgeContainer;
        TextView itemTitle, itemPrice, itemQuantity;
        public OrderItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.item_order_image);
            itemTitle = itemView.findViewById(R.id.item_order_title);
            itemPrice = itemView.findViewById(R.id.item_order_price);
            itemQuantity = itemView.findViewById(R.id.item_order_quantity);
            badgeContainer = itemView.findViewById(R.id.item_order_badge_container);
        }
    }
}
