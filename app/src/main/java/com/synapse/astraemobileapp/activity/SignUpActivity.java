package com.synapse.astraemobileapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.synapse.astraemobileapp.databinding.ActivitySignUpBinding;
import com.synapse.astraemobileapp.model.User;
import com.synapse.astraemobileapp.util.PasswordUtils;
import com.synapse.astraemobileapp.util.ValidationUtils;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //skip text click
        binding.signupSkipText.setOnClickListener(v -> {
            updateUi(HomeActivity.class);
        });

        // handle password and confirm password visibility
        PasswordUtils.togglePasswordVisibility(binding.signupPasswordInput);
        PasswordUtils.togglePasswordVisibility(binding.signupConfirmPasswordInput);

        // handle sign in text click
        binding.signupMoveToSigninText.setOnClickListener(v -> moveToSignIn());

        // handle sign up button click
        binding.signupBtn.setOnClickListener(v -> saveUserData());

    }

    private void saveUserData() {
        String firstName = binding.signupFirstNameInput.getText().toString().trim();
        String lastName = binding.signupLastNameInput.getText().toString().trim();
        String email = binding.signupEmailInput.getText().toString().trim();
        String password = binding.signupPasswordInput.getText().toString().trim();
        String confirmPassword = binding.signupConfirmPasswordInput.getText().toString().trim();

        if (!isInputValid(firstName, lastName, email, password, confirmPassword)) {
            return;
        }

        // save user data to database and move to sign in
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()
                            && task.getResult() != null
                            && task.getResult().getUser() != null) {

                        String uId = task.getResult().getUser().getUid();

                        Log.i("SignUpActivity", "Created user with uid=" + uId);

                        //save user data to firestore
                        User user = User.builder()
                                .uId(uId)
                                .firstName(firstName)
                                .lastName(lastName)
                                .email(email)
                                .build();

                        db.collection("users")
                                .document(uId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getApplicationContext(), "Registration successful!. Please sign in.", Toast.LENGTH_SHORT).show();
                                    Log.i("SignUpActivity", "User data saved successfully for uid=" + uId);
                                    moveToSignIn();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getApplicationContext(), "Registration failed!. Please try again.", Toast.LENGTH_SHORT).show();
                                    Log.e("SignUpActivity", "Failed to save user data for uid=" + uId);
                                });

                    } else {
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() : "Authentication failed";
                        Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        Log.e("SignUpActivity", "Auth failed: " + errorMessage);
                    }

                });
    }


    private boolean isInputValid(String firstName, String lastName, String email, String password, String confirmPassword) {

        if (firstName.isEmpty()) {
            showError(binding.signupFirstNameInput, "First name is required");
            return false;
        }

        if (lastName.isEmpty()) {
            showError(binding.signupLastNameInput, "Last name is required");
            return false;
        }

        if (email.isEmpty()) {
            showError(binding.signupEmailInput, "Email is required");
            return false;
        } else if (!ValidationUtils.isValidEmail(email)) {
            showError(binding.signupEmailInput, "Invalid email format");
            return false;
        }

        if (password.isEmpty()) {
            showError(binding.signupPasswordInput, "Password is required");
            return false;
        } else if (!ValidationUtils.isValidPassword(password)) {
            showError(binding.signupPasswordInput, "Password must be at least 6 characters");
            return false;
        }

        if (confirmPassword.isEmpty()) {
            showError(binding.signupConfirmPasswordInput, "Confirm password is required");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showError(binding.signupConfirmPasswordInput, "Passwords do not match");
            return false;
        }

        return true;
    }

    private void showError(EditText editText, String message) {
        TextView errorText = binding.signupErrorMessageText;
        errorText.setText(message);
        editText.requestFocus();
        errorText.setVisibility(View.VISIBLE);
    }

    private void moveToSignIn() {
        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUi(Class<? extends AppCompatActivity> activityClass) {
        Intent intent = new Intent(SignUpActivity.this, activityClass);
        startActivity(intent);
        finish();
    }
}