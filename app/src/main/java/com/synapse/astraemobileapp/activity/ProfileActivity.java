package com.synapse.astraemobileapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.becomeASellerLayout.setOnClickListener(v -> {
            updateActivity(AddProductActivity.class, true);
        });
    }

    private void updateActivity(Class<? extends AppCompatActivity> activityClass, boolean needToBack) {
        Intent intent = new Intent(ProfileActivity.this, activityClass);
        startActivity(intent);
        if (!needToBack) finish();
    }
}