package com.synapse.astraemobileapp.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.synapse.astraemobileapp.model.Address;
import com.synapse.astraemobileapp.model.User;

import java.util.List;
import java.util.UUID;

public class ProfileRepository {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    private Context context;

    private ImageView sideNavHeaderImage;

    public ProfileRepository(Context context) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        this.context = context;
    }

    public void setSideNavImageView(ImageView imageView) {
        this.sideNavHeaderImage = imageView;
    }

    public void isPermanentAddressFilled(ProfileStatusCallback profileCallback) {

        if (auth.getCurrentUser() == null) {
            Log.e("ProfileRepository", "current user is null");
            return;
        }

        String uId = auth.getCurrentUser().getUid();
        db.collection("users")
                .whereEqualTo("uid", uId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObjects(User.class).get(0);
                        if (user != null && user.getPermanentAddressId() != null) {

                            if (user.getPermanentAddressId().isEmpty()) {
                                profileCallback.callback(false);
                                return;
                            }
                            profileCallback.callback(true);

                        } else {
                            profileCallback.callback(false);
                        }
                    }
                });
    }

    public void getUserInitialData(String uId, UserInitialCallback callback) {
        db.collection("users").document(uId)
                .get()
                .addOnSuccessListener(ds -> {
                    if (ds != null) {
                        User user = ds.toObject(User.class);
                        if (user != null) callback.callback(user);
                    }

                })
                .addOnFailureListener(e -> Log.e("ProfileRepository", "Failed to retrieve user"));
    }


    public void saveAddressDetails(Address address, UpdateProfileCallback callback) {

        if (auth.getCurrentUser() == null) return;

        checkAddressAvailability(auth.getCurrentUser().getUid(), address, isAvailable -> {

            if (!isAvailable) {
                CollectionReference addressRef = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(auth.getCurrentUser().getUid())
                        .collection("addresses");

                DocumentReference newDoc = addressRef.document();
                String newAddressId = newDoc.getId();

                newDoc.set(address)
                        .addOnSuccessListener(unused -> {
                            callback.callback(newAddressId);
                            Toast.makeText(context, "Details saved successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to save details", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(context, "This address already exist", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void updatePermanentAddressId(String id, User user) {
        user.setPermanentAddressId(id);
        db.collection("users")
                .document(user.getUId())
                .set(user)
                .addOnSuccessListener(unused -> {
                    Log.i("ProfileRepository", "Permanent address Id updated");
                })
                .addOnFailureListener(e -> Log.i("ProfileRepository", "Failed to update permanent address Id"));
    }

    public void getPermanentAddress(String uId, String permanentAddressId, PermanentAddressCallback callback) {
        db.collection("users")
                .document(uId)
                .collection("addresses")
                .document(permanentAddressId)
                .get()
                .addOnSuccessListener(ds -> {
                    Address address = ds.toObject(Address.class);
                    callback.callback(address);
                })
                .addOnFailureListener(e -> Log.i("ProfileRepository", "Failed to retrieve permanent address"));
    }


    public void checkAddressAvailability(String uId, Address permanentAddress, AddressAvailabilityCallback callback) {
        db.collection("users")
                .document(uId)
                .collection("addresses")
                .get()
                .addOnSuccessListener(qds -> {
                    List<Address> addresses = qds.toObjects(Address.class);
                    if (!addresses.isEmpty()) {
                        boolean alreadyExists = addresses.stream().anyMatch(existing ->
                                        existing.getFirstName().equalsIgnoreCase(permanentAddress.getFirstName()) &&
                                                existing.getLastName().equalsIgnoreCase(permanentAddress.getLastName()) &&
                                                existing.getAddressLine1().equalsIgnoreCase(permanentAddress.getAddressLine1()) &&
                                                existing.getMobile().equals(permanentAddress.getMobile())
                        );
                        callback.callback(alreadyExists);
                        return;
                    }
                    callback.callback(false);
                })
                .addOnFailureListener(e -> Log.e("ProfileRepository", "Failed to check address availability"));
    }


    public void getAllUserAddresses(String uId, AllUserAddressesCallback callback) {
        db.collection("users")
                .document(uId)
                .collection("addresses")
                .get()
                .addOnSuccessListener(ds -> {
                    List<Address> addresses = ds.toObjects(Address.class);
                    callback.callback(addresses);
                })
                .addOnFailureListener(e -> Log.e("ProfileRepository", "Failed to retrieve user addresses"));
    }


    public void storeProfileImage(Uri uri, User user) {

        if (user == null) {
            Log.e("ProfileRepository", "current user is null");
            return;
        }

        String imageId = UUID.randomUUID().toString();

        StorageReference imageReference = storage.getReference("profile-images").child(imageId);
        imageReference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {

                    db.collection("users").document(user.getUId())
                            .update("profilePicUrl", imageId)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Profile image changed", Toast.LENGTH_SHORT).show();

                                if (sideNavHeaderImage == null) return;
                                loadProfileImage(user, sideNavHeaderImage);
                            });

                });

    }


    public void loadProfileImage(User user, ImageView imageView) {

        if (user.getProfilePicUrl() == null || user.getProfilePicUrl().isEmpty()) {
            return;
        }

        storage.getReference("profile-images/" + user.getProfilePicUrl()).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(context)
                            .load(uri)
                            .circleCrop()
                            .into(imageView);
                });
    }


    public interface ProfileStatusCallback {
        void callback(boolean isFilled);
    }

    public interface UserInitialCallback {
        void callback(User user);
    }

    public interface UpdateProfileCallback {
        void callback(String addressId);
    }

    public interface PermanentAddressCallback {
        void callback(Address permanentAddress);
    }

    public interface AddressAvailabilityCallback {
        void callback(boolean isAvailable);
    }

    public interface AllUserAddressesCallback {
        void callback(List<Address> addresses);
    }

}
