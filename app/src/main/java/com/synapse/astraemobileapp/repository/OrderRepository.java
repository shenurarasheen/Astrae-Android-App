package com.synapse.astraemobileapp.repository;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.model.Address;
import com.synapse.astraemobileapp.model.Order;
import com.synapse.astraemobileapp.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private Context context;

    public OrderRepository(Context context) {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
        this.context = context;
    }

    public void getUserOrders(OrderCallback callback) {

        if (auth.getCurrentUser() == null) {
            return;
        }
        String uId = auth.getCurrentUser().getUid();
        db.collection("orders")
                .whereEqualTo("userId", uId)
                .get()
                .addOnSuccessListener(qds -> {
                    List<Order> orders = qds.toObjects(Order.class);
                    callback.callback(orders);
                })
                .addOnFailureListener(e -> Log.e("OrderRepository", "Error fetching orders ", e));
    }


    public void getOrderItems(OrderItemsCallback callback) {
        getUserOrders(orders -> {
            for (Order order: orders) {
                db.collection("orders").document(order.getOrderId())
                        .get()
                        .addOnSuccessListener(ds -> {
                            if (ds.exists()) {
                                Order o = ds.toObject(Order.class);
                                if (o != null) {
                                    List<Order.OrderItem> orderItems = o.getOrderItems();
                                    callback.callback(orderItems);
                                }
                            }
                        })
                        .addOnFailureListener(e -> Log.e("OrderRepository", "Error fetching order items ", e));
            }
        });
    }

    public void getProductFromId(String productId, ProductCallback callback) {
        db.collection("products")
                .whereEqualTo("productId", productId)
                .get()
                .addOnSuccessListener(qds -> {
                    Product product = qds.toObjects(Product.class).get(0);
                    callback.callback(product);
                })
                .addOnFailureListener(e -> Log.e("OrderRepository", "Error fetching product ", e));
    }

    public void getAddressFromId(String addressId, AddressCallback callback) {
        if (auth.getCurrentUser() == null) {
            return;
        }
        String uId = auth.getCurrentUser().getUid();

        db.collection("users").document(uId)
                .collection("addresses")
                .document(addressId)
                .get()
                .addOnSuccessListener(ds -> {
                    if (ds == null) return;
                    Address address = ds.toObject(Address.class);
                    callback.callback(address);
                })
                .addOnFailureListener(e -> Log.e("OrderRepository", "Error fetching address ", e));
    }

    public void getProductsForOrderItems(List<Order.OrderItem> orderItems, ProductsMapCallback callback) {
        List<String> productIds = new ArrayList<>();
        for (Order.OrderItem item: orderItems) {
            productIds.add(item.getProductId());
        }
        getProductsForOrder(productIds, callback);
    }

    public void getProductsForOrder(List<String> ids, ProductsMapCallback callback) {
        db.collection("products").whereIn("productId", ids).get()
                .addOnSuccessListener(qds -> {
                    Map<String, Product> map = new HashMap<>();
                    for (DocumentSnapshot ds : qds.getDocuments()) {
                        Product p = ds.toObject(Product.class);
                        if (p != null) map.put(p.getProductId(), p);
                    }
                    callback.onLoaded(map);
                });
    }


    public interface OrderCallback {
        void callback(List<Order> orders);
    }

    public interface OrderItemsCallback {
        void callback(List<Order.OrderItem> orderItems);
    }

    public interface ProductCallback {
        void callback(Product product);
    }

    public interface AddressCallback {
        void callback(Address address);
    }

    public interface ProductsMapCallback {
        void onLoaded(Map<String, Product> productsMap);
    }
}
