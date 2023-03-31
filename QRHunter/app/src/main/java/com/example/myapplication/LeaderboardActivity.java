package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    ListView playerRanks;
    RankAdapter rankAdapter;
    Player currentPlayer;
    Button back;

    List<HashMap<String, Object>> players = new ArrayList<>();
    FirebaseFirestore db;
    String userName;// Mock player set as default player for now

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard);
        //BottomNavigationView bottomNavigationView  = findViewById(R.id.nav_bar);
        playerRanks = findViewById(R.id.all_players_rank);
        rankAdapter = new RankAdapter(this, players);
        playerRanks.setAdapter(rankAdapter);
        back = findViewById(R.id.back_from_leaderboard);

        Bundle bundle = getIntent().getExtras();
        currentPlayer = (Player)bundle.getSerializable("currentUser");
        userName  = currentPlayer.getUsername();
        // Get the player rank for the highest score QR scanned
        db = FirebaseFirestore.getInstance();
        CollectionReference playerRef = db.collection("Player");
        Query query = playerRef.orderBy("highestScore", Query.Direction.DESCENDING);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            players = new ArrayList<>();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                HashMap<String, Object> playerData = new HashMap<>();
                playerData.put("Username", document.getString("Username"));
                playerData.put("highestScore", document.getLong("highestScore"));
                playerData.put("Avatar", document.get("Avatar"));
                players.add(playerData);
            }

            // Find the rank of the player with the given name
            int playerRank = -1;
            for (int i = 0; i < players.size(); i++) {
                String name = (String) players.get(i).get("Username");
                if (name.equals(userName)) {
                    playerRank = i ;
                    break;
                }
            }

            // Set the text of the playerRank TextView to display the player's rank
            String playerRankText = "Your rank(highest scoring): " + playerRank;
            TextView playerRankTextView = findViewById(R.id.player_rank);
            playerRankTextView.setText(playerRankText);
        });

        // Get the scores collection from Firestore
        CollectionReference scoresCollection = db.collection("Player");
        scoresCollection.orderBy("Score",Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                // Display the top3 player in the rank
                players = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    HashMap<String, Object> playerData = new HashMap<>();
                    playerData.put("Username", document.getString("Username"));
                    playerData.put("Score", document.getLong("Score"));
                    playerData.put("Avatar", document.get("Avatar"));
                    players.add(playerData);

                }
                // Top 3 player in game-wide scores competition
                for (int i = 0; i < players.size(); i++) {
                    switch (i) {
                        case 0:
                            ((TextView)findViewById(R.id.rank1_name)).setText(players.get(i).get("Username").toString()); // set 1st username
                            ((TextView)findViewById(R.id.rank1_score)).setText("Score:\n"+players.get(i).get("Score").toString());
                            ((ImageView)findViewById(R.id.rank1)).setImageBitmap(StringToBitMap((String) players.get(i).get("Avatar")));
                            break;
                        case 1:
                            ((TextView)findViewById(R.id.rank2_name)).setText(players.get(i).get("Username").toString()); // set 2nd username
                            ((TextView)findViewById(R.id.rank2_score)).setText("Score:\n"+players.get(i).get("Score").toString());
                            ((ImageView)findViewById(R.id.rank2)).setImageBitmap(StringToBitMap((String) players.get(i).get("Avatar")));
                            break;

                        case 2:
                            ((TextView)findViewById(R.id.rank3_name)).setText(players.get(i).get("Username").toString()); // set 1st username
                            ((TextView)findViewById(R.id.rank3_score)).setText("Score:\n"+players.get(i).get("Score").toString());
                            ((ImageView)findViewById(R.id.rank3)).setImageBitmap(StringToBitMap((String) players.get(i).get("Avatar")));
                            break;

                        default:
                            String name = (String) players.get(i).get("Username");
                            players.get(i).put("Rank", i); // add Rank attribute into player's hashMap

                            rankAdapter.add(players.get(i));
                    }

                }

                rankAdapter.notifyDataSetChanged();
                playerRanks.setAdapter(rankAdapter);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}

