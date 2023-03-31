package com.example.myapplication;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.nio.charset.StandardCharsets;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView username1;

    private TextView username3;
    private TextView username2;
    private TextView email;
    private TextView user_score;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        db = FirebaseFirestore.getInstance();
        username1 = findViewById(R.id.username);
        username2 =findViewById(R.id.username2);
        username3 = findViewById(R.id.username3);
        email = findViewById(R.id.email);
        user_score = findViewById(R.id.user_score);
        backButton = findViewById(R.id.backButton);

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        db.collection("Player")
                .whereEqualTo("MachineCode", deviceId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // User with matching device ID found
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String username = documentSnapshot.getId();
                        String getEmail = documentSnapshot.getString("Email");
                        String realName = documentSnapshot.getString("Name");
                        Integer myAttribute = Math.toIntExact(documentSnapshot.getLong("Score"));
                        username1.setText(realName);
                        username2.setText(realName);
                        username3.setText("@" + username);
                        user_score.setText("Score: " + myAttribute.toString());
                        email.setText("Email: " + getEmail);
                    } else {
                        Log.d("ProfileActivity: ", "The device ID doesn't exist");
                    }
                })
                .addOnFailureListener(msg -> {
                    String error = "Error retrieving the data from database: " + msg.getMessage();
                    Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                });
    }
}

