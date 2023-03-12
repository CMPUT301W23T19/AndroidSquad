package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearderboardActivity extends AppCompatActivity {
    ListView playerRanks;
    ArrayAdapter<String> adapter;
    ArrayList<String> names = new ArrayList<>();
    FirebaseFirestore db;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highest_scores);
        BottomNavigationView bottomNavigationView  = findViewById(R.id.nav_bar);

        getSupportActionBar().setTitle("Leaderboard");
        playerRanks = findViewById(R.id.player_ranks);
        adapter = new ArrayAdapter<String>(this, R.layout.userranks, R.id.username, names);
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

    }
 
}

