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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {

    private EditText editText;
    private EditText userText;
    private Button submit_button;
    private ImageButton back;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        db = FirebaseFirestore.getInstance();
        userText = findViewById(R.id.username_edit);
        editText = findViewById(R.id.comment_edit);
        submit_button = findViewById(R.id.submit);
        back = findViewById(R.id.back_bb);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userText.getText().toString();
                String comment = editText.getText().toString();

                HashMap<String, Object> commentMap = new HashMap<>();
                commentMap.put("username", username);
                commentMap.put("comment", comment);

                db.collection("QR Code").document("Comment").set(commentMap)
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
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(-1, intent);      // result code of -1 means user did not delete QR code
                finish();
            }
        });
    }
}
