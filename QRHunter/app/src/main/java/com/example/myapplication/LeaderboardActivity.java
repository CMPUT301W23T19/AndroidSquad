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
import java.util.HashMap;
import java.util.List;

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

        db = FirebaseFirestore.getInstance();
        CollectionReference playerRef = db.collection("Player");
        Query query = playerRef.orderBy("highestScore", Query.Direction.DESCENDING);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<HashMap<String, Object>> players = new ArrayList<>();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                HashMap<String, Object> playerData = new HashMap<>();
                playerData.put("Username", document.getString("Username"));
                playerData.put("highestScore", document.getLong("highestScore"));
                players.add(playerData);
            }

            // Find the rank of the player with the given name
            String userName = "anna46";
            int playerRank = -1;
            for (int i = 0; i < players.size(); i++) {
                String name = (String) players.get(i).get("Username");
                if (name.equals(userName)) {
                    playerRank = i + 1;
                    break;
                }
            }

            // Set the text of the playerRank TextView to display the player's rank
            String playerRankText = "Your rank(highest scoring): " + playerRank;
            TextView playerRankTextView = findViewById(R.id.player_rank);
            playerRankTextView.setText(playerRankText);
        });

    }
 
}

