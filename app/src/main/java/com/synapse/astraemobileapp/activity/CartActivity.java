package com.synapse.astraemobileapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.adapter.CartAdapter;
import com.synapse.astraemobileapp.databinding.ActivityCartBinding;
import com.synapse.astraemobileapp.repository.CartRepository;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;

    private CartRepository cartRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCartBinding.inflate(getLayoutInflater());
        cartRepo = new CartRepository(this);

        setContentView(binding.getRoot());

        // get cart items from CartRepository
        cartRepo.getAllCartItems(cartItems -> {
            cartRecyclerView = findViewById(R.id.cartRecyclerView);
            cartAdapter = new CartAdapter(cartItems, this, position -> {
                cartItems.remove(position);
                cartAdapter.notifyItemRemoved(position);
                cartAdapter.notifyItemRangeRemoved(position, cartItems.size());
            });
            cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            cartRecyclerView.setAdapter(cartAdapter);
        });

        binding.cartCheckoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, PaymentDetailsActivity.class);
            startActivity(intent);
            finish();
        });
    }
}