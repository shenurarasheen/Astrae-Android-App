package com.synapse.astraemobileapp.repository;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.synapse.astraemobileapp.model.Category;
import com.synapse.astraemobileapp.model.Product;
import com.synapse.astraemobileapp.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRepository {

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Context context;

    public ProductRepository(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    public void getAllCategories(CategoryCallback callback) {
        db.collection("categories")
                .get()
                .addOnSuccessListener(qds -> {
                    List<Category> categories = qds.toObjects(Category.class);
                    Map<String, Category> categoryMap = new HashMap<>();
                    for (Category category : categories) {
                        categoryMap.put(category.getName(), category);
                    }
                    callback.onCategoriesLoaded(categoryMap);
                })
                .addOnFailureListener(e -> Log.e("ProductRepository", "Error fetching categories", e));
    }


    public void getNewProductId(NewProductIdCallback callback) {
        db.collection("products")
                .orderBy("productId", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String nextId;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String lastId = queryDocumentSnapshots.getDocuments().get(0).getString("productId");
                        nextId = generateNextProductId(lastId);
                    } else {
                        nextId = "PROD001";
                    }

                    callback.onNewProductIdGenerated(nextId);
                });
    }

    private String generateNextProductId(String lastId) {
        if (lastId == null || !lastId.startsWith("PROD")) {
            return "PROD001";
        }

        String numberPart = lastId.substring(4);

        int nextNumber = Integer.parseInt(numberPart) + 1;

        return String.format("PROD%03d", nextNumber);
    }

    public void saveProduct(Product product) {
        db.collection("products")
                .add(product)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ProductRepository", "Product saved successfully");
                    Toast.makeText(context, "Product saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductRepository", "Error saving product", e);
                    Toast.makeText(context, "Error saving product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    public void getAllSellerProducts(String sellerId, ProductListCallback callback) {
        db.collection("products")
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnSuccessListener(qds -> {
                    List<Product> products = qds.toObjects(Product.class);
                    callback.onProductListLoaded(products);
                })
                .addOnFailureListener(e -> Log.e("ProductRepository", "Error fetching seller products", e));
    }


    public void isDataFilled(String uId, DataFilledCallback callback) {
        db.collection("users").document(uId)
                .get()
                .addOnSuccessListener(ds -> {
                    User user = ds.toObject(User.class);
                    if (user == null) {
                        callback.callback(false);
                        return;
                    }
                    String addressId = user.getPermanentAddressId();
                    Map<String, Double> location = user.getLocation();

                    if (addressId == null || addressId.isEmpty() || location == null) {
                        callback.callback(false);
                        return;
                    }
                    callback.callback(true);
                })
                .addOnFailureListener(e -> Log.e("ProductRepository", "Error in isDataFilled", e));
    }


    public interface ProductListCallback {
        void onProductListLoaded(List<Product> products);
    }


    public interface CategoryCallback {
        void onCategoriesLoaded(Map<String, Category> categoryMap);
    }

    public interface NewProductIdCallback {
        void onNewProductIdGenerated(String newProductId);
    }

    public interface DataFilledCallback {
        void callback(boolean isFilled);
    }

}
