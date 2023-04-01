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
 * Resource: https://www.youtube.com/watch?v=lBgX58-Sdf0
 * https://www.geeksforgeeks.org/how-to-implement-android-searchview-with-example/
 * https://www.youtube.com/watch?v=M3UDh9mwBd8
 * https://www.youtube.com/watch?v=j9Kp0shGUT8
 */


public class SearchActivity extends AppCompatActivity {
    private ListView searchList;
    private SearchAdapter adapter;

    private ImageButton back;
    private SearchView searchView;

    FirebaseFirestore db;
    List<HashMap<String, String>> originPlayers = new ArrayList<>();
    List<HashMap<String, String>> filterPlayers = new ArrayList<>();

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
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String username = adapter.getItem(position).get("Username");
                Intent intent = new Intent(SearchActivity.this, OtherUserProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}



