package com.example.myapplication;

/** Reference(s):
 * Using setOnItemSelectedListener to make bottom navigation bar functional:
 * -- From: www.stackoverflow.com
 * -- URL: https://stackoverflow.com/q/67641594
 * -- Author: https://stackoverflow.com/users/10429009/ali-moghadam
 *
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarMenu;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView playerRanks;
    ArrayAdapter<String> adapter;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highest_scores);

        BottomNavigationView bottomNavigationView  = (BottomNavigationView) findViewById(R.id.nav_bar);

        // Testing leaderboard screen (highest scores)
        String names[] = {
                "Harry", "Draco", "Ron", "Hermione"
        };
        getSupportActionBar().setTitle("Leaderboard");
        playerRanks = findViewById(R.id.player_ranks);
        adapter = new ArrayAdapter<String> (this, R.layout.userranks, R.id.username, names);
        playerRanks.setAdapter(adapter);

        // Bottom Navigation bar functionality
        bottomNavigationView.setOnItemSelectedListener(item -> {
                    if (item.getItemId() == R.id.camera) {
                        try {
                            Intent intent = new Intent();
                            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                });

        // Testing the add QR code to firebase functionality
        QRCodeController qrController = new QRCodeController("BFG5DGW54", "anna46");
        qrController.validateAndAdd();
        //qrController.addQRCodetoDatabase();


    }



}