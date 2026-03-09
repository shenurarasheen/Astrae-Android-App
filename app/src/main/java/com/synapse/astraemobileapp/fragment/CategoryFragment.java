package com.synapse.astraemobileapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.databinding.FragmentCategoryBinding;
import com.synapse.astraemobileapp.model.Category;

import java.util.List;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;

    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCategoryBinding.inflate(inflater, container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getAllCategories(categories -> {
            if (categories != null && !categories.isEmpty()) {
                // need to set category items for flexbox layout
            } else {
                Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                Log.e("CategoryFragment", "Failed to fetch categories or no categories found");
            }
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