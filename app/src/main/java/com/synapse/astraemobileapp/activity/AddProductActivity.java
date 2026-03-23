package com.synapse.astraemobileapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.synapse.astraemobileapp.R;
import com.synapse.astraemobileapp.databinding.ActivityAddProductBinding;
import com.synapse.astraemobileapp.model.Category;
import com.synapse.astraemobileapp.model.Product;
import com.synapse.astraemobileapp.repository.ProductRepository;
import com.synapse.astraemobileapp.util.DialogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding binding;

    private FirebaseStorage storage;
    private FirebaseAuth auth;

    private ProductRepository productRepo;

    private String selectedCategoryId;

    private Uri[] imageUris = new Uri[4];
    private int currentSlot = 0;

    private Uri certificateImageUri;

    private boolean isTermsAgreed = false;

    private static final int MAX_IMAGE_COUNT = 4;

    private int totalFilesToUpload = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(AddProductActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.productRepo = new ProductRepository(this);

        loadCategoriesToTextView();

        binding.addProductCategory.setOnClickListener(v -> {
            binding.addProductCategory.setDropDownBackgroundResource(R.drawable.item_popup_bg);
            binding.addProductCategory.showDropDown();
        });

        binding.addProductAddBtn.setOnClickListener(view -> {
            saveProduct();
        });

        binding.addProductImageSelector.setOnClickListener(v -> {
            for (int i = 0; i < imageUris.length; i++) {
                if (imageUris[i] == null) {
                    currentSlot = i;
                    pickerLauncher.launch("image/*");
                    break;
                }
            }
        });

        binding.addProductUploadCertificate.setOnClickListener(v -> {
            certificatePickerLauncher.launch("image/*");
        });

        binding.addProductTermsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (!isChecked) {
                isTermsAgreed = false;
                return;
            }

            DialogUtils.showCustomDialog(
                    this,
                    "Terms and Conditions",
                    "After adding a product, it may can go 3 days for get admin approval. " +
                            "Admin has full authority to accept or decline you product after reviewing it. \n\n" +
                            "If your product will approve, it shows in the application. Otherwise, it doesn't.",
                    "I Agree",
                    "Decline",
                    new DialogUtils.CustomDialogClickCallback() {
                        @Override
                        public void onConfirm() {
                            isTermsAgreed = true;
                        }

                        @Override
                        public void onCancel() {
                            isTermsAgreed = false;
                            binding.addProductTermsCheckbox.setChecked(false);
                        }
                    }

            );
        });

    }

    private final ActivityResultLauncher<String> pickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    binding.addProductSelectedImagesContainer.setVisibility(View.VISIBLE);
                    imageUris[currentSlot] = uri;
                    updateUI(currentSlot, uri);
                }
            }
    );

    private final ActivityResultLauncher<String> certificatePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    certificateImageUri = uri;
                    binding.addProductCertUploadStatus.setVisibility(View.VISIBLE);
                }
            }
    );

    private void updateUI(int slot, Uri uri) {
        ImageView[] views = {binding.img1, binding.img2, binding.img3, binding.img4};
        views[slot].setImageURI(uri);
        views[slot].setVisibility(ImageView.VISIBLE);
    }


    private void saveProduct() {
        String title = binding.addProductProductTitle.getText().toString().trim();
        String description = binding.addProductProductDescription.getText().toString().trim();
        String price = binding.addProductPrice.getText().toString().trim();
        String category = binding.addProductCategory.getText().toString().trim();
        String size = binding.addProductSize.getText().toString().trim();
        String metal = binding.addProductMetal.getText().toString().trim();
        String quantity = binding.addProductQuantity.getText().toString();

        boolean isValid = validateInputs(title, description, price, category, size, metal, quantity);

        if (!isValid) return;

        if (!isTermsAgreed) {
            Toast.makeText(this, "You must agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        //check if seller added phone number and geo location in their profile.
        productRepo.isDataFilled(auth.getCurrentUser().getUid(), isFilled -> {

            if (isFilled) {
                productRepo.getNewProductId(newProductId -> {

                    if(newProductId == null) {
                        Toast.makeText(this, "Failed to generate product ID. Please try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    uploadImagesToFirebase(newProductId, (productImageUrls, certificateUrl) -> {

                        Product product = Product.builder()
                                .productId(newProductId)
                                .title(title)
                                .description(description)
                                .price(Double.parseDouble(price))
                                .stockCount(Integer.parseInt(quantity))
                                .categoryId(selectedCategoryId)
                                .sellerId(auth.getCurrentUser().getUid())
                                .productImages(productImageUrls)
                                .certificateImage(certificateUrl)
                                .status("PENDING")
                                .rating(0.0f)
                                .soldCount(0)
                                .viewedCount(0)
                                .attributes(List.of(
                                        Product.Attribute.builder().name("Size").values(List.of(size)).build(),
                                        Product.Attribute.builder().name("Metal").values(List.of(metal)).build()
                                ))
                                .build();

                        productRepo.saveProduct(product);
                    });
                });
            } else {
                Toast.makeText(this, "Please complete your profile details first", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean validateInputs(
            String title,
            String description,
            String price,
            String category,
            String size,
            String metal,
            String quantity) {

        if (title.isEmpty()) {
            binding.addProductProductTitle.setError("Title is required");
            return false;
        }
        if (description.isEmpty()) {
            binding.addProductProductDescription.setError("Description is required");
            return false;
        }
        if (price.isEmpty()) {
            binding.addProductPrice.setError("Price is required");
            return false;
        }
        if (category.isEmpty()) {
            binding.addProductCategory.setError("Category is required");
            return false;
        }

        if (size.isEmpty()) {
            binding.addProductSize.setError("Size is required");
            return false;
        }
        if (metal.isEmpty()) {
            binding.addProductMetal.setError("Metal type is required");
            return false;
        }
        if (quantity.isEmpty()) {
            binding.addProductQuantity.setError("Quantity is required");
            return false;
        }

        try {
            Double.parseDouble(price);
        } catch (NumberFormatException e) {
            binding.addProductPrice.setError("Invalid price format");
            return false;
        }

        try {
            Integer.parseInt(quantity);
        } catch (NumberFormatException e) {
            binding.addProductQuantity.setError("Invalid quantity format");
            return false;
        }

        if (imageUris[0] == null || imageUris[1] == null || imageUris[2] == null || imageUris[3] == null) {
            Toast.makeText(this, "Please select 4 product images", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (certificateImageUri == null) {
            Toast.makeText(this, "Please upload the certificate image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }


    private void loadCategoriesToTextView() {
        productRepo.getAllCategories(categories -> {

            List<String> categoryNames = new ArrayList<>();
            categories.values().forEach(category -> {
                categoryNames.add(category.getName());
            });

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    R.layout.item_category_list,
                    categoryNames
            );

            AutoCompleteTextView categoryTextView = binding.addProductCategory;
            categoryTextView.setAdapter(adapter);

            categoryTextView.setOnItemClickListener((parent, view, position, id) -> {
                String selectedCategory = categoryNames.get(position);
                if (selectedCategory.isEmpty()) {
                    return;
                }
                Category categoryObj = categories.get(selectedCategory);
                if (categoryObj == null) {
                    return;
                }
                selectedCategoryId = categoryObj.getCategoryId();
            });
        });
    }


    private void uploadImagesToFirebase(String newProductId, UploadCallback callback) {
        List<String> productImageUrls = new ArrayList<>();
        final String[] certificateUrl = {null};


        for (Uri u : imageUris) if (u != null) totalFilesToUpload++;
        if (certificateImageUri != null) totalFilesToUpload++;

        if (totalFilesToUpload == 0) {
            Toast.makeText(this, "Please select images and certificate", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading (0/" + totalFilesToUpload + ")...");
        pd.show();

        // Upload Product Images
        for (int i = 0; i < imageUris.length; i++) {
            Uri uri = imageUris[i];
            if (uri == null) continue;

            int index = i + 1;
            String urlPath = "product-images/" + newProductId + "/image" + index + ".jpg";
            StorageReference ref = storage.getReference(urlPath);

            ref.putFile(uri).addOnSuccessListener(task -> ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                productImageUrls.add(urlPath); // Save the FULL URL
                checkTotalProgress(pd, productImageUrls, certificateUrl[0], totalFilesToUpload, callback);
            }));
        }

        // Upload Certificate
        String certPath = "certificate-images/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference certRef = storage.getReference(certPath);
        certRef.putFile(certificateImageUri).addOnSuccessListener(task -> certRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
            certificateUrl[0] = certPath;
            checkTotalProgress(pd, productImageUrls, certificateUrl[0], totalFilesToUpload, callback);
        }));
    }

    private void checkTotalProgress(ProgressDialog pd, List<String> pImages, String certUrl, int total, UploadCallback callback) {
        int currentCount = pImages.size() + (certUrl != null ? 1 : 0);
        pd.setMessage("Uploading (" + currentCount + "/" + total + ")...");

        if (currentCount == total) {
            pd.dismiss();
            // NOW both are ready! Save to Firestore
            callback.onUploadComplete(pImages, certUrl);
        }
    }

    public interface UploadCallback {
        void onUploadComplete(List<String> productImageUrls, String certificateUrl);
    }
}