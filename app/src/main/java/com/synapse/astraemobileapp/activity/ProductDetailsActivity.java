package com.synapse.astraemobileapp.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.adapter.ProductSliderAdapter;
import com.synapse.astraemobileapp.databinding.ActivityProductDetailsBinding;
import com.synapse.astraemobileapp.model.Product;

import java.util.List;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {

    private ActivityProductDetailsBinding binding;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private String productId;

    private int qty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        productId = intent.getStringExtra("productId");

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//            RenderEffect blurEffect = RenderEffect.createBlurEffect(
//                    25f, 25f, Shader.TileMode.CLAMP
//            );
//
//            binding.bottomBtnPanel.setRenderEffect(blurEffect);
//        }

        loadProductData();
    }

    private void loadProductData() {

        db.collection("products")
                .whereEqualTo("productId", productId)
                .get()
                .addOnSuccessListener(qds -> {
                    if (!qds.isEmpty()) {
                        Product product = qds.toObjects(Product.class).get(0);
                        setProductDataToLayout(product);
                    } else {
                        Log.e("ProductDetailsActivity", "No such a product from product id : " + productId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductDetailsActivity", "Failed to load product details " + e);
                });
    }

    private void setProductDataToLayout(Product product) {
        ProductSliderAdapter adapter = new ProductSliderAdapter(product.getProductImages());
        binding.productViewpager.setAdapter(adapter);

        binding.dotsIndicator.attachTo(binding.productViewpager);
        binding.singleProductTitle.setText(product.getTitle());
        binding.singleProductDescription.setText(product.getDescription());
        loadProductBadges(product.getAttributes(), binding.singleProductBadgeContainer);

        // add listener for move to seller details activity
        binding.singleProductSeller.setOnClickListener(view -> {
            Intent intent = new Intent(ProductDetailsActivity.this, SellerDetailsActivity.class);
            intent.putExtra("sellerId", product.getSellerId());
            startActivity(intent);
        });

        //draw a line through across the estimate price
        binding.singleProductEstimatePrice.setPaintFlags(binding.singleProductEstimatePrice.getPaintFlags() |
                Paint.STRIKE_THRU_TEXT_FLAG);

        //set estimate and real price
        binding.singleProductRealPrice.setText(String.format(Locale.US,"%,.2f", product.getPrice()));
        binding.singleProductEstimatePrice.setText(String.format(Locale.US,"%,.2f", calculateEstimatePrice(product.getPrice())));

        // call increment and decrement methods when click +, - buttons
        binding.singleProductPlusQtyBtn.setOnClickListener(v -> {
            incrementQty(product.getStockCount());
        });

        binding.singleProductMinusQtyBtn.setOnClickListener(v -> {
            decrementQty();
        });
    }

    private double calculateEstimatePrice(double realPrice) {
        return (realPrice * 20 /100) + realPrice;
    }

    private void incrementQty(int productQty) {
        if (productQty > qty) {
            qty ++;
            binding.singleProductQty.setText(String.valueOf(qty));
            binding.singleProductPlusQtyBtn.setEnabled(true);
            binding.singleProductMinusQtyBtn.setEnabled(true);
        } else {
            binding.singleProductPlusQtyBtn.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Maximum quantity exceeded!", Toast.LENGTH_SHORT).show();
        }
    }

    private void decrementQty() {
        if (qty > 1) {
            qty --;
            binding.singleProductQty.setText(String.valueOf(qty));
            binding.singleProductMinusQtyBtn.setEnabled(true);
            binding.singleProductPlusQtyBtn.setEnabled(true);
        } else {
            binding.singleProductMinusQtyBtn.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Quantity can't be less than 1", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadProductBadges(List<Product.Attribute> attributes, FlexboxLayout badgeContainer) {

        badgeContainer.removeAllViews();

        if (attributes == null || attributes.isEmpty()) return;

        LayoutInflater inflater = LayoutInflater.from(badgeContainer.getContext());

        for (Product.Attribute attribute : attributes) {
            FrameLayout productBadge = (FrameLayout) inflater.inflate(R.layout.item_single_product_badge, null);
            TextView badgeText = productBadge.findViewById(R.id.single_product_badge_item_text);
            badgeText.setText(attribute.getValues().get(0));
            badgeContainer.addView(productBadge);
        }
    }
}