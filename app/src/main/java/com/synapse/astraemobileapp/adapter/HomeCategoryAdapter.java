package com.synapse.astraemobileapp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.fragment.ListingFragment;
import com.synapse.astraemobileapp.model.Category;

import java.util.List;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private FragmentManager fragmentManager;

    public HomeCategoryAdapter(List<Category> categories, FragmentManager fragmentManager) {
        this.categories = categories;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryName.setClipToOutline(true);

        holder.itemView.setOnClickListener(v -> loadListingFragment(category.getCategoryId(), category.getName()));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private void loadListingFragment(String categoryId, String categoryName) {
        Bundle bundle = new Bundle();
        bundle.putString("categoryId", categoryId);
        bundle.putString("categoryName", categoryName);

        ListingFragment fragment = new ListingFragment();
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.item_home_category_badge);
        }
    }
}
