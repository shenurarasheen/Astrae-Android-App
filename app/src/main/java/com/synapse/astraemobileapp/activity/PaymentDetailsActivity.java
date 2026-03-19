package com.synapse.astraemobileapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.databinding.ActivityPaymentDetailsBinding;
import com.synapse.astraemobileapp.databinding.ActivityProfileBinding;
import com.synapse.astraemobileapp.repository.PaymentRepository;

import java.util.Locale;

public class PaymentDetailsActivity extends AppCompatActivity {

    private ActivityPaymentDetailsBinding binding;
    private PaymentRepository paymentRepo;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(PaymentDetailsActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();

            return;
        }

        binding = ActivityPaymentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        paymentRepo = new PaymentRepository(this);

        paymentRepo.calculateTotal(total -> {
            binding.paymentDetailsTotalAmount.setText(String.format(
                    Locale.US,
                    "LKR %,.2f",
                    total
            ));
        });

    }
}