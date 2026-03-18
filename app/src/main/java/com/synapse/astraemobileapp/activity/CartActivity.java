package com.synapse.astraemobileapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.adapter.CartAdapter;
import com.synapse.astraemobileapp.databinding.ActivityCartBinding;
import com.synapse.astraemobileapp.model.CartItem;
import com.synapse.astraemobileapp.model.Product;
import com.synapse.astraemobileapp.repository.CartRepository;
import com.synapse.astraemobileapp.repository.ProfileRepository;
import com.synapse.astraemobileapp.util.DialogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;

    private CartRepository cartRepo;
    private ProfileRepository profileRepo;

    private Map<String, Product> priceCache = new HashMap<>();
    private List<CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCartBinding.inflate(getLayoutInflater());
        cartRepo = new CartRepository(this);
        profileRepo = new ProfileRepository();

        setContentView(binding.getRoot());

        // get cart items from CartRepository
        cartRepo.getAllCartItems(cartItems -> {

            this.cartItems = cartItems;

            cartRepo.cacheCartProducts(cartItems, productMap -> {
                this.priceCache = productMap;

                setCartRecyclerView(cartItems);

                binding.cartCheckoutBtn.setOnClickListener(v -> {

                    profileRepo.isPermanentAddressFilled(isFilled -> {
                        if (isFilled) {
                            Intent intent = new Intent(CartActivity.this, PaymentDetailsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            //showAlertDialog();
                            DialogUtils.showCustomDialog(this,
                                    "Confirmation",
                                    "Please fill your address details in profile before checkout",
                                    "Update",
                                    "Discard",
                                    new DialogUtils.CustomDialogClickCallback() {
                                        @Override
                                        public void onConfirm() {
                                            Intent intent = new Intent(CartActivity.this, ProfileActivity.class);
                                            startActivity(intent);
                                        }
                                        @Override
                                        public void onCancel() {
                                        }
                                    });
                        }
                    });

                });

                binding.itemCount.setText("Item (" + cartItems.size() + ")");

                updateTotalUI();
            });

        });

    }

    private void setCartRecyclerView(List<CartItem> cartItems) {
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartAdapter = new CartAdapter(cartItems, this, position -> {
            cartItems.remove(position);
            cartAdapter.notifyItemRemoved(position);
            cartAdapter.notifyItemRangeRemoved(position, cartItems.size());
        });

        cartAdapter.setOnQuantityChangedListener(() -> {
            updateTotalUI();
        });

        cartAdapter.setOnItemRemoveListener(() -> {
            updateTotalUI();
        });

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void updateTotalUI() {
        double total = 0;
        double saved = 0;

        for (CartItem item : cartItems) {
            Product p = priceCache.get(item.getProductId());
            if (p != null) {
                total += p.getPrice() * item.getQuantity();
                saved += (p.getPrice() * 0.05) * item.getQuantity();
            }
        }

        binding.cartTotalPrice.setText(String.format(Locale.US, "LKR %,.2f", total));
        binding.cartSavedPrice.setText(String.format(Locale.US, "LKR %,.2f", saved));
    }


}