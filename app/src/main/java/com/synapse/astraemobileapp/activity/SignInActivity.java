package com.synapse.astraemobileapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.databinding.ActivitySignInBinding;
import com.synapse.astraemobileapp.util.PasswordUtils;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //handle password visibility
        PasswordUtils.togglePasswordVisibility(binding.signinPasswordInput);

        // handle sign up text click
        binding.signinMoveToSignupText.setOnClickListener(v -> moveToSignUp());

    }

    private void moveToSignUp() {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}