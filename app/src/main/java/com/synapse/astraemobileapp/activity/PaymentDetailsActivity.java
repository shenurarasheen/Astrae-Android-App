package com.synapse.astraemobileapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.adapter.BottomDialogAddressAdapter;
import com.synapse.astraemobileapp.databinding.ActivityPaymentDetailsBinding;
import com.synapse.astraemobileapp.databinding.ActivityProfileBinding;
import com.synapse.astraemobileapp.model.User;
import com.synapse.astraemobileapp.repository.PaymentRepository;
import com.synapse.astraemobileapp.repository.ProfileRepository;

import java.util.Locale;

public class PaymentDetailsActivity extends AppCompatActivity {

    private ActivityPaymentDetailsBinding binding;
    private PaymentRepository paymentRepo;
    private ProfileRepository profileRepo;

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
        profileRepo = new ProfileRepository(this);

        paymentRepo.calculateTotal(total -> {
            binding.paymentDetailsTotalAmount.setText(String.format(
                    Locale.US,
                    "LKR %,.2f",
                    total
            ));
            binding.paymentDetailsPayBtn.setText(String.format(
                    Locale.US,
                    "Pay LKR %,.2f",
                    total
            ));
        });

        binding.paymentDetailsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                profileRepo.getUserInitialData(auth.getCurrentUser().getUid(), this::showAddressBottomSheet);
            }
        });

    }

    private void showAddressBottomSheet(User user) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_address_bottom_sheet, null);

        RecyclerView recyclerView = view.findViewById(R.id.bottom_sheet_address_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        profileRepo.getAllUserAddresses(user.getUId(), addresses -> {
            BottomDialogAddressAdapter adapter = new BottomDialogAddressAdapter(addresses, user.getPermanentAddressId(), address -> {
                binding.paymentDetailsFirstName.setText(address.getFirstName());
                binding.paymentDetailsLastName.setText(address.getLastName());
                binding.paymentDetailsAddressLine1.setText(address.getAddressLine1());
                binding.paymentDetailsAddressLine2.setText(address.getAddressLine2());
                binding.paymentDetailsCity.setText(address.getCity());
                binding.paymentDetailsPostalCode.setText(address.getPostalCode());
                binding.paymentDetailsMobileNumber.setText(address.getMobile());

                bottomSheetDialog.dismiss();
            });

            recyclerView.setAdapter(adapter);
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
}