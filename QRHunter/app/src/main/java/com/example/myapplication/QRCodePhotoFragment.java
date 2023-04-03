package com.example.myapplication;

/**
 * Resource(s):
 * Using Glide to display photo from Firebase Storage
 * --From: www.stackoverflow.com
 * --URL: https://stackoverflow.com/q/41737271
 * --Author: https://stackoverflow.com/users/4654957/diego-venâncio
 * --License: CC BY-SA
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Displays the photo of the QR code's location take by the user, if it exists
 */

public class QRCodePhotoFragment extends DialogFragment {
    private String qrName;
    private Uri uri;
    private String username;

    public QRCodePhotoFragment(String qrName, String username, Uri uri) {
        this.qrName = qrName;
        this.username = username;
        this.uri = uri;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.qr_photo, null);
        ImageView photo = view.findViewById(R.id.photo);
        TextView title = view.findViewById(R.id.title);

        Bundle args = getArguments();
        if (args != null) {
            qrName = (String) args.getSerializable("qrName");
        }

        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton("Exit Box", null)
                .create();

        if (uri != null) {
            title.setText("Photo of " + qrName);
            Glide.with(getContext())
                    .load(uri)
                    .into(photo);
        } else {
            photo.getLayoutParams().height = 5;
            title.setText("No photo found...");
        }

        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onShow(DialogInterface dialog) {
                builder.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(R.color.testerPurple);
                builder.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(16);
                builder.getWindow().setBackgroundDrawableResource(R.drawable.rounded_view);
            }
        });
        return builder;

    }
}
