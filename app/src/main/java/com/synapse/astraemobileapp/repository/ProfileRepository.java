package com.synapse.astraemobileapp.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.model.Address;
import com.synapse.astraemobileapp.model.User;

import java.util.List;

public class ProfileRepository {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private Context context;

    public ProfileRepository(Context context) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        this.context = context;
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

}
