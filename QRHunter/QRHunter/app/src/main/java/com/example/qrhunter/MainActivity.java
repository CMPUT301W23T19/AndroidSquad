package com.example.qrhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    public ListView playerRanks;
    public ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Test out leaderboard
        String names[] = {
                "Harry", "Draco", "Ron", "Hermione"
        };
        setContentView(R.layout.highest_scores);        // using highest_scores.xml to test
        getSupportActionBar().setTitle("Leaderboard");
        playerRanks = findViewById(R.id.player_ranks);
        adapter = new ArrayAdapter<String>(this, R.layout.userranks, R.id.username, names);
        playerRanks.setAdapter(adapter);


    }

}