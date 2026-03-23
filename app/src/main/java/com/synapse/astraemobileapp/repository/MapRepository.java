package com.synapse.astraemobileapp.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.model.Address;
import com.synapse.astraemobileapp.model.User;

import java.util.Map;

public class MapRepository {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Context context;

    public MapRepository(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.context = context;
    }

    public void saveLocation(Map<String, Double> locationMap) {
        if (auth.getCurrentUser() == null) return;
        String uId = auth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(uId);
        docRef.update("location", locationMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Location updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e("MapRepository", "Failed to update location"));
    }

    public void getSellerMobile(String permanentAddressId, MobileCallback callback) {
        if (auth.getCurrentUser() == null) return;
        String uId = auth.getCurrentUser().getUid();
        db.collection("users").document(uId)
                .collection("addresses").document(permanentAddressId)
                .get()
                .addOnSuccessListener(ds -> {
                    Address permanentAddress = ds.toObject(Address.class);
                    if (permanentAddress == null) {
                        Log.e("MapRepository", "Permanent address not found");
                        return;
                    }
                    String mobile = permanentAddress.getMobile();
                    if (mobile == null) {
                        Log.e("MapRepository", "Mobile number not found in permanent address");
                        return;
                    }
                    callback.onMobileReceived(mobile);
                })
                .addOnFailureListener(e -> Log.e("MapRepository", "Failed to get seller mobile"));
    }

    public interface MobileCallback {
        void onMobileReceived(String mobile);
    }
}
