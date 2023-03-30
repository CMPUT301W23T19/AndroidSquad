package com.example.myapplication;

/** Reference(s):
 * Using setOnItemSelectedListener to make bottom navigation bar functional:
 * -- From: www.stackoverflow.com
 * -- URL: https://stackoverflow.com/q/67641594
 * -- Author: https://stackoverflow.com/users/10429009/ali-moghadam
 *
 */

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private Player currentPlayer;

    //test
    ActivityResultLauncher<Intent> forResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Log.e("MainActivity: ", "I think Signup Activity is done?");
            Log.e("MainActivity: the result is: ", result.toString());
            if (result != null && result.getResultCode() == RESULT_OK) {

                Bundle bundle = result.getData().getExtras();

                currentPlayer = (Player)bundle.getSerializable("CurrentUser");
                Log.e("MainActivity: ", "User " + currentPlayer.getUsername());


                long highestScore = (long) currentPlayer.getHighestscore();
                long lowestScore = (long) currentPlayer.getLowestscore();
                long qrCount = currentPlayer.getQrcode().size();
                long totalScore = (long) currentPlayer.getScore();
                String playerRanksText = "Highest score: " + highestScore + "\n" + "Lowest score: " + lowestScore+"\nQR scanned: "+qrCount
                        +"\nTotal Score: " + totalScore;
                TextView playerRankTextView = findViewById(R.id.player_ranks);
                playerRankTextView.setText(playerRanksText);
            }
        }
    });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.camera);
        setContentView(R.layout.mapp);

        setContentView(R.layout.home_page);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        bottomNavigationView  = (BottomNavigationView)findViewById(R.id.nav_bar);

        ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if(result.getContents()!=null) {
                cameraController.handleScanResult(result.getContents(), db, this, currentPlayer.getUsername());
            }
        });


        // Bottom Navigation bar functionality
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.map) {
                try {
                    Intent intent = new Intent(this,MapActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (item.getItemId() == R.id.profile) {
                try {
                    Intent intent = new Intent(this,ProfileActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

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
                    intent.putExtra("username", currentPlayer.getUsername());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        });

        Intent signup = new Intent(MainActivity.this, SignUpActivity.class);
        forResult.launch(signup);

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
        Query query1 = playersRef.orderBy("Score", Query.Direction.DESCENDING).limit(10);
        query1.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int rank = 1;
                StringBuilder sb = new StringBuilder();

                for (DocumentSnapshot document : task.getResult()) {
                    String name = document.getString("Name");
                    long Score = document.getLong("Score");

                    sb.append(rank).append("\t").append(name).append("\t").append(Score).append("\n");
                    rank++;
                }
                ((TextView)findViewById(R.id.leaderboard_text)).setText(String.valueOf(sb) + '.');
            }
        });

        // Set the leaderboard to be clickable
        // Transitions between home page to leaderboard page
        TextView leaderboardText = findViewById(R.id.view_more);
        leaderboardText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });

    }
}
