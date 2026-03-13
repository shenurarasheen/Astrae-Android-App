package com.synapse.astraemobileapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.activity.ProductDetailsActivity;
import com.synapse.astraemobileapp.adapter.ListingAdapter;
import com.synapse.astraemobileapp.databinding.FragmentListingBinding;
import com.synapse.astraemobileapp.model.Product;

import java.util.List;

public class ListingFragment extends Fragment {

    private FragmentListingBinding binding;
    private String categoryId;
    private String categoryName;

    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            categoryName = getArguments().getString("categoryName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.listingCategoryTitle.setText(categoryName);
        binding.listingCategoryProductContainer.setLayoutManager(new GridLayoutManager(getContext(), 2));

        getProductsByCategory(categoryId);

    }

    private void getProductsByCategory(String categoryId) {
        db.collection("products")
                .whereEqualTo("categoryId", categoryId)
                .orderBy("title", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(ds -> {
                    if (!ds.isEmpty()) {
                        List<Product> products = ds.toObjects(Product.class);

                        ListingAdapter listingAdapter = new ListingAdapter(products, product -> {
                            Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                            intent.putExtra("productId", product.getProductId());
                            startActivity(intent);
                        });

                        binding.listingCategoryProductContainer.setAdapter(listingAdapter);
                    } else {
                        Log.i("ListingFragment", "No products to load.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ListingFragment", "Failed to retrieve data from database " + e.getMessage());
                });
    }
}