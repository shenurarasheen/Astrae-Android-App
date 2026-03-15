package com.synapse.astraemobileapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.adapter.SellerProductsAdapter;
import com.synapse.astraemobileapp.databinding.ActivitySellerDetailsBinding;
import com.synapse.astraemobileapp.model.Product;
import com.synapse.astraemobileapp.model.User;

import java.util.List;

public class SellerDetailsActivity extends AppCompatActivity {

    private ActivitySellerDetailsBinding binding;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private String sellerId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        sellerId = intent.getStringExtra("sellerId");

        binding = ActivitySellerDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // set seller data to the layout
        getSellerDetails(seller -> {
            binding.sellerDetailsFullName.setText(seller.getFirstName() + " " + seller.getLastName());
            binding.sellerDetailsEmail.setText(seller.getEmail());

            //need to set mobile and location details after completing profile.

        });

        binding.sellerDetailsProductRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

        //load seller products to the linear layout
        getAllSellerProducts(products -> {

            SellerProductsAdapter adapter = new SellerProductsAdapter(products, product -> {
                Intent toProductDetailsIntent = new Intent(SellerDetailsActivity.this, ProductDetailsActivity.class);
                toProductDetailsIntent.putExtra("productId", product.getProductId());
                startActivity(toProductDetailsIntent);
            });

            binding.sellerDetailsProductRecyclerView.setAdapter(adapter);

            binding.sellerDetailsItemCount.setText("All items (" + products.size() + ")");
        });

    }

    private void getSellerDetails(SellerCallback sellerCallback) {
        db.collection("users")
                .whereEqualTo("uid", sellerId)
                .get()
                .addOnSuccessListener(qds -> {
                    if(!qds.isEmpty()) {
                       User seller = qds.toObjects(User.class).get(0);
                       if (seller != null) {
                           sellerCallback.callback(seller);
                       } else {
                           Log.e("SellerDetailsActivity", "Seller is null");
                       }
                    } else {
                        Log.e("SellerDetailsActivity", "Seller not found from id : " + sellerId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SellerDetailsActivity", "Failed to retrieve seller data. " + e.getMessage());
                });
    }

    private void getAllSellerProducts(SellerAllProductsCallback sellerProductsCallback) {
        db.collection("products")
                .whereEqualTo("sellerId", sellerId)
                .orderBy("productId", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(qds -> {
                    if (!qds.isEmpty()) {
                        List<Product> products = qds.toObjects(Product.class);
                        sellerProductsCallback.callback(products);
                    } else {
                        Log.e("SellerDetailsActivity", "Seller products not found from id : " + sellerId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SellerDetailsActivity", "Failed to retrieve seller products. " + e.getMessage());
                });
    }

    public interface SellerCallback {
        void callback(User seller);
    }

    public interface SellerAllProductsCallback {
        void callback(List<Product> products);
    }
}