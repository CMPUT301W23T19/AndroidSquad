package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * https://www.youtube.com/watch?v=SGiY_AitrN0
 */
public class CommentListViewActivity extends AppCompatActivity {
    private ListView commentList;
    private CommentAdapter adapter;

    private ImageButton back;
    private SearchView searchView;


    FirebaseFirestore db;
    List<HashMap<String, String>> players = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_comments);

        commentList = findViewById(R.id.comment_list);
        back = (ImageButton) findViewById(R.id.goback);
        adapter = new CommentAdapter(this, new ArrayList<>()); // Initialize adapter
        commentList.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        CollectionReference searchRef = db.collection("QR Code");
        searchRef.orderBy("Comment", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                players = new ArrayList<>();
                //below is the error to fix, it saves the right data but has issue showing each userid and
                //user comment from hashmap type value
//                for (DocumentSnapshot document : queryDocumentSnapshots) {
//                    HashMap<String, String> playerData = new HashMap<>();
//                    Object commentObj = document.get("Comment");
//                    if(commentObj != null) {
//                        String comment = commentObj.toString();
//                        playerData.put("Comment", comment);
//                        players.add(playerData);
//                    }
//                }
//
//                for (int i = 0; i < players.size(); i++) {
//                    switch (i) {
//                        default:
//                            String comment = players.get(i).get("Comment");
//                            adapter.add(players.get(i));
//                    }
//                }

                adapter.notifyDataSetChanged();
                commentList.setAdapter(adapter);
            }
        });



        //set up back button to go back if user doesnt want to continue searching
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}
