package com.example.myapplication;

/** Reference(s):
 * Using setOnItemSelectedListener to make bottom navigation bar functional:
 * -- From: www.stackoverflow.com
 * -- URL: https://stackoverflow.com/q/67641594
 * -- Author: https://stackoverflow.com/users/10429009/ali-moghadam
 * -- License: CC BY-SA
 *
 * Setting the appropriate icon on navigation bar to be highlighted
 * -- From: www.stackoverflow.com
 * -- URL: https://stackoverflow.com/q/41744219
 * -- Author: https://stackoverflow.com/users/5227265/hardanger
 * -- License: CC BY-SA
 */

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarMenu;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    BottomNavigationView bottomNavigationView;
    private CameraController cameraController;
    private static Player currentPlayer;
    private Button search;
    private TextView name;

    ActivityResultLauncher<Intent> forResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            Log.e("MainActivity: ", "I think Signup Activity is done?");
            Log.e("MainActivity: the result is: ", result.toString());
            if (result != null && result.getResultCode() == RESULT_OK) {
                Bundle bundle = result.getData().getExtras();

                // Sending image with Player class would cause size too large error, so avatar field is null in local
                // If you need avatar, you can ask firestore to find current user and fetch avatar in callback function (i.e. OnSuccessfulListener)
                currentPlayer = (Player)bundle.getSerializable("CurrentUser");
                Log.e("MainActivity: ", "User " + currentPlayer.getUsername());
                name = findViewById(R.id.name);
                name.setText(currentPlayer.getUsername());

                Integer highestScore = currentPlayer.getHighestscore();
                Integer lowestScore = currentPlayer.getLowestscore();
                Integer qrCount = currentPlayer.getQrcode().size();
                Integer totalScore =  currentPlayer.getScore();
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

        setContentView(R.layout.home_page);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        search = (Button) findViewById(R.id.search_button);

        Intent signup = new Intent(MainActivity.this, SignUpActivity.class);
        forResult.launch(signup);
        bottomNavigationView  = (BottomNavigationView)findViewById(R.id.nav_bar);
        leaderboardScores();
        getMostScanned();

        // Update Home page
        ActivityResultLauncher<Intent> updateScores = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Log.e("Updating scores","yes");
                    Log.e("Username", currentPlayer.getUsername());
                    db.collection("Player").document(currentPlayer.getUsername())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    long highestScore = (long) task.getResult().get("highestScore");
                                    long lowestScore = (long) task.getResult().get("lowestScore");
                                    long qrCount = ((ArrayList<String>) task.getResult().get("QRcode")).size();
                                    long totalScore = (long) task.getResult().get("Score");
                                    String playerRanksText = "Highest score: " + highestScore + "\n" + "Lowest score: " + lowestScore + "\nQR scanned: " + qrCount
                                            + "\nTotal Score: " + totalScore;
                                    TextView playerRankTextView = findViewById(R.id.player_ranks);
                                    playerRankTextView.setText(playerRanksText);
                                }
                            });
                    Menu menu = bottomNavigationView.getMenu();
                    MenuItem menuItem = menu.getItem(0);
                    menuItem.setChecked(true);
                    leaderboardScores();
                    getMostScanned();
                }
            }
        });

        ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if(result.getContents()!=null) {
                cameraController.handleScanResult(result.getContents(), db, this, currentPlayer.getUsername(), updateScores);
            }
        });

        // Bottom Navigation bar functionality
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.map) {
                try {
                    Intent intent = new Intent(this,MapActivity.class);
                    intent.putExtra("username", currentPlayer.getUsername());
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
                    updateScores.launch(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        });



        // Set the leaderboard to be clickable
        // Transitions between home page to leaderboard page
        TextView leaderboardText = findViewById(R.id.view_more);
        leaderboardText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
                intent.putExtra("currentUser", currentPlayer);
                startActivity(intent);
            }
        });


        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);

            }
        });
    }

    /**
     * Displays top ten users based on total scores
     */
    public void leaderboardScores() {
        // Retrieve game-wide high scores
        CollectionReference playersRef = db.collection("Player");
        Query query1 = playersRef.orderBy("Score", Query.Direction.DESCENDING).limit(10);
        query1.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int rank = 1;
                StringBuilder sb = new StringBuilder();
                for (DocumentSnapshot document : task.getResult()) {
                    String name = document.getString("Name");
                    long Score = document.getLong("Score").intValue();

                    sb.append(rank).append("\t").append(name).append("\t").append(Score).append("\n");
                    rank++;
                }
                ((TextView)findViewById(R.id.leaderboard_text)).setText(String.valueOf(sb) + '.');
            }
        });
    }

    /**
     * Displays top ten most scanned qr codes
     */
    public void getMostScanned() {
        TextView mostScanned = findViewById(R.id.most_scanned_text);
        CollectionReference qrCodeRef = db.collection("QR Code");
        qrCodeRef.orderBy("Player Count", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<String> features;
                        for (DocumentSnapshot qrCode : task.getResult().getDocuments()) {
                            mostScanned.setText((String)qrCode.get("Name"));
                            features = (ArrayList<String>) qrCode.get("Avatar");

                            //Get avatar list
                            HashMap<Integer, Integer[]> faces = new HashMap<>();
                            faces.put(0, new Integer[]{R.id.face1, R.id.face2});
                            faces.put(1, new Integer[]{R.id.eyebrow1, R.id.eyebrow2});
                            faces.put(2, new Integer[]{R.id.eye1, R.id.eye2});
                            faces.put(3, new Integer[]{R.id.nose1, R.id.nose2});
                            faces.put(4, new Integer[]{R.id.mouth1, R.id.mouth2});

                            for (int i = 0; i < faces.size(); i++) {
                                ImageView feature;
                                if (features.get(i).compareTo("0") == 0) {
                                    feature = findViewById(faces.get(i)[0]);
                                } else {
                                    feature = findViewById(faces.get(i)[1]);
                                }
                                feature.setVisibility(View.VISIBLE);
                            }
                            TextView countPlayers = findViewById(R.id.count_players_scanned_qr_code);
                            countPlayers.setText(String.format("%s people scanned this QR Code!", qrCode.get("Player Count")));
                        }

                    }
                });
    }

}
