package com.synapse.astraemobileapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.adapter.OrderAdapter;
import com.synapse.astraemobileapp.databinding.ActivityOrdersBinding;
import com.synapse.astraemobileapp.repository.OrderRepository;

public class OrdersActivity extends AppCompatActivity {

    private ActivityOrdersBinding binding;

    private FirebaseAuth auth;

    private OrderRepository orderRepo;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(OrdersActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }

        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderRepo = new OrderRepository(this);

        binding.orderContainerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderRepo.getUserOrders(orders -> {
            if (orders.isEmpty()) {
                Toast.makeText(this, "No orders found", Toast.LENGTH_SHORT).show();
                return;
            }
            OrderAdapter adapter = new OrderAdapter(orders, this);
            binding.orderContainerRecyclerView.setAdapter(adapter);

            binding.orderItemsCount.setText("Items (" + orders.size() + ")");
        });

    }
}