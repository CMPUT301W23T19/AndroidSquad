package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {
    ListView playerRanks;
    ArrayAdapter<String> adapter;
    ArrayList<String> names = new ArrayList<>();
    FirebaseFirestore db;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard);
        BottomNavigationView bottomNavigationView  = findViewById(R.id.nav_bar);

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

