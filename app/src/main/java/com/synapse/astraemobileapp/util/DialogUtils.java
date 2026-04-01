package com.synapse.astraemobileapp.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.synapse.astraemobileapp.R;

public class DialogUtils {

    public static void showCustomDialog(Context context,
                                        String title,
                                        String description,
                                        String confirmBtnText,
                                        String cancelBtnText,
                                        CustomDialogClickCallback callback) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.item_custom_alert_dialog);

        Window window = dialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER_HORIZONTAL);

            WindowManager.LayoutParams params = window.getAttributes();

            params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }

        TextView titleText = dialog.findViewById(R.id.custom_dialog_title);
        TextView descriptionText = dialog.findViewById(R.id.custom_dialog_description);
        Button btnConfirm = dialog.findViewById(R.id.custom_dialog_confirm_btn);
        Button btnCancel = dialog.findViewById(R.id.custom_dialog_cancel_btn);

        dialog.setCancelable(false);

        titleText.setText(title);
        descriptionText.setText(description);
        btnConfirm.setText(confirmBtnText);
        btnCancel.setText(cancelBtnText);

        btnConfirm.setOnClickListener(v -> {
            callback.onConfirm();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            callback.onCancel();
            dialog.dismiss();
        });

        dialog.show();
    }

    public static void showCertificateDialog(Context context, String imageUrl) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.item_certificate_dialog);

        Window window = dialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.CENTER_HORIZONTAL);

            WindowManager.LayoutParams params = window.getAttributes();

            params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }

        dialog.findViewById(R.id.certificate_dialog_close_btn).setOnClickListener(v -> {
            dialog.dismiss();
        });

        // set certificate image

        ImageView imageView = dialog.findViewById(R.id.certificate_image_view);

        FirebaseStorage.getInstance().getReference("/" + imageUrl)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(imageView.getContext())
                            .load(uri)
                            .centerCrop()
                            .into(imageView);
                });

        dialog.show();
    }

    public interface CustomDialogClickCallback {
        void onConfirm();
        void onCancel();
    }
}
