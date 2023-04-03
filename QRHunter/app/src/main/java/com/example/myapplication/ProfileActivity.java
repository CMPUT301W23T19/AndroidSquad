package com.example.myapplication;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

/***
 * This Activity displays the user profile information
 * Retrieves the data from database and displays whe  user views other user profile
 * https://www.tabnine.com/code/java/methods/android.graphics.Canvas/drawBitmap
 * @author: Anika, Aamna Noor
 *
 */

public class ProfileActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView username3;
    private TextView username2;
    private TextView email;
    private TextView user_score;
    private ImageButton backButton;
    private TextView highest_score;
    private TextView lowest_score;
    private TextView scannedCode;
    private ImageView profilePic;

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
        username2 =findViewById(R.id.username2);
        username3 = findViewById(R.id.username3);
        email = findViewById(R.id.email);
        user_score = findViewById(R.id.user_score);
        profilePic = findViewById(R.id.userAvatar);
        backButton = findViewById(R.id.backButton);
        highest_score = findViewById(R.id.highest_score);
        lowest_score = findViewById(R.id.lowest_score);
        scannedCode = findViewById(R.id.scanned_code_num);

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
                        Integer myAttribute1 = Math.toIntExact(documentSnapshot.getLong("highestScore"));
                        Integer myAttribute2 = Math.toIntExact(documentSnapshot.getLong("lowestScore"));
                        ArrayList<String> items = (ArrayList<String>) documentSnapshot.get("QRcode");
                        int itemNum = items.size();
                        username2.setText(realName);
                        username3.setText("@" + username);
                        user_score.setText("Score: " + myAttribute.toString());
                        email.setText("E-mail: " + getEmail);
                        highest_score.setText("Highest score: " +myAttribute1);
                        lowest_score.setText("Lowest score: " + myAttribute2);
                        scannedCode.setText("Total QR codes scanned: " + itemNum);
                        Bitmap bitmap = getPicture(realName);
                        profilePic.setImageBitmap(bitmap);
                    } else {
                        Log.d("ProfileActivity: ", "The device ID doesn't exist");
                    }
                })
                .addOnFailureListener(msg -> {
                    String error = "Error retrieving the data from database: " + msg.getMessage();
                    Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                });
    }

    /***
     * This function is used to draw the first letter of user name and display it as profile picture
     * Created a canvas to draw the first letter of user's name on Bitmap
     * @param realName
     * @return bitmap
     * author: Anika, Aamna Noor
     */

    public Bitmap getPicture(String realName) {
        int size = 250;
        // creating a new bitmap
        Bitmap bitmap = Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888);
        // canvas to draw on Bitmap
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FFFFFF"));
        paint.setStyle(Paint.Style.FILL);
        // drawing the first letter of user's name on the Bitmap
        paint.setTextSize(size / 2);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(realName.substring(0, 1).toUpperCase(), size / 2, size / 2 + size / 8, paint);
        return bitmap;
    }
}

