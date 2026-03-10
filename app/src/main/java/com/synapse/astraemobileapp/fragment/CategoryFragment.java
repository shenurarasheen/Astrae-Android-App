package com.synapse.astraemobileapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.databinding.FragmentCategoryBinding;
import com.synapse.astraemobileapp.databinding.ItemCartBinding;
import com.synapse.astraemobileapp.databinding.ItemCategoryBinding;
import com.synapse.astraemobileapp.model.Category;

import java.util.List;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private FlexboxLayout flexLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(inflater, container,false);

        flexLayout = binding.categoryFlexboxContainer;


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getAllCategories(categories -> {
            if (categories != null && !categories.isEmpty()) {
                // set category items for flexbox layout
                for (Category category: categories) {
                    ItemCategoryBinding categoryBinding = ItemCategoryBinding.inflate(getLayoutInflater(), flexLayout, false);

                    categoryBinding.categoryItemTitle.setText(category.getName());
                    setCategoryImage(category, categoryBinding);

                    FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                            FlexboxLayout.LayoutParams.WRAP_CONTENT,
                            FlexboxLayout.LayoutParams.WRAP_CONTENT
                    );

                    layoutParams.setMargins(0, 10, 10, 10);

                    View rootView = categoryBinding.getRoot();
                    rootView.setLayoutParams(layoutParams);

                    flexLayout.addView(rootView);

                }

            } else {
                Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                Log.e("CategoryFragment", "Failed to fetch categories or no categories found");
            }
        });
    }

    private void setCategoryImage(Category category, ItemCategoryBinding categoryBinding) {
        storage.getReference(category.getImageUrl())
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(getContext())
                            .load(uri)
                            .centerCrop()
                            .into(categoryBinding.categoryItemImage);
                });
    }

    private void getAllCategories(FirestoreCallback callback) {
        db.collection("categories").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onCallback(task.getResult().toObjects(Category.class));
                    } else {
                        callback.onCallback(null);
                    }
                });
    }

    public interface FirestoreCallback {
        void onCallback(List<Category> categories);
    }
}