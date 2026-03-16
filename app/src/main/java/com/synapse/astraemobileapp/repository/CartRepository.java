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

import java.util.List;

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

        if (auth.getCurrentUser() == null) {
            moveToSignIn();
            return;
        }

        if (qty > product.getStockCount()) {
            Toast.makeText(context, "Maximum quantity exceeded", Toast.LENGTH_SHORT).show();
            return;
        }

        String uId = auth.getCurrentUser().getUid();

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


    public void getAllCartItems(CartItemsCallback callback) {

        if (auth.getCurrentUser() == null) {
            moveToSignIn();
            return;
        }

        String uId = auth.getCurrentUser().getUid();
        db.collection("users").document(uId).collection("cart")
                .get()
                .addOnSuccessListener(qds -> {
                    List<CartItem> cartItems = qds.toObjects(CartItem.class);
                    callback.callback(cartItems);

//                    if (cartItems.isEmpty()) {
//                        Toast.makeText(context, "No cart items available", Toast.LENGTH_SHORT).show();
//                        return;
//                    }

//                    List<String> productIds = new ArrayList<>();
//                    cartItems.forEach(item -> {
//                        productIds.add(item.getProductId());
//                    });

//                    db.collection("products")
//                            .whereIn("productId", productIds)
//                            .get()
//                            .addOnSuccessListener(qs -> {
//                                List<Product> products = qs.toObjects(Product.class);
//                                callback.callback(products);
//                            })
//                            .addOnFailureListener(e -> Log.e("CartRepository", "Failed to get products from cart items. " + e));
                })
                .addOnFailureListener(e -> Log.e("CartRepository", "Failed to retrieve cart items."));
    }

    public void getCartItemProduct(CartItem item, ItemCallback callback) {
        db.collection("products")
                .whereEqualTo("productId", item.getProductId())
                .get()
                .addOnSuccessListener(qds -> {
                    Product product = qds.toObjects(Product.class).get(0);
                    callback.callback(product);
                })
                .addOnFailureListener(e -> Log.e("CartRepository", "Failed to retrieve product from cart item " +e));
    }

    public void deleteCartItem(String documentId) {
        if (auth.getCurrentUser() == null) {
            moveToSignIn();
            return;
        }
        String uId = auth.getCurrentUser().getUid();
        db.collection("users").document(uId).collection("cart")
                .document(documentId)
                .delete()
                .addOnCompleteListener(task-> {
                    if (task.isSuccessful()) {
                        Log.i("CartRepository", "Cart item deleted successfully");
                    } else {
                        Log.e("CartRepository", "Failed to delete cart item");
                    }
                });
    }


    private void moveToSignIn() {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }

    public interface FirebaseCallback {
        void callback(boolean isAvailable, DocumentSnapshot ds);
    }

    public interface CartItemsCallback {
        void callback(List<CartItem> items);
    }

    public interface ItemCallback {
        void callback(Product product);
    }

}
