package com.synapse.astraemobileapp.repository;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.synapse.astraemobileapp.model.CartItem;
import com.synapse.astraemobileapp.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            for (CartItem item: items) {
                productIds.add(item.getProductId());
            }

            db.collection("products")
                    .whereIn("productId", productIds)
                    .get()
                    .addOnSuccessListener(qds -> {
                        Map<String, Product> productCache = new HashMap<>();
                        for (DocumentSnapshot ds: qds.getDocuments()) {
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
            for (CartItem item: cartItems) {
                Product p = map.get(item.getProductId());
                if (p == null) return;
                total += item.getQuantity() * p.getPrice();
            }
            callback.callback(total);
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
}
