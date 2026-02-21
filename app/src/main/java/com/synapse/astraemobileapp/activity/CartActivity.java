package com.synapse.astraemobileapp.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.adapter.CartAdapter;
import com.synapse.astraemobileapp.model.Cart;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;

    private ArrayList<Cart> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Cart cartItem = new Cart();
        Cart cartItem2 = new Cart();
        Cart cartItem3 = new Cart();
        Cart cartItem4 = new Cart();

        cartItem.setTitle("Ladies Necklace");
        cartItem2.setTitle("Diamond Necklace");
        cartItem3.setTitle("Ladies Necklace");
        cartItem4.setTitle("Gold Necklace");

        cartItems.add(cartItem);
        cartItems.add(cartItem2);
        cartItems.add(cartItem3);
        cartItems.add(cartItem4);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartAdapter = new CartAdapter(cartItems);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);
    }
}