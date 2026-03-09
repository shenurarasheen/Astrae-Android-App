package com.synapse.astraemobileapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.databinding.ActivitySignInBinding;
import com.synapse.astraemobileapp.util.PasswordUtils;
import com.synapse.astraemobileapp.util.ValidationUtils;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        //skip text click
        binding.signinSkipText.setOnClickListener(v -> {
            updateUi(HomeActivity.class);
        });

        //handle password visibility
        PasswordUtils.togglePasswordVisibility(binding.signinPasswordInput);

        // handle sign up text click
        binding.signinMoveToSignupText.setOnClickListener(v -> {
            updateUi(SignUpActivity.class);
        });

        //handle login
        binding.signinBtn.setOnClickListener(v -> loginUser());

    }

    private void loginUser() {
        String email = binding.signinEmailInput.getText().toString().trim();
        String password = binding.signinPasswordInput.getText().toString().trim();

        if (!isInputValid(email, password)) return;

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.i("SignInActivity", "Login successful");
                        updateUi(HomeActivity.class);
                    } else {
                        String errorMessage = task.getException() != null ?
                                getFirebaseErrorMessage(task.getException()) :
                                "Authentication Failed.";

                        Log.w("SignInActivity", errorMessage);

                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isInputValid(String email, String password) {

        if (email.isEmpty()) {
            showError(binding.signinEmailInput, "Enter the email address.");
            return false;
        } else if (!ValidationUtils.isValidEmail(email)) {
            showError(binding.signinEmailInput, "Invalid email address");
            return false;
        }

        if (password.isEmpty()) {
            showError(binding.signinPasswordInput, "Enter the password");
            return false;
        }

        return true;
    }

    private void showError(EditText editText, String message) {
        TextView errorText = binding.signinErrorText;
        errorText.setText(message);
        editText.requestFocus();
        errorText.setVisibility(View.VISIBLE);
    }

    private void updateUi(Class<? extends AppCompatActivity> activityClass) {
        Intent intent = new Intent(SignInActivity.this, activityClass);
        startActivity(intent);
        finish();
    }

    private String getFirebaseErrorMessage(Exception e) {
        if (e instanceof FirebaseAuthInvalidUserException) {
            return "No account found with this email.";
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "Incorrect email or password.";
        } else if (e instanceof FirebaseNetworkException) {
            return "Network error. Please check your internet connection.";
        } else {
            return "An unknown error occurred";
        }
    }

}