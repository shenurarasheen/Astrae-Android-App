package com.synapse.astraemobileapp.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.synapse.astraemobileapp.model.CartItem;
import com.synapse.astraemobileapp.model.Order;
import com.synapse.astraemobileapp.model.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.payhere.androidsdk.model.StatusResponse;

public class PaymentRepository {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Context context;

    private double total;

    private List<CartItem> cartItems = new ArrayList<>();

    public PaymentRepository(Context context) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        this.context = context;
    }

    private void getCartItems(CartProductCallback callback) {

        if (auth.getCurrentUser() == null) return;

        db.collection("users").document(auth.getCurrentUser().getUid())
                .collection("cart")
                .get()
                .addOnSuccessListener(qds -> {
                    List<CartItem> items = qds.toObjects(CartItem.class);
                    callback.callback(items);
                })
                .addOnFailureListener(e -> Log.e("PaymentRepository", "Failed to get cart total"));
    }

    private void cacheProductMap(CacheProductCallback callback) {
        getCartItems(items -> {
            cartItems = items;
            List<String> productIds = new ArrayList<>();
            for (CartItem item : items) {
                productIds.add(item.getProductId());
            }

            db.collection("products")
                    .whereIn("productId", productIds)
                    .get()
                    .addOnSuccessListener(qds -> {
                        Map<String, Product> productCache = new HashMap<>();
                        for (DocumentSnapshot ds : qds.getDocuments()) {
                            Product p = ds.toObject(Product.class);
                            if (p == null) return;
                            productCache.put(p.getProductId(), p);
                        }
                        callback.callback(productCache);
                    });
        });
    }

    public void calculateTotal(CartItemTotalCallback callback) {
        total = 0;
        cacheProductMap(map -> {
            for (CartItem item : cartItems) {
                Product p = map.get(item.getProductId());
                if (p == null) return;
                total += item.getQuantity() * p.getPrice();
            }
            callback.callback(total);
        });
    }


    // save order to firestore
    public void placeOrder(StatusResponse statusResponse, double total, String addressId, OrderPlacementCallback callback) {
        if (auth.getCurrentUser() == null) return;

        //get order items
        cacheProductMap(map -> {

            List<Order.OrderItem> orderItems = new ArrayList<>();
            for (CartItem item : cartItems) {
                Product p = map.get(item.getProductId());
                if (p == null) return;
                orderItems.add(
                        Order.OrderItem.builder()
                                .productId(item.getProductId())
                                .unitPrice(p.getPrice())
                                .quantity(item.getQuantity())
                                .build()
                );
            }

            Order order = Order.builder()
                    .userId(auth.getCurrentUser().getUid())
                    .totalAmount(total)
                    .status("PAID")
                    .orderDate(Timestamp.now().toDate().getTime())
                    .orderItems(orderItems)
                    .addressId(addressId)
                    .statusResponse(statusResponse)
                    .build();

            db.collection("orders")
                    .add(order)
                    .addOnSuccessListener(dr -> {

                        // decrease stock quantity
                        for (Order.OrderItem item : orderItems) {
                            db.collection("products")
                                    .whereEqualTo("productId", item.getProductId())
                                    .get()
                                    .addOnSuccessListener(qs -> {
                                        DocumentSnapshot ds = qs.getDocuments().get(0);
                                        Product p = ds.toObject(Product.class);
                                        if (p == null) return;
                                        //int newStock = p.getStockCount() - item.getQuantity();
                                        ds.getReference().update("stockCount", FieldValue.increment(-item.getQuantity()), "soldCount", FieldValue.increment(item.getQuantity()));
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("PaymentRepository", "Failed to update stock quantity " + e);
                                        callback.onFailure("Failed to update product stock after placing order.");
                                    });
                        }

                        //delete cart items
                        for (CartItem item : cartItems) {
                            db.collection("users").document(auth.getCurrentUser().getUid())
                                    .collection("cart")
                                    .whereEqualTo("productId", item.getProductId())
                                    .get()
                                    .addOnSuccessListener(qds2 -> {
                                        for (DocumentSnapshot ds : qds2.getDocuments()) {
                                            ds.getReference().delete();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("PaymentRepository", "Failed to delete cart item " + e);
                                        callback.onFailure("Failed to clear cart after placing order.");
                                    });
                        }

                        callback.onSuccess();

                    })
                    .addOnFailureListener(e -> Log.e("PaymentRepository", "Failed to place order " + e));

        });

    }

    // overload placeOrder for single product purchase
    public void placeOrder(StatusResponse statusResponse, double total, String addressId, String productId, int quantity, OrderPlacementCallback callback) {
        if (auth.getCurrentUser() == null) return;

        Order.OrderItem orderItem = Order.OrderItem.builder()
                .productId(productId)
                .unitPrice(total / quantity)
                .quantity(quantity)
                .build();

        Order order = Order.builder()
                .userId(auth.getCurrentUser().getUid())
                .totalAmount(total)
                .status("PAID")
                .orderDate(Timestamp.now().toDate().getTime())
                .orderItems(Arrays.asList(orderItem))
                .addressId(addressId)
                .statusResponse(statusResponse)
                .build();

        db.collection("orders")
                .add(order)
                .addOnSuccessListener(dr -> {

                    db.collection("products")
                            .whereEqualTo("productId", productId)
                            .get()
                            .addOnSuccessListener(qs -> {
                                DocumentSnapshot ds = qs.getDocuments().get(0);
                                Product p = ds.toObject(Product.class);

                                if (p == null) return;
                                ds.getReference().update("stockCount", FieldValue.increment(-quantity), "soldCount", FieldValue.increment(quantity));

                                callback.onSuccess();

                            })
                            .addOnFailureListener(e -> {
                                Log.e("PaymentRepository", "Failed to update stock quantity " + e);
                                callback.onFailure("Failed to update product stock after placing order.");
                            });

                })
                .addOnFailureListener(e -> {
                    Log.e("PaymentRepository", "Failed to place order " + e);
                    callback.onFailure("Failed to place order. Please try again.");
                });
    }


    public interface CartProductCallback {
        void callback(List<CartItem> items);
    }

    public interface CacheProductCallback {
        void callback(Map<String, Product> map);
    }

    public interface CartItemTotalCallback {
        void callback(double total);
    }

    public interface OrderPlacementCallback {
        void onSuccess();

        void onFailure(String errorMessage);
    }
}
