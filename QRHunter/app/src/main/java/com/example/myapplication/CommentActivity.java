package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * Activity class that allows a user to add comments to a QR Code
 * @authors Jessie, Angela
 */
public class CommentActivity extends AppCompatActivity {

    private EditText editText;
    private EditText userText;
    private Button submit_button;
    private ImageButton back;
    private String qrName;
    private String username;
    FirebaseFirestore db;

    /**
     * Gets views associated with activity_comment layout and allows user to comment
     * Comment is stored in firebase.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        db = FirebaseFirestore.getInstance();
        editText = findViewById(R.id.comment_edit);
        submit_button = findViewById(R.id.submit);
        back = findViewById(R.id.back_bb);
        Intent intent = getIntent();
        qrName = intent.getStringExtra("qrName");
        username = intent.getStringExtra("username");

        submit_button.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles event when SUBMIT button is clicked
             * Stores comment in firebase
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                String comment = editText.getText().toString();

                HashMap<String, Object> commentMap = new HashMap<>();
                commentMap.put("username", username);
                commentMap.put("comment", comment);

                db.collection("QR Code").document(qrName)
                        .update("Comment", FieldValue.arrayUnion(commentMap))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(CommentActivity.this, "Data successfully added", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(CommentActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                finish();
            }

        });

        back.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the event when BACK button is clicked.
             * Returns to Home page (Main Activity)
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(-1, intent);      // result code of -1 means user did not delete QR code
                finish();
            }
        });
    }
}
