/**
 The SignUpActivity class is responsible for handling user registration.
 It collects user information, such as first and last name, unique username, email and avatar, and adds them to the Firestore database.
 This class also ensures that the entered username is unique, and provides feedback to the user if the username is already in use.
 If the user successfully registers, the class sends the user back to the homepage.
 */
package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity that allows user to create an account, if they are not an existing Player.
 * Retrieves and stores information in firebase.
 * @author: Shirley
 */
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
                                    newPlayer.put("Score", ((long)0));
                                    newPlayer.put("highestScore", ((long)0));
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    ((BitmapDrawable)avatar.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                                    newPlayer.put("Avatar", Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
                                    newPlayer.put("lowestScore", ((long)0));
                                    newPlayer.put("Name", firstName.getText().toString() + " "+ lastName.getText().toString());
                                    newPlayer.put("Email", email.getText().toString());

                                    Log.e("SignUp Activity, bitmap is :", Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));

                                    db.collection("Player").document(newUsername)
                                                                        .set(newPlayer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.e("Signup Activity: ", "Signup Successful shutting down page!");
                                                    Toast.makeText(SignUpActivity.this, "SignUp Successful!", Toast.LENGTH_SHORT);
                                                    Intent intent = new Intent();
                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                                    ((BitmapDrawable)avatar.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                                    Player player = new Player(firstName.getText().toString() + " "+ lastName.getText().toString(),
                                                                                0,
                                                                                newUsername,
                                                                                0,
                                                                                0,
                                                                                new ArrayList<String>(),
                                                                                id,
                                                                          null); //Init a new Player class locally
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
                Player player = new Player(document.getString("Name"), document.getLong("Score").intValue(), document.getString("Username"), document.getLong("highestScore").intValue(), document.getLong("lowestScore").intValue(), (ArrayList<String>) document.get("QRcode"), document.getString("MachineCode"), document.getString("Avatar"));
                Bundle bundle = new Bundle();
                bundle.putSerializable("CurrentUser", player);
                intent.putExtras(bundle);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

}
