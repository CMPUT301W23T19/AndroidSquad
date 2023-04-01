package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;


/***
 * This Activity displays other user profile
 * author: Anika, Aamna Noor
 */
public class OtherUserProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView username2;
    private TextView username3;
    private TextView highestScore;
    private TextView user_score;
    private ListView listView;
    private Button backButton;
    private ArrayAdapter<String> scennedCodeAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        // Get the username from the previous intent
        String username = getIntent().getStringExtra("username");

        // back button to go back to home page
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        username2 = findViewById(R.id.username2);
        username3 = findViewById(R.id.username3);
        highestScore = findViewById(R.id.highest_score);
        user_score = findViewById(R.id.user_score);
        listView = findViewById(R.id.qrCodeListView);
        scennedCodeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(scennedCodeAdapter);

        // show the progress dialog when it takes time to retrieve the data from firebase
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Query to display the user data
        db.collection("Player")
                .whereEqualTo("Username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressDialog.dismiss();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // if the user profile exist display it
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String name = documentSnapshot.getString("Name");
                        // String getEmail = documentSnapshot.getString("highestScore");
                        String realName = documentSnapshot.getString("Name");
                        Integer myAttribute = Math.toIntExact(documentSnapshot.getLong("Score"));
                        Integer myAttribute1 = Math.toIntExact(documentSnapshot.getLong("highestScore"));
                        List<String> scanList = (List<String>) documentSnapshot.get("QRcode");
                        StringBuilder qrBuilder = new StringBuilder();
                        for (String qrCode : scanList) {
                            qrBuilder.append(qrCode).append(", ");
                            scennedCodeAdapter.add(qrCode);
                        }
                        String codesString = qrBuilder.toString();
                        username2.setText(realName);
                        username3.setText("@"+ username);
                        user_score.setText("Total Score: " + myAttribute.toString());
                        highestScore.setText("Highest score: " + myAttribute1);


                    } else {
                        // username doesn't exist display error msg
                        Toast.makeText(OtherUserProfileActivity.this, "This username doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    // if we can't retrieve the data from firebase display
                    Toast.makeText(OtherUserProfileActivity.this, "Can not retrieve data", Toast.LENGTH_SHORT).show();
                });
    }
}




