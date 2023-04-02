package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;


/***
 * This Activity displays other user profile
 * Retrieves data from firebase and displays
 * It displays name, username, highest score, total score, profile picture and list of scanned QR code
 * @author: Anika, Aamna Noor
 */
public class OtherUserProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView username2;
    private TextView username3;
    private TextView highestScore;
    private TextView user_score;
    private ListView listView;
    private Button backButton;
    private ImageView profilePic;
    private ArrayAdapter<String> scannedCodeAdapter;
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
        profilePic = findViewById(R.id.profile_pic);
        scannedCodeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(scannedCodeAdapter);

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
                        String realName = documentSnapshot.getString("Name");
                        Integer myAttribute = Math.toIntExact(documentSnapshot.getLong("Score"));
                        Integer myAttribute1 = Math.toIntExact(documentSnapshot.getLong("highestScore"));
                        List<String> scanList = (List<String>) documentSnapshot.get("QRcode");
                        StringBuilder qrBuilder = new StringBuilder();
                        for (String qrCode : scanList) {
                            qrBuilder.append(qrCode).append(", ");
                            scannedCodeAdapter.add(qrCode);
                        }
                        String codesString = qrBuilder.toString();
                        username2.setText(realName);
                        username3.setText("@"+ username);
                        user_score.setText("Total Score: " + myAttribute.toString());
                        highestScore.setText("Highest score: " + myAttribute1);
                        Bitmap bitmap = getPicture(realName);
                        profilePic.setImageBitmap(bitmap);

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

    /***
     * This function is used to draw the first letter of user name and display it as profile picture
     * Created a canvas to draw the first letter of user's name on Bitmap
     * @param realName
     * @return bitmap
     * @author: Anika, Aamna Noor
     */

    public Bitmap getPicture(String realName) {
        int size = 250; // Change this to adjust the size of the bitmap
        // creating a new bitmap
        Bitmap bitmap = Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888);
        // canvas to draw on Bitmap
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#800080"));
        paint.setStyle(Paint.Style.FILL);
        // drawing the first letter of user's name on the Bitmap
        paint.setTextSize(size / 2);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(realName.substring(0, 1).toUpperCase(), size / 2, size / 2 + size / 8, paint);
        return bitmap;
    }
}




