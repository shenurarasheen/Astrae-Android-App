package com.synapse.astraemobileapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.model.Order;
import com.synapse.astraemobileapp.repository.OrderRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private Context context;

    private OrderRepository orderRepo;

    public OrderAdapter(List<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
        this.orderRepo = new OrderRepository(context);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_container, parent, false);
        return new OrderViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        RecyclerView recyclerView = holder.itemView.findViewById(R.id.item_order_container_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        holder.itemView.setTag(order.getOrderId());

        List<Order.OrderItem> itemsInThisOrder = order.getOrderItems();

        if (itemsInThisOrder != null && !itemsInThisOrder.isEmpty()) {
            orderRepo.getProductsForOrderItems(itemsInThisOrder, productsMap -> {
                if (holder.itemView.getTag().equals(order.getOrderId())) {
                    OrderItemsAdapter adapter = new OrderItemsAdapter(itemsInThisOrder, productsMap, context);
                    recyclerView.setAdapter(adapter);
                }
            });
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd, MMM yyyy", Locale.getDefault());
        String estDateString = sdf.format(new Date(order.getDeliveryDate()));

        if (order.getStatus().equalsIgnoreCase("DELIVERED")) {
            holder.deliverDate.setText("Delivered on " + estDateString);
        } else {
            holder.deliverDate.setText("Est Delivery on " + estDateString);
        }



        orderRepo.getAddressFromId(order.getAddressId(), address -> {
            holder.deliverAddress.setText(
                    address.getFirstName() + ", "
                            + address.getLastName() + ", "
                            + address.getAddressLine1() + ", "
                            + address.getAddressLine2() + ", "
                            + address.getCity() + ", "
                            + address.getPostalCode() + ", "
                            + address.getMobile()
            );
        });
        holder.orderStatus.setText(order.getStatus());
        changeStatusBadge(holder.orderStatus, order.getStatus());
    }

    private void changeStatusBadge(TextView statusTextView, String status) {
        switch (status.toUpperCase()) {
            case "PAID":
                statusTextView.setBackgroundResource(R.drawable.status_paid_bg);
                statusTextView.setTextColor(ContextCompat.getColor(context, R.color.paid_status_text_color));
                break;
            case "PENDING":
                statusTextView.setBackgroundResource(R.drawable.status_pending_bg);
                statusTextView.setTextColor(ContextCompat.getColor(context, R.color.pending_status_text_color));
                break;
            case "PROCESSING":
                statusTextView.setBackgroundResource(R.drawable.status_processing_bg);
                statusTextView.setTextColor(ContextCompat.getColor(context, R.color.processing_status_text_color));
                break;
            case "SHIPPED":
                statusTextView.setBackgroundResource(R.drawable.status_shipped_bg);
                statusTextView.setTextColor(ContextCompat.getColor(context, R.color.shipped_status_text_color));
                break;
            case "OUT FOR DELIVERY":
                statusTextView.setBackgroundResource(R.drawable.status_out_for_delivery_bg);
                statusTextView.setTextColor(ContextCompat.getColor(context, R.color.out_for_delivery_status_text_color));
                break;
            case "DELIVERED":
                statusTextView.setBackgroundResource(R.drawable.status_delivered_bg);
                statusTextView.setTextColor(ContextCompat.getColor(context, R.color.delivered_status_text_color));
                break;
            default:
                statusTextView.setBackgroundResource(R.drawable.status_paid_bg);
                statusTextView.setTextColor(ContextCompat.getColor(context, R.color.paid_status_text_color));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView deliverDate, deliverAddress, orderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            deliverDate = itemView.findViewById(R.id.item_order_container_delivery_date);
            deliverAddress = itemView.findViewById(R.id.item_order_container_address);
            orderStatus = itemView.findViewById(R.id.item_order_container_status);
        }
    }
}
