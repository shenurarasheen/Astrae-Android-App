package com.synapse.astraemobileapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.model.Order;
import com.synapse.astraemobileapp.repository.OrderRepository;

import java.util.List;

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
