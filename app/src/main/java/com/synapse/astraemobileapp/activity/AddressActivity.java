package com.synapse.astraemobileapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.adapter.AddressAdapter;
import com.synapse.astraemobileapp.databinding.ActivityAddressBinding;
import com.synapse.astraemobileapp.model.User;
import com.synapse.astraemobileapp.repository.ProfileRepository;

public class AddressActivity extends AppCompatActivity {

    private ActivityAddressBinding binding;

    private FirebaseAuth auth;

    private ProfileRepository profileRepo;

    private User user;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(AddressActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        profileRepo = new ProfileRepository(this);

        binding.addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String uId = auth.getCurrentUser().getUid();

        profileRepo.getUserInitialData(uId, user -> {

            this.user = user;

            if (user != null) {
                profileRepo.getAllUserAddresses(uId, addresses -> {
                    AddressAdapter adapter = new AddressAdapter(addresses, user.getPermanentAddressId());
                    binding.addressRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }
}