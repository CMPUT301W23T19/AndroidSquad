package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class LearderboardActivity extends AppCompatActivity {
    ListView playerRanks;
    ArrayAdapter<String> adapter;
    FirebaseFirestore db;
    String username;

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

