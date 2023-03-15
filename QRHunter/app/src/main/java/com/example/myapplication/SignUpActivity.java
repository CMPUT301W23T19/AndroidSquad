package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    public FirebaseFirestore db;
    ImageView avatar;
    private String id;

    ActivityResultLauncher<Intent> galleryResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result != null && result.getResultCode() == RESULT_OK){
                if (result.getData()!=null) {
                    Log.d("SignUpActivity: ", "Image fetched");
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),result.getData().getData() );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    avatar.setImageBitmap(bitmap);
                } else {
                    Log.d("SignupActivity: ", "Image not fetched");
                }
            } else {
                Log.d("SignupActivity: result is ", result.toString());
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup);
        try {
            id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        EditText firstName = findViewById(R.id.FirstName);
        EditText lastName = findViewById(R.id.LastName);
        EditText username = findViewById(R.id.username);
        EditText email = findViewById(R.id.email);
        avatar = findViewById(R.id.avatar);

        db = FirebaseFirestore.getInstance();
        Query query =  db.collection("Player").whereEqualTo("MachineCode", id);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(queryDocumentSnapshots.isEmpty()) { //This player has not registered yet proceed to activity for signing up


                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent gallery = new Intent(Intent.ACTION_PICK);
                        gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryResult.launch(gallery);
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
                                    newPlayer.put("Avatar", ((BitmapDrawable)avatar.getDrawable()).getBitmap().toString());
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
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable("CurrentUser", player);
                                                    intent.putExtras(bundle);
                                                    setResult(RESULT_OK, intent);
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
                Bundle bundle = new Bundle();
                bundle.putSerializable("CurrentUser", player);
                intent.putExtras(bundle);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

}
