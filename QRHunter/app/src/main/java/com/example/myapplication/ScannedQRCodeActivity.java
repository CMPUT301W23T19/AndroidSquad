package com.example.myapplication;

/**
 * Resource(s):
 * Returning to ParentActivity (HomePage / MainActivity):
 * --From: www.stackoverflow.com
 * --URL: https://stackoverflow.com/q/12276027
 * --Author: https://stackoverflow.com/users/3118/lorenzck
 * --License: CC BY-SA
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/** Activity class that displays information about newly scanned QR code */
public class ScannedQRCodeActivity extends AppCompatActivity {
    private QRCodeControllerView qrControllerView;
    private FirebaseFirestore db;
    private ImageView face;
    private TextView QRname;
    private TextView QRscore;
    private TextView playerCount;
    private Button confirm;
    private FusedLocationProviderClient locationProviderClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanned_qr_code);
        Intent intent = getIntent();
        QRCode qrCode = (QRCode) intent.getSerializableExtra("qrCode");
        db = FirebaseFirestore.getInstance();

        qrControllerView = new QRCodeControllerView(qrCode, db);
        locationProviderClient = new FusedLocationProviderClient(this);

        // get QR code visual representation to appear
        ArrayList<Integer> faces = qrControllerView.getAvatarlist();
        for (int i = 0; i < 5; i++) {
            face = findViewById(faces.get(i));
            face.setVisibility(View.VISIBLE);
        }

        // Get values for scanned QR code identity
        QRname = findViewById(R.id.name);
        QRscore = findViewById(R.id.qrScore);
        playerCount = findViewById(R.id.player_count);
        confirm = findViewById(R.id.confirm_button);

        QRname.setText(qrControllerView.getName());
        QRscore.setText(String.valueOf(qrControllerView.getScore()));
        calculatePlayerCount();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScannedQRCodeActivity.this);
                builder.setTitle("Do you want to store the location of this QR code?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // get location of user
                                // store in database
                                // TODO: Get location permission from camera (pass it)
                                if (ActivityCompat.checkSelfPermission(ScannedQRCodeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ScannedQRCodeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                locationProviderClient.getLastLocation();
                                getPhotoDialog().show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getPhotoDialog().show();
                            }
                        })
                        .show();
            }
        });

    }

    /**
     * Gets the number of users who have scanned the QR code
     */
    public void calculatePlayerCount() {
        db.collection("QR Code")
                .document(qrControllerView.getName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            Log.d("TAG", "Successfully accessed usernames (to be counted) in QR code Database!");
                            ArrayList<String> usernames = (ArrayList<String>) task.getResult().get("Username");
                            for (int i = 0; i < usernames.size(); i++) {
                                count += 1;
                            }
                            if (count == 1) {
                                count = 0;
                            }
                            playerCount.setText(String.valueOf(count) + " other player(s) scanned this QR code!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Failed to get usernames");
                    }
                });
    }

    public AlertDialog getPhotoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScannedQRCodeActivity.this);
        return builder.setTitle("Do you want to store a photo of the QR Code?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // open camera (disable scan)
                        // take picture, allow them to cancel, store if they confirm
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();

    }



}
