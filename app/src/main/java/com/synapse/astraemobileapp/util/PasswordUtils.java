package com.synapse.astraemobileapp.util;

import android.annotation.SuppressLint;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.EditText;

import com.synapse.astraemobileapp.R;

public class PasswordUtils {
    @SuppressLint("ClickableViewAccessibility")
    public static void togglePasswordVisibility(EditText passwordEditText) {

        final boolean[] isPasswordVisible = {false};

        passwordEditText.setOnTouchListener((v, event) -> {

            final int DRAWABLE_END = 2;

            if (passwordEditText.getCompoundDrawables()[DRAWABLE_END] != null) {

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    int drawableWidth = passwordEditText.getCompoundDrawables()[DRAWABLE_END].getBounds().width();
                    int iconAreaStart = passwordEditText.getRight() - drawableWidth - passwordEditText.getPaddingEnd();

                    if (event.getRawX() >= iconAreaStart) {
                        isPasswordVisible[0] = !isPasswordVisible[0];

                        if (isPasswordVisible[0]) {
                            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_input_hidden, 0);
                        } else {
                            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_input_show, 0);
                        }

                        passwordEditText.setSelection(passwordEditText.getText().length());
                        return true;
                    }

                }
            }

            return false;

        });
    }
}
