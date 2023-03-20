package com.example.myapplication;

/**
 * Resource(s):
 * Returning to ParentActivity (HomePage / MainActivity):
 * --From: www.stackoverflow.com
 * --URL: https://stackoverflow.com/q/12276027
 * --Author: https://stackoverflow.com/users/3118/lorenzck
 * --License: CC BY-SA
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ScannedQRCodeActivity extends AppCompatActivity {
    private QRCodeControllerView qrControllerView;
    private FirebaseFirestore db;
    private ImageView face;
    private TextView QRname;
    private TextView QRscore;
    private TextView playerCount;
    private Button confirm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanned_qr_code);
        Intent intent = getIntent();
        String codeContents = intent.getStringExtra("contents");
        db = FirebaseFirestore.getInstance();

        qrControllerView = new QRCodeControllerView(codeContents, db);

        // get QR code visual representation to appear
        ArrayList<Integer> faces = qrControllerView.getAvatarlist();
        for (int i = 0; i < 5; i++ ) {
            face = findViewById(faces.get(i));
            face.setVisibility(View.VISIBLE);
        }

        // Get values for scanned QR code identity
        QRname = findViewById(R.id.name);
        QRscore = findViewById(R.id.qrScore);
        playerCount= findViewById(R.id.player_count);
        confirm = findViewById(R.id.confirm_button);

        QRname.setText(qrControllerView.getName());
        QRscore.setText(String.valueOf(qrControllerView.getScore()));
        calculatePlayerCount();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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


}
