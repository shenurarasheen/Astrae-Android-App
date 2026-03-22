package com.synapse.astraemobileapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.adapter.BottomDialogAddressAdapter;
import com.synapse.astraemobileapp.databinding.ActivityPaymentDetailsBinding;
import com.synapse.astraemobileapp.databinding.ActivityProfileBinding;
import com.synapse.astraemobileapp.model.Address;
import com.synapse.astraemobileapp.model.User;
import com.synapse.astraemobileapp.repository.PaymentRepository;
import com.synapse.astraemobileapp.repository.ProfileRepository;

import java.util.Locale;

import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class PaymentDetailsActivity extends AppCompatActivity {

    private ActivityPaymentDetailsBinding binding;
    private PaymentRepository paymentRepo;
    private ProfileRepository profileRepo;

    private FirebaseAuth auth;


    private double total;
    private boolean paymentActive;
    private boolean isCartBuying;

    private String addressId;

    //for single product buying
    private String productId;
    private int quantity;

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


        Intent intent = getIntent();
        if (intent.getExtras() != null && !intent.getExtras().isEmpty()) {
            double price = intent.getDoubleExtra("price", 0.0);
            productId = intent.getStringExtra("productId");
            quantity = intent.getIntExtra("quantity", 1);

            total = price * quantity;

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
            paymentActive = true;
            isCartBuying = false;
        } else {
            paymentRepo.calculateTotal(total -> {
                this.total = total;
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
                paymentActive = true;
                isCartBuying = true;
            });
        }


        binding.paymentDetailsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                profileRepo.getUserInitialData(auth.getCurrentUser().getUid(), this::showAddressBottomSheet);
                disableInputs();
            } else {
                clearInputs();
                enableInputs();
            }
        });

        //pay button click listener
        binding.paymentDetailsPayBtn.setOnClickListener(v -> {
            doPayment();
        });

    }

    private void doPayment() {
        String firstName = binding.paymentDetailsFirstName.getText().toString().trim();
        String lastName = binding.paymentDetailsLastName.getText().toString().trim();
        String addressLine1 = binding.paymentDetailsAddressLine1.getText().toString().trim();
        String addressLine2 = binding.paymentDetailsAddressLine2.getText().toString().trim();
        String city = binding.paymentDetailsCity.getText().toString().trim();
        String postalCode = binding.paymentDetailsPostalCode.getText().toString().trim();
        String mobileNumber = binding.paymentDetailsMobileNumber.getText().toString().trim();

        if (!isValidInputs(firstName, lastName, addressLine1, addressLine2, city, postalCode, mobileNumber)) {
            return;
        }

        Address address = Address.builder()
                .firstName(firstName)
                .lastName(lastName)
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .city(city)
                .postalCode(postalCode)
                .mobile(mobileNumber)
                .build();

        profileRepo.saveAddressDetails(address, false, addressId -> {
            this.addressId = addressId;
            Log.i("PaymentDetailsActivity", "Address saved with ID: " + addressId);
        });

        //payhere integration will be here
        profileRepo.getUserInitialData(auth.getCurrentUser().getUid(), this::pay);
    }

    private void pay(User user) {

        if (!paymentActive) return;

        InitRequest req = new InitRequest();
        req.setSandBox(true);

        req.setMerchantId("1225083");
        req.setMerchantSecret("MTkwOTQwNzg4ODUzNTQ4ODE1OTI1NzYwODM1NjAxOTg2NTk2ODAx");
        req.setCurrency("LKR");
        req.setAmount(total);
        req.setOrderId("ASOI-001");
        req.setItemsDescription("Order Description");

        req.getCustomer().setFirstName(binding.paymentDetailsFirstName.getText().toString());
        req.getCustomer().setLastName(binding.paymentDetailsLastName.getText().toString());
        req.getCustomer().setEmail(user.getEmail());
        req.getCustomer().setPhone(binding.paymentDetailsMobileNumber.getText().toString());
        req.getCustomer().getAddress().setAddress(binding.paymentDetailsAddressLine1.getText().toString() + " " + binding.paymentDetailsAddressLine2.getText().toString());
        req.getCustomer().getAddress().setCity(binding.paymentDetailsCity.getText().toString());
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        req.setNotifyUrl("https://astrae.requestcatcher.com/"); //request to this url to notify if it success or not

        Intent intent = new Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);

        payhereLauncher.launch(intent);

    }


    private boolean isValidInputs(String firstName, String lastName, String addressLine1, String addressLine2, String city, String postalCode, String mobileNumber) {
        if (firstName.isEmpty()) {
            Toast.makeText(this, "First name is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (lastName.isEmpty()) {
            Toast.makeText(this, "Last name is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addressLine1.isEmpty()) {
            Toast.makeText(this, "Address Line 1 is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addressLine2.isEmpty()) {
            Toast.makeText(this, "Address Line 2 is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (city.isEmpty()) {
            Toast.makeText(this, "City is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (postalCode.isEmpty()) {
            Toast.makeText(this, "Postal code is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mobileNumber.isEmpty()) {
            Toast.makeText(this, "Mobile number is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showAddressBottomSheet(User user) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_address_bottom_sheet, null);

        RecyclerView recyclerView = view.findViewById(R.id.bottom_sheet_address_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        profileRepo.getAllUserAddresses(user.getUId(), addresses -> {
            BottomDialogAddressAdapter adapter = new BottomDialogAddressAdapter(addresses, user.getPermanentAddressId(), address -> {
                this.addressId = address.getAddressId();
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

    private final ActivityResultLauncher<Intent> payhereLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

                if (result.getResultCode() == PHMainActivity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                        PHResponse<StatusResponse> response =
                                (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

                        if (response != null && response.isSuccess()) {
                            StatusResponse statusResponse = response.getData();

                            if (isCartBuying) {
                                // save order details to firestore here
                                paymentRepo.placeOrder(statusResponse, total, addressId, new PaymentRepository.OrderPlacementCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(getApplicationContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                        Log.i("PAYHERE", "Payment Successful! Transaction ID: " + statusResponse.getPaymentNo());

                                        Intent intent = new Intent(PaymentDetailsActivity.this, OrdersActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Toast.makeText(getApplicationContext(), "Failed to place order: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {

                                if (productId == null || quantity <= 0) {
                                    Toast.makeText(getApplicationContext(), "Invalid product or quantity", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                paymentRepo.placeOrder(statusResponse, total, addressId, productId, quantity, new PaymentRepository.OrderPlacementCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(getApplicationContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                        Log.i("PAYHERE", "Payment Successful! Transaction ID: " + statusResponse.getPaymentNo());

                                        Intent intent = new Intent(PaymentDetailsActivity.this, OrdersActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Toast.makeText(getApplicationContext(), "Failed to place order: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } else {
                            Log.e("PAYHERE", response.getData().getMessage());
                        }
                    }
                } else if (result.getResultCode() == PHMainActivity.RESULT_CANCELED) {
                    Log.e("PAYHERE", "Payment Cancelled!");
                }

            });



    private void disableInputs() {
        binding.paymentDetailsFirstName.setEnabled(false);
        binding.paymentDetailsLastName.setEnabled(false);
        binding.paymentDetailsAddressLine1.setEnabled(false);
        binding.paymentDetailsAddressLine2.setEnabled(false);
        binding.paymentDetailsCity.setEnabled(false);
        binding.paymentDetailsPostalCode.setEnabled(false);
        binding.paymentDetailsMobileNumber.setEnabled(false);
    }

    private void enableInputs() {
        binding.paymentDetailsFirstName.setEnabled(true);
        binding.paymentDetailsLastName.setEnabled(true);
        binding.paymentDetailsAddressLine1.setEnabled(true);
        binding.paymentDetailsAddressLine2.setEnabled(true);
        binding.paymentDetailsCity.setEnabled(true);
        binding.paymentDetailsPostalCode.setEnabled(true);
        binding.paymentDetailsMobileNumber.setEnabled(true);
    }

    private void clearInputs() {
        binding.paymentDetailsFirstName.setText("");
        binding.paymentDetailsLastName.setText("");
        binding.paymentDetailsAddressLine1.setText("");
        binding.paymentDetailsAddressLine2.setText("");
        binding.paymentDetailsCity.setText("");
        binding.paymentDetailsPostalCode.setText("");
        binding.paymentDetailsMobileNumber.setText("");
    }


}