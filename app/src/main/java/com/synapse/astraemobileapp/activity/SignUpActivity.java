package com.synapse.astraemobileapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.databinding.ActivitySignUpBinding;
import com.synapse.astraemobileapp.util.PasswordUtils;

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

        // handle password and confirm password visibility
        PasswordUtils.togglePasswordVisibility(binding.signupPasswordInput);
        PasswordUtils.togglePasswordVisibility(binding.signupConfirmPasswordInput);

        // handle sign in text click
        binding.signupMoveToSigninText.setOnClickListener(v -> moveToSignIn());

    }

    private void saveUserData() {
        String firstName = binding.signupFirstNameInput.getText().toString();
        String lastName = binding.signupLastNameInput.getText().toString();
        String email = binding.signupEmailInput.getText().toString();
        String password = binding.signupPasswordInput.getText().toString();
        String confirmPassword = binding.signupConfirmPasswordInput.toString();

        if (firstName.isEmpty()) {
            showError(binding.signupFirstNameInput, "First name is required");
            return;
        }

        if (lastName.isEmpty()) {
            showError(binding.signupLastNameInput, "Last name is required");
            return;
        }

        if (email.isEmpty()) {
            showError(binding.signupEmailInput, "Email is required");
            return;
        }

        if (password.isEmpty()) {
            showError(binding.signupPasswordInput, "Password is required");
            return;
        }

        if (confirmPassword.isEmpty()) {
            showError(binding.signupConfirmPasswordInput, "Confirm password is required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError(binding.signupConfirmPasswordInput, "Passwords do not match");
            return;
        }

        // save user data to database and move to sign in
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()
                            && task.getResult() != null
                            && task.getResult().getUser() != null) {

                        String uId = task.getResult().getUser().getUid();

                        Log.i(SignUpActivity.class.getSimpleName(), "Created user with uid=" + uId);

                        //save user data to firestore

                    }

                });

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


}