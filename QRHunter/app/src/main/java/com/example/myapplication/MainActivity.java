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

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity {
    TextView playerRanks;
    ArrayAdapter<String> adapter;
    FirebaseFirestore db;
    BottomNavigationView bottomNavigationView;
    private CameraController cameraController;
    private QRCodeControllerDB qrCodeControllerDB;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.camera);

        setContentView(R.layout.home_page);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        bottomNavigationView  = (BottomNavigationView)findViewById(R.id.nav_bar);

        // Testing leaderboard screen (highest scores)
        String names[] = {
                "Harrys", "Draco", "Ron", "Hermione"
        };

        playerRanks = findViewById(R.id.player_ranks);
        adapter = new ArrayAdapter<String> (this, R.layout.userranks, R.id.username, names);

        ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if(result.getContents()!=null) {
                cameraController.handleScanResult(result.getContents(), db, this);
            }
        });


        // Bottom Navigation bar functionality
        bottomNavigationView.setOnItemSelectedListener(item -> {

            cameraController = new CameraController(this, barLauncher);
            if (item.getItemId() == R.id.camera) {
                try {
                    cameraController.scanCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (item.getItemId() == R.id.history) {
                try {
                    Intent intent = new Intent(this,HistoryActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        });



        // Get highest and lowest scores, sum of scores, total number of QR player scanned
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

        // Set the leaderboard to be clickable
        // Transitions between home page to leaderboard page
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
