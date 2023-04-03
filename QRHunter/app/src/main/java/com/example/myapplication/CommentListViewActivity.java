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

/**
 * Activity class that displays comments for a QR Code
 * QR code was selected by user in the HistoryActivity
 * Comments are retrieved from Firestore Firebase
 * @authors Jessie, Angela
 */
public class CommentListViewActivity extends AppCompatActivity {
    private ListView commentList;
    private CommentAdapter adapter;
    private ImageButton back;
    private String username;
    private String qrName;
    FirebaseFirestore db;
    List<HashMap<String, String>> players = new ArrayList<>();

    /**
     * Gets views associated with history_list layout and populates them with comments
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_comments);

        commentList = findViewById(R.id.comment_list);
        username = getIntent().getStringExtra("username");
        qrName = getIntent().getStringExtra("qrName");
        back = (ImageButton) findViewById(R.id.goback);

        db = FirebaseFirestore.getInstance();

        // get comments that will be displayed by CommentAdapter
        db.collection("QR Code").document(qrName)
                .get().addOnCompleteListener(task -> {
                    ArrayList<HashMap<String,String>> comments = (ArrayList<HashMap<String, String>>) task.getResult().get("Comment");
                    adapter = new CommentAdapter(this, comments); // Initialize adapter
                    commentList.setAdapter(adapter);
                });


        // set up back button to go back if user doesnt want to continue searching
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

    }

}
