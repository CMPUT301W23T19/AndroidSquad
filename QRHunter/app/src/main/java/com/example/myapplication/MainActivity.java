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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarMenu;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    TextView leaderboardText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_page);
        BottomNavigationView bottomNavigationView  = (BottomNavigationView) findViewById(R.id.nav_bar);
        db = FirebaseFirestore.getInstance();

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
        String playerName = "anna46";
        Query query = playerRef.whereEqualTo("Username", playerName);
        //Query query = playerRef.orderBy("Score",Query.Direction.DESCENDING);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                long highestScore = document.getLong("highestScore");
                long lowestScore = document.getLong("lowestScore");
                long qrCount = ((ArrayList<String>)document.get("QRcode")).size();
                long totalScore = document.getLong("Score");
                String playerRanksText = "Highest score: " + highestScore + "\n" + "Lowest score: " + lowestScore+"\nQR scanned: "+qrCount
                        +"\nTotal Score: " + totalScore;
                TextView playerRankTextView = findViewById(R.id.player_ranks);
                playerRankTextView.setText(playerRanksText);

            }
        });

        // Retrieve game-wide high scores
        CollectionReference playersRef = db.collection("Player");
        Query query1 = playersRef.orderBy("Score", Query.Direction.DESCENDING);
        query1.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int rank = 1;
                StringBuilder sb = new StringBuilder();
                sb.append("Rank\tPlayers\tHigh Score\n");

                for (DocumentSnapshot document : task.getResult()) {
                    String name = document.getString("Name");
                    long Score = document.getLong("Score");

                    sb.append(rank).append("\t").append(name).append("\t").append(Score).append("\n");
                    rank++;
                }
                ((TextView)findViewById(R.id.leaderboard_text)).setText(sb);
            }
        });


        TextView leaderboardText = findViewById(R.id.leaderboard_text);
        leaderboardText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });
    }
}
