package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ScannedQRCodeActivity extends AppCompatActivity {
    private QRCodeControllerView qrControllerView;
    private FirebaseFirestore db;
    private ImageView face;
    private TextView QRname;
    private TextView QRscore;
    private TextView playerCount;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanned_qr_code);
        db = FirebaseFirestore.getInstance();
        qrControllerView = new QRCodeControllerView("BFG5DG154", "anna46", db);

        // get QR code visual representation to appear
        ArrayList<Integer> faces = qrControllerView.getAvatarlist();

        for (int i = 0; i < 5; i++ ) {
            face = findViewById(faces.get(i));
            face.setVisibility(View.VISIBLE);
        }

        QRname = findViewById(R.id.name);
        QRscore = findViewById(R.id.qrScore);
        playerCount= findViewById(R.id.player_count);

        QRname.setText(qrControllerView.getName());
        QRscore.setText(String.valueOf(qrControllerView.getScore()));

        qrControllerView.playerCount();

    }

}
