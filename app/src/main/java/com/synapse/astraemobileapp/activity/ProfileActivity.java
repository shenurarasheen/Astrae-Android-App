package com.synapse.astraemobileapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.databinding.ActivityProfileBinding;
import com.synapse.astraemobileapp.model.Address;
import com.synapse.astraemobileapp.model.User;
import com.synapse.astraemobileapp.repository.ProfileRepository;
import com.synapse.astraemobileapp.util.ValidationUtils;

import java.sql.Array;
import java.util.Arrays;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    private FirebaseAuth auth;

    private ProfileRepository profileRepo;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
            startActivity(intent);
            return;
        }

        profileRepo = new ProfileRepository(this);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.becomeASellerLayout.setOnClickListener(v -> {
            updateActivity(AddProductActivity.class, true);
        });

        //set firstname, lastname and email to fields
        setProfileDetails();

        binding.profileSaveChangesBtn.setOnClickListener(view -> {
            updateProfileData();
        });

        binding.profileDiscardBtn.setOnClickListener(view -> {
            setProfileDetails();
        });


        binding.profileProfileImage.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            activityResultLauncher.launch(intent);
        });


        binding.profileAllAddressesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AddressActivity.class);
            startActivity(intent);
        });
    }


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == ProfileActivity.RESULT_OK) {

                    if (result.getData() == null) return;

                    Uri uri = result.getData().getData();

                    if (uri != null && uri.getPath() != null) {

                        Log.i("ImageURI", uri.getPath());

                        Glide.with(ProfileActivity.this)
                                .load(uri)
                                .centerCrop()
                                .into(binding.profileProfileImage);

                        profileRepo.storeProfileImage(uri, user);
                    }

                }
            }
    );


    @SuppressLint("SetTextI18n")
    private void setProfileDetails() {

        if (auth.getCurrentUser() == null) return;

        String uId = auth.getCurrentUser().getUid();
        profileRepo.getUserInitialData(uId, user -> {
            this.user = user;

            profileRepo.loadProfileImage(user, binding.profileProfileImage);

            binding.profileUserName.setText(user.getFirstName() + " " + user.getLastName());

            binding.profileFirstName.setText(user.getFirstName());
            binding.profileLastName.setText(user.getLastName());
            binding.profileEmailAddress.setText(user.getEmail());

            if (user.getPermanentAddressId() == null) {
                return;
            }

            if (user.getPermanentAddressId().isEmpty()) {
                return;
            }

            profileRepo.getPermanentAddress(uId, user.getPermanentAddressId(), permanentAddress -> {
                binding.profileMobileNumber.setText(permanentAddress.getMobile());
                binding.profileAddressLine1.setText(permanentAddress.getAddressLine1());
                binding.profileAddressLine2.setText(permanentAddress.getAddressLine2());
                binding.profileCity.setText(permanentAddress.getCity());
                binding.profilePostalCode.setText(permanentAddress.getPostalCode());
            });

        });
    }


    private void updateActivity(Class<? extends AppCompatActivity> activityClass, boolean needToBack) {
        Intent intent = new Intent(ProfileActivity.this, activityClass);
        startActivity(intent);
        if (!needToBack) finish();
    }


    private void updateProfileData() {
        String firstName = binding.profileFirstName.getText().toString().trim();
        String lastName = binding.profileLastName.getText().toString().trim();
        String email = binding.profileEmailAddress.getText().toString().trim();
        String mobile = binding.profileMobileNumber.getText().toString().trim();
        String addressLine1 = binding.profileAddressLine1.getText().toString().trim();
        String addressLine2 = binding.profileAddressLine2.getText().toString().trim();
        String city = binding.profileCity.getText().toString().trim();
        String postalCode = binding.profilePostalCode.getText().toString().trim(); //need to convert to long when save

        if (!validateFields(firstName, lastName, email, mobile, addressLine1, addressLine2, city, postalCode)) {
            return;
        }

        if (user == null) {
            return;
        }

        Address permanentAddress = Address.builder()
                .firstName(firstName)
                .lastName(lastName)
                .mobile(mobile)
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .city(city)
                .postalCode(postalCode)
                .build();

        profileRepo.saveAddressDetails(permanentAddress, addressId -> {

            profileRepo.updatePermanentAddressId(addressId, user);

        });
    }


    private boolean validateFields(String firstName, String lastName, String email, String mobile, String line1, String line2, String city, String postalCode) {

        if (firstName.isEmpty()) {
            Toast.makeText(this, "First name is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (lastName.isEmpty()) {
            Toast.makeText(this, "Last name is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!ValidationUtils.isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mobile.isEmpty()) {
            Toast.makeText(this, "Mobile number is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!ValidationUtils.isValidMobile(mobile)) {
            Toast.makeText(this, "Invalid mobile number format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (line1.isEmpty()) {
            Toast.makeText(this, "Address line 1 is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (line2.isEmpty()) {
            Toast.makeText(this, "Address line 2 is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (city.isEmpty()) {
            Toast.makeText(this, "City is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (postalCode.isEmpty()) {
            Toast.makeText(this, "Postal code is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!ValidationUtils.isValidPostalCode(postalCode)) {
            Toast.makeText(this, "Invalid postal code", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}