/**
 * This class handles player search and display using a search feature that filters data based on user input.
 * It shows a list of players that match the search query, and lets the user view profiles by clicking on a player.
 * Firebase's Realtime Database and Firestore are used for retrieving and querying player data.
 * Resource: https://www.youtube.com/watch?v=lBgX58-Sdf0
 * https://www.geeksforgeeks.org/how-to-implement-android-searchview-with-example/
 * https://www.youtube.com/watch?v=M3UDh9mwBd8
 * https://www.youtube.com/watch?v=j9Kp0shGUT8
 */
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Activity class that allows user to search for other players.
 * This class directs current user to another user's profile when they select a player from the search results.
 * Retrieves information from firebase.
 * @authors: Jessie, Shirley, Aamna
 */
public class SearchActivity extends AppCompatActivity {
    private ListView searchList;
    private SearchAdapter adapter;

    private ImageButton back;
    private SearchView searchView;

    FirebaseFirestore db;
    List<HashMap<String, String>> originPlayers = new ArrayList<>();
    List<HashMap<String, String>> filterPlayers = new ArrayList<>();

    /**
     * Handels the event when user searches for otherplayers by username
     * It creates the listview of the usernames and names of otherplayers
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        searchList = findViewById(R.id.result_list);
        back = findViewById(R.id.back_button_button);
        adapter = new SearchAdapter(this, new ArrayList<>()); // Initialize adapter
        searchList.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        CollectionReference searchRef = db.collection("Player");
        searchRef.orderBy("Username", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                // Display the player info
                originPlayers = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    HashMap<String, String> playerData = new HashMap<>();
                    playerData.put("Username", document.getString("Username"));
                    playerData.put("Name", document.getString("Name"));
                    originPlayers.add(playerData);
                }
                filterPlayers.addAll(originPlayers);
                // get the all user info saved in database
                for (int i = 0; i < originPlayers.size(); i++) {
                    switch (i) {
                        default:
                            String name = (String) originPlayers.get(i).get("Username");
                            adapter.add(originPlayers.get(i));
                    }
                }
                adapter.notifyDataSetChanged();
                searchList.setAdapter(adapter);
            }
        });

        //set up back button to go back if user doesnt want to continue searching
        back.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the event when BACK button is clicked.
             * Returns to Home page (Main Activity)
             * @param view The view that was clicked.
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Get the reference to the search view
        searchView = findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit (String query){
                // Called when the user submits the search query
                return false;
            }

            /**
             * Handles the event when user changes the text in search view
             * It clears out the filterlist and add the new filtered list
             * @param newText
             * @return boolean
             */

            @Override
            public boolean onQueryTextChange (String newText){
                // Called when the user changes the text in the search view
                filterPlayers.clear(); // Clear the filtered data
                for (int i = 0; i < originPlayers.size(); i++) {
                    String name = originPlayers.get(i).get("Username");
                    if (name.toLowerCase().contains(newText.toLowerCase())) {
                        filterPlayers.add(originPlayers.get(i));
                    }
                }
                adapter.clear();
                adapter.addAll(filterPlayers);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            /**
             * Handles the event when a user from the search results is clicked.
             * Starts OtherUserProfileActivity
             * @param v The view that was clicked.
             */
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String username = adapter.getItem(position).get("Username");
                Intent intent = new Intent(SearchActivity.this, OtherUserProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}



