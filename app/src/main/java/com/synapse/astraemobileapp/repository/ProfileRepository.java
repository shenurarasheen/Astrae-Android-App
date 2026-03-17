package com.synapse.astraemobileapp.repository;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.model.User;

public class ProfileRepository {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public ProfileRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
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
                            profileCallback.callback(true);
                        } else {
                            profileCallback.callback(false);
                        }
                    }
                });
    }

    public interface ProfileStatusCallback{
        void callback(boolean isFilled);
    }

}
