package com.synapse.astraemobileapp.repository;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.synapse.astraemobileapp.model.Category;
import com.synapse.astraemobileapp.model.Product;

import java.util.List;

public class HomeRepository {

    private FirebaseFirestore db;

    public HomeRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void getAllCategories(CategoriesCallback callback) {
        db.collection("categories")
                .get()
                .addOnSuccessListener(qds -> {
                    List<Category> categories = qds.toObjects(Category.class);
                    callback.onCategoriesFetched(categories);
                })
                .addOnFailureListener(e -> Log.e("HomeRepository", "Error fetching categories", e));
    }

    public void getNewProducts(ProductsCallback callback) {
        db.collection("products")
                .whereEqualTo("status", "APPROVED")
                .orderBy("productId", Query.Direction.DESCENDING)
                .limit(8)
                .get()
                .addOnSuccessListener(qds -> {
                    List<Product> products = qds.toObjects(Product.class);
                    callback.onProductsFetched(products);
                })
                .addOnFailureListener(e -> Log.e("HomeRepository", "Error fetching new products", e));
    }

    public void getAllProducts(ProductsCallback callback) {
        db.collection("products")
                .whereEqualTo("status", "APPROVED")
                .get()
                .addOnSuccessListener(qds -> {
                    List<Product> products = qds.toObjects(Product.class);
                    callback.onProductsFetched(products);
                })
                .addOnFailureListener(e -> Log.e("HomeRepository", "Error fetching all products", e));
    }

    public interface CategoriesCallback {
       void onCategoriesFetched(List<Category> categories);
    }

    public interface ProductsCallback {
        void onProductsFetched(List<Product> products);
    }
}
