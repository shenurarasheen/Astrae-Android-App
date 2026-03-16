package com.synapse.astraemobileapp.repository;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.synapse.astraemobileapp.activity.SignInActivity;
import com.synapse.astraemobileapp.fragment.ListingFragment;
import com.synapse.astraemobileapp.model.CartItem;
import com.synapse.astraemobileapp.model.Product;

public class CartRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final Context context;

    public CartRepository(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.context = context;
    }

    public void addToCart(Product product, int qty) {

        if (auth.getCurrentUser() != null) {
            String uId = auth.getCurrentUser().getUid();
            if (qty <= product.getStockCount()) {
                // check if same product already remaining in the cart
                checkCartItemAvailability(uId, product.getProductId(), (isAvailable, ds) -> {

                    if (isAvailable && ds != null) {
                        CartItem cartItem = ds.toObject(CartItem.class);
                        int subQty = qty + cartItem.getQuantity();
                        if (subQty < product.getStockCount()) {
                            ds.getReference().update("quantity", subQty)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(context, "Cart updated successfully!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to update cart!", Toast.LENGTH_SHORT).show();
                                    });
                            return;
                        }

                        Toast.makeText(context, "Maximum quantity of the product exceeded", Toast.LENGTH_SHORT).show();

                    }

                    if (!isAvailable && ds == null) {
                        db.collection("users").document(uId).collection("cart")
                                .add(new CartItem(product.getProductId(), qty))
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Product added to the cart successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ListingFragment", "Failed to add to cart");
                                });
                        return;
                    }

                    Toast.makeText(context, "Unknown error occurred.", Toast.LENGTH_SHORT).show();

                });
                return;
            }

            Toast.makeText(context, "Maximum quantity exceeded", Toast.LENGTH_SHORT).show();

        } else {
            moveToSignIn();
        }
    }

    private void checkCartItemAvailability(String uId, String productId, FirebaseCallback firebaseCallback) {
        db.collection("users").document(uId).collection("cart")
                .whereEqualTo("productId", productId)
                .get()
                .addOnSuccessListener(qs -> {
                    if (!qs.isEmpty()) {
                        firebaseCallback.callback(true, qs.getDocuments().get(0));
                    } else {
                        firebaseCallback.callback(false, null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ListingFragment", "Failed to check cart availability");
                    Toast.makeText(context, "Failed to check cart availability", Toast.LENGTH_SHORT).show();
                });
    }

    private void moveToSignIn() {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }

    public interface FirebaseCallback {
        void callback(boolean isAvailable, DocumentSnapshot ds);
    }

}
