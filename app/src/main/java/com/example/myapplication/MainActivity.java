package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView playerRanks;
    ArrayAdapter<String> adapter;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Testing leaderboard screen (highest scores)
        String names[] = {
                "Harry", "Draco", "Ron", "Hermione"
        };
        setContentView(R.layout.highest_scores);
        getSupportActionBar().setTitle("Leaderboard");
        playerRanks = findViewById(R.id.player_ranks);
        adapter = new ArrayAdapter<String> (this, R.layout.userranks, R.id.username, names);
        playerRanks.setAdapter(adapter);


    }
}