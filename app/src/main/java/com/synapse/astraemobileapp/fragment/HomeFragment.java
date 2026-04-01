package com.synapse.astraemobileapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.activity.ProductDetailsActivity;
import com.synapse.astraemobileapp.adapter.HomeCaroselSliderAdapter;
import com.synapse.astraemobileapp.adapter.HomeCategoryAdapter;
import com.synapse.astraemobileapp.adapter.NewItemsAdapter;
import com.synapse.astraemobileapp.databinding.FragmentHomeBinding;
import com.synapse.astraemobileapp.repository.HomeRepository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private HomeRepository homeRepo;

    private ScheduledExecutorService carouselExecutor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.homeRepo = new HomeRepository();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        loadCategories();
        loadNewProducts();
        loadAllProducts();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HomeCaroselSliderAdapter adapter = new HomeCaroselSliderAdapter();
        binding.homeCarouselViewPager.setAdapter(adapter);
        binding.dotsIndicator.attachTo(binding.homeCarouselViewPager);
    }

    private void loadCategories() {
        binding.homeCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeRepo.getAllCategories(categories -> {
            HomeCategoryAdapter adapter = new HomeCategoryAdapter(categories, getParentFragmentManager());
            binding.homeCategoryRecyclerView.setAdapter(adapter);
        });
    }

    private void startAutoCarousel() {
        carouselExecutor = Executors.newSingleThreadScheduledExecutor();

        carouselExecutor.scheduleWithFixedDelay(() -> {
            getActivity().runOnUiThread(() -> {
                int nextItem = binding.homeCarouselViewPager.getCurrentItem() + 1;
                if (nextItem >= binding.homeCarouselViewPager.getAdapter().getItemCount()) {
                    nextItem = 0;
                }
                binding.homeCarouselViewPager.setCurrentItem(nextItem, true);
            });
        },6,5, TimeUnit.SECONDS);
    }

    private void loadNewProducts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.newProductItemsSection.itemSectionRecyclerView.setLayoutManager(layoutManager);
        homeRepo.getNewProducts(products -> {
            NewItemsAdapter adapter = new NewItemsAdapter(products, getContext(), product -> {
                Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                intent.putExtra("productId", product.getProductId());
                startActivity(intent);
            });
            binding.newProductItemsSection.itemSectionRecyclerView.setAdapter(adapter);
        });
        binding.newProductItemsSection.itemSectionTitle.setTextSize(18);
        binding.newProductItemsSection.itemSectionTitle.setText("New Items");
    }

    private void loadAllProducts() {
        binding.homeAllProductItemsSection.itemSectionRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        homeRepo.getAllProducts(products -> {
            NewItemsAdapter adapter = new NewItemsAdapter(products, getContext(), product -> {
                Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                intent.putExtra("productId", product.getProductId());
                startActivity(intent);
            });
            binding.homeAllProductItemsSection.itemSectionRecyclerView.setAdapter(adapter);
        });
        binding.homeAllProductItemsSection.itemSectionTitle.setTextSize(18);
        binding.homeAllProductItemsSection.itemSectionTitle.setText("All Products");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (carouselExecutor != null || !carouselExecutor.isShutdown()) {
            carouselExecutor.shutdown();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoCarousel();
    }
}