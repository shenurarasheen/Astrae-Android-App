package com.synapse.astraemobileapp.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.synapse.astraemobileapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeCaroselSliderAdapter extends RecyclerView.Adapter<HomeCaroselSliderAdapter.SliderViewHolder> {

    private FirebaseStorage storage;
    private List<String> carouselImages = new ArrayList<>(Arrays.asList(
            "/carousel1.jpg",
            "/carousel2.jpg",
            "/carousel3.jpg",
            "/carousel4.jpg",
            "/carousel5.jpg"
    ));

    public HomeCaroselSliderAdapter() {
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        loadProductImage(holder.imageView, carouselImages.get(position));
    }

    @Override
    public int getItemCount() {
        return carouselImages.size();
    }

    private void loadProductImage(ImageView imageView, String imageUri) {
        storage.getReference("/carousel-images" + imageUri)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(imageView.getContext())
                            .load(uri)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageView);
                })
                .addOnFailureListener(e -> Log.e("Carousel", "Failed to load image", e));
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_slider_image_item);
        }
    }
}
