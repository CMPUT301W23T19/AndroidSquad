package com.example.myapplication;

import android.annotation.SuppressLint;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeoSearchActivity extends AppCompatActivity {
    private ListView searchList;
    private GeoSearchAdapter adapter;
    private ImageButton back;
    private SearchView searchView;
    private String qrpassName;
    private String locationString;

    FirebaseFirestore db;

    List<HashMap<String, String>> origingeo = new ArrayList<>();
    List<HashMap<String, String>> filtergeo = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geosearch_main);

        searchList = findViewById(R.id.geo_list);
        back = findViewById(R.id.back_bb);
        adapter = new GeoSearchAdapter(this, new ArrayList<>()); // Initialize adapter
        searchList.setAdapter(adapter);
//        adapter = new SearchAdapter(this, new ArrayList<>()); // Initialize adapter
//        searchList.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        CollectionReference searchRef = db.collection("QR Code");
        searchRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                // Display the player info
                origingeo = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    HashMap<String, String> playerData = new HashMap<>();
                    qrpassName = document.getString("Name");
                    playerData.put("Name", document.getString("Name"));
                    GeoPoint location = document.getGeoPoint("Location");
                    if (location != null) {
                        locationString = String.format("%.6f,%.6f", location.getLatitude(), location.getLongitude());
                        playerData.put("Location", locationString);
                        origingeo.add(playerData);
                    }
                }
                filtergeo.addAll(origingeo);
                // get the all user info saved in database
                for (int i = 0; i < origingeo.size(); i++) {
                    switch (i) {
                        default:
                            String name = (String) origingeo.get(i).get("Name");
                            adapter.add(origingeo.get(i));
                    }
                }
                adapter.notifyDataSetChanged();
                searchList.setAdapter(adapter);
            }
        });
        // Get the reference to the search view
        searchView = findViewById(R.id.search_bb);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit (String query){
                // Called when the user submits the search query
                return false;
            }

            @Override
            public boolean onQueryTextChange (String newText){
                // Called when the user changes the text in the search view
                filtergeo.clear(); // Clear the filtered data
                for (int i = 0; i < origingeo.size(); i++) {
                    String name = origingeo.get(i).get("Name");
                    if (name != null){
                        if (name.toLowerCase().contains(newText.toLowerCase())) {
                            filtergeo.add(origingeo.get(i));
                        }
                    }
                }
                adapter.clear();
                adapter.addAll(filtergeo);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        // Get the reference to the search view
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

//                String[] latLongStrings = locationString.split(",");
//                double latitude = Double.parseDouble(latLongStrings[0].trim());
//                double longitude = Double.parseDouble(latLongStrings[1].trim());
                String qrpass = String.valueOf(adapter.getItem(position).get("Name"));
                Log.e("HistoryActivity","Position of item to be deleted: " + qrpass);
                Intent intent = new Intent(GeoSearchActivity.this, MapActivity.class);

                intent.putExtra("qrpassname", qrpass); // Replace with the actual latitude of the QR code
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}
