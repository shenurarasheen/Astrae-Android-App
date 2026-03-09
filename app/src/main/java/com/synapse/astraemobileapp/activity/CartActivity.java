package com.synapse.astraemobileapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.adapter.CartAdapter;
import com.synapse.astraemobileapp.databinding.ActivityCartBinding;
import com.synapse.astraemobileapp.model.Cart;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;

    private ArrayList<Cart> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCartBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Cart cartItem = new Cart();
        Cart cartItem2 = new Cart();
        Cart cartItem3 = new Cart();
        Cart cartItem4 = new Cart();
        Cart cartItem5 = new Cart();

        cartItem.setTitle("Ladies Necklace");
        cartItem2.setTitle("Diamond Necklace");
        cartItem3.setTitle("Ladies Necklace");
        cartItem4.setTitle("Gold Necklace");
        cartItem5.setTitle("Diamond Necklace");

        cartItems.add(cartItem);
        cartItems.add(cartItem2);
        cartItems.add(cartItem3);
        cartItems.add(cartItem4);
        cartItems.add(cartItem5);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartAdapter = new CartAdapter(cartItems);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        binding.cartCheckoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, PaymentDetailsActivity.class);
            startActivity(intent);
            finish();
        });
    }
}