package com.synapse.astraemobileapp.util;

import android.util.Patterns;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return !email.isEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return !password.isEmpty() && password.length() >= 6;
    }

    public static boolean isValidMobile(String mobile) {
        return !mobile.isEmpty() &&
                mobile.matches("^07[01245678][0-9]{7}$");
    }

    public static boolean isValidPostalCode(String postalCode) {
        return !postalCode.isEmpty() &&
                postalCode.matches("^[0-9]{5}$");
    }
}
