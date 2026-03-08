package com.synapse.astraemobileapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.synapse.astraemobileapp.databinding.ActivitySignUpBinding;
import com.synapse.astraemobileapp.model.User;
import com.synapse.astraemobileapp.util.PasswordUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private FirebaseAuth auth;
    private FirebaseFirestore db;


    private final ExecutorService service = Executors.newSingleThreadExecutor();

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

        // handle sign up button click
        binding.signupBtn.setOnClickListener(v -> saveUserData());

    }

    private void saveUserData() {
        String firstName = binding.signupFirstNameInput.getText().toString();
        String lastName = binding.signupLastNameInput.getText().toString();
        String email = binding.signupEmailInput.getText().toString();
        String password = binding.signupPasswordInput.getText().toString();
        String confirmPassword = binding.signupConfirmPasswordInput.getText().toString();

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