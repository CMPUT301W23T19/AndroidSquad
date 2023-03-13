package com.example.myapplication;

/** Reference(s):
 * Using setOnItemSelectedListener to make bottom navigation bar functional:
 * -- From: www.stackoverflow.com
 * -- URL: https://stackoverflow.com/q/67641594
 * -- Author: https://stackoverflow.com/users/10429009/ali-moghadam
 *
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView playerRanks;
    ArrayAdapter<String> adapter;
    FirebaseFirestore db;
    BottomNavigationView bottomNavigationView;
    private CameraController cameraController;
    ActivityResultLauncher<ScanOptions> barLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        setContentView(R.layout.highest_scores);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        bottomNavigationView  = (BottomNavigationView)findViewById(R.id.nav_bar);

        // Testing leaderboard screen (highest scores)
        String names[] = {
                "Harrys", "Draco", "Ron", "Hermione"
        };
        getSupportActionBar().setTitle("Leaderboard");
        playerRanks = findViewById(R.id.player_ranks);
        adapter = new ArrayAdapter<String> (this, R.layout.userranks, R.id.username, names);
        playerRanks.setAdapter(adapter);

         barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if(result.getContents()!=null) {
                cameraController.handleScanResult(result.getContents());
            }
        });

        // Bottom Navigation bar functionality
        bottomNavigationView.setOnItemSelectedListener(item -> {
            cameraController = new CameraController(this, barLauncher,db);

            if (item.getItemId() == R.id.camera) {
                try {
                    cameraController.scanCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        });

        // Testing the add QR code to firebase functionality
        QRCodeController qrController = new QRCodeController("fggfg", "anna46", db);
        qrController.validateAndAdd();


    }
}