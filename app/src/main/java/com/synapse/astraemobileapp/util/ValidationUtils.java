package com.synapse.astraemobileapp.util;

import android.util.Patterns;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return email.isEmpty() ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password.isEmpty() || password.length() < 6;
    }
}
