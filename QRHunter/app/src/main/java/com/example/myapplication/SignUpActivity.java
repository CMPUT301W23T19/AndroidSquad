package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    public FirebaseFirestore db;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        try {
            id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        db = FirebaseFirestore.getInstance();
        Query query =  db.collection("Player").whereEqualTo("MachineCode", id);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(queryDocumentSnapshots.isEmpty()) { //This player has not registered yet proceed to activity for signing up
                EditText firstName = findViewById(R.id.FirstName);
                EditText lastName = findViewById(R.id.LastName);
                ImageView avatar = findViewById(R.id.avatar);
                EditText username = findViewById(R.id.username);
                EditText email = findViewById(R.id.email);

                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                    }
                });

                Button confirm = (Button) findViewById(R.id.register_confirm);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newUsername = username.getText().toString();
                        if(!newUsername.equals("")){ // if user supply something as username, we check the uniqueness
                            Query checkUsername = db.collection("Player").whereEqualTo("Username", newUsername);
                            checkUsername.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                                if(queryDocumentSnapshots1.isEmpty()){ //username valid, register this user to Firestore and pass user to homepage.

                                    Map<String, Object> newPlayer = new HashMap<>();
                                    newPlayer.put("Username", newUsername);
                                    newPlayer.put("MachineCode", id);
                                    newPlayer.put("QRcode", new ArrayList<String>());
                                    newPlayer.put("Score", 0);
                                    newPlayer.put("highestScore", 0);
                                    newPlayer.put("lowestScore", 0);
                                    newPlayer.put("Name", firstName.getText().toString() + " "+ lastName.getText().toString());
                                    newPlayer.put("Email", email.getText().toString());

                                    db.collection("Player").document(newUsername)
                                                                        .set(newPlayer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(SignUpActivity.this, "SignUp Successful!", Toast.LENGTH_SHORT);
                                                    Intent intent = new Intent();
                                                    Player player = new Player(0, newUsername, 0, 0); //Init a new Player class locally
                                                    intent.putExtra("CurrentUser", player);
                                                    finish(); // Send user back to homepage
                                                }
                                            });
                                } else { //Username is not unique, ask user to input again.
                                    Toast.makeText(SignUpActivity.this, "Username is not unique", Toast.LENGTH_SHORT).show();
                                    username.setText(""); //empty original input
                                }
                            });

                        } else { //if user supply invalid username, ask user to enter again
                            Toast.makeText(SignUpActivity.this, "Please input a valid username", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else { // Player found! Send player back to homepage, logged in.
                Intent intent = new Intent();
                DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                Player player = new Player(document.getLong("Score"), document.getString("Username"), document.getLong("highestScore"), document.getLong("lowestScore"));
                intent.putExtra("CurrentUser", player);
                finish();
            }
        });

    }
}
