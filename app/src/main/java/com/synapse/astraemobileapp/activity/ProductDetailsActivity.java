package com.synapse.astraemobileapp.activity;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.adapter.ProductSliderAdapter;
import com.synapse.astraemobileapp.adapter.RecentlyViewItemAdapter;
import com.synapse.astraemobileapp.adapter.SimilarItemsAdapter;
import com.synapse.astraemobileapp.databinding.ActivityProductDetailsBinding;
import com.synapse.astraemobileapp.model.Product;
import com.synapse.astraemobileapp.repository.CartRepository;

import java.util.List;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {

    private ActivityProductDetailsBinding binding;
    private FirebaseFirestore db;

    private CartRepository cartRepo;

    private int qty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        cartRepo = new CartRepository(this);

        Intent intent = getIntent();
        String productId = intent.getStringExtra("productId");

        loadProductData(productId);

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//            RenderEffect blurEffect = RenderEffect.createBlurEffect(
//                    25f, 25f, Shader.TileMode.CLAMP
//            );
//            binding.bottomBtnPanel.setRenderEffect(blurEffect);
//        }

    }

    private void loadProductData(String productId) {
        db.collection("products")
                .whereEqualTo("productId", productId)
                .get()
                .addOnSuccessListener(qds -> {
                    if (!qds.isEmpty()) {
                        Product product = qds.toObjects(Product.class).get(0);
                        setProductDataToLayout(product);
                        loadAllSimilarProducts(product);

                        updateProductViewedCount(productId);
                    } else {
                        Log.e("ProductDetailsActivity", "No such a product from product id : " + productId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductDetailsActivity", "Failed to load product details " + e);
                });
    }

    @SuppressLint("SetTextI18n")
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

        binding.singleProductSoldCount.setText("Sold " + product.getSoldCount() + "+");

        binding.singleProductRating.setRating(product.getRating());
        binding.singleProductRatingValue.setText(String.format(Locale.US,"%.1f", product.getRating()));

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

        binding.singleProductAddToCartBtn.setOnClickListener(v -> {
            int qty =  Integer.parseInt(binding.singleProductQty.getText().toString());
            cartRepo.addToCart(product, qty);
        });

        binding.singleProductBuyNowBtn.setOnClickListener(v -> {
            int qty =  Integer.parseInt(binding.singleProductQty.getText().toString());
            Intent intent = new Intent(ProductDetailsActivity.this, PaymentDetailsActivity.class);
            intent.putExtra("productId", product.getProductId());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("quantity", qty);
            startActivity(intent);
        });
    }

    private double calculateEstimatePrice(double realPrice) {
        return (realPrice * 5 /100) + realPrice;
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


    private void loadAllSimilarProducts(Product product) {
        db.collection("products")
                .whereEqualTo("categoryId", product.getCategoryId())
                .whereNotEqualTo("productId", product.getProductId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> products = task.getResult().toObjects(Product.class);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        binding.similarProductItemsSection.itemSectionRecyclerView.setLayoutManager(layoutManager);

                        SimilarItemsAdapter itemAdapter = new SimilarItemsAdapter(products, similarProduct -> {
                            loadProductData(similarProduct.getProductId());
                        });

                        binding.similarProductItemsSection.itemSectionTitle.setText("More Similar Items");
                        binding.similarProductItemsSection.itemSectionRecyclerView.setAdapter(itemAdapter);

                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                        Log.e("ProductDetailsActivity", "Failed to load product details " + task.getException().getMessage());
                    }
                });
    }

    private void updateProductViewedCount(String productId) {
        db.collection("products")
                .whereEqualTo("productId", productId)
                .limit(1)
                .get()
                .addOnSuccessListener(qds -> {
                    for (DocumentSnapshot doc : qds) {
                        doc.getReference().update("viewedCount", FieldValue.increment(1));
                    }
                })
                .addOnFailureListener(e -> Log.e("ProductDetailsActivity", "Update failed", e));
    }
}