package com.example.myapplication;

/**
 * Resource(s):
 * Returning to ParentActivity (HomePage / MainActivity):
 * --From: www.stackoverflow.com
 * --URL: https://stackoverflow.com/q/12276027
 * --Author: https://stackoverflow.com/users/3118/lorenzck
 * --License: CC BY-SA
 *
 * Invoking a camera Activity using registerForActivityResult and using a Uri
 * --From: www.youtube.com
 * --URL: https://youtu.be/T8T1HAUdz1Y
 * --Author: https://www.youtube.com/@discospiff
 *
 * Getting a bitmap from a Uri
 * --From: www.stackoverflow.com
 * --URL: https://stackoverflow.com/q/3879992
 * --Author: https://stackoverflow.com/a/4717740
 * --License: CC BY-SA
 *
 * Using and compressing a bitmap
 * --From: www.youtube.com
 * --URL: https://youtu.be/uMfaRApmabA
 * --Author: https://www.youtube.com/@ProgrammerWorld
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.contract.ActivityResultContracts.TakePicture;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/** Activity class that displays information about newly scanned QR code */
public class ScannedQRCodeActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView QRname;
    private TextView QRscore;
    private TextView playerCount;
    private Button confirm;
    private String username;
    private DocumentReference qrDocRef;
    private String filename;
    private Uri uri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanned_qr_code);
        Intent intent = getIntent();
        String qrName = intent.getStringExtra("qrName");
        Location location = intent.getParcelableExtra("location");
        username = intent.getStringExtra("username");
        ArrayList<Integer> face = (ArrayList<Integer>) intent.getSerializableExtra("Avatar");
        db = FirebaseFirestore.getInstance();
        qrDocRef = db.collection("QR Code").document(qrName);

        QRname = findViewById(R.id.name);
        QRscore = findViewById(R.id.qrScore);
        playerCount = findViewById(R.id.player_count);
        confirm = findViewById(R.id.confirm_button);

        // get QR code visual representation to appear
//        for (int i = 0; i < 5; i++) {
//            ImageView feature = findViewById(face.get(i));
//            feature.setVisibility(View.VISIBLE);
//        }
        qrDocRef.get().addOnCompleteListener(task -> {
            ArrayList<String> features = (ArrayList<String>) task.getResult().get("Avatar");
            HashMap<Integer, Integer[]> faces = new HashMap<>();
            faces.put(0, new Integer[]{R.id.face1, R.id.face2});
            faces.put(1, new Integer[]{R.id.eyebrow1, R.id.eyebrow2});
            faces.put(2, new Integer[]{R.id.eye1, R.id.eye2});
            faces.put(3, new Integer[]{R.id.nose1, R.id.nose2});
            faces.put(4, new Integer[]{R.id.mouth1, R.id.mouth2});

            for (int i = 0; i < faces.size(); i++) {
                ImageView feature;
                if (features.get(i).compareTo("0") == 0) {
                    feature = findViewById(faces.get(i)[0]);
                } else {
                    feature = findViewById(faces.get(i)[1]);
                }
                feature.setVisibility(View.VISIBLE);
            }
        });


        qrDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                int count = 0;
                QRname.setText(String.valueOf(task.getResult().get("Name")));
                QRscore.setText(String.valueOf(task.getResult().get("Score")));

                Log.d("TAG", "Successfully accessed usernames (to be counted) in QR code Database!");
                ArrayList<String> usernames = (ArrayList<String>) task.getResult().get("Username");
                for (int i = 0; i < usernames.size(); i++) {
                    count += 1;
                }
                qrDocRef.update("Player Count", count);
                playerCount.setText(String.valueOf(count-1) + " other player(s) scanned this QR code!");
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScannedQRCodeActivity.this);
                builder.setTitle("Do you want to store the location of this QR code?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               Log.e("Location", String.valueOf(location));
                               GeoPoint qrLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                               qrDocRef.update("Location", qrLocation);
                               getPhotoDialog().show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                qrDocRef.update("Location", null);
                                getPhotoDialog().show();
                            }
                        })
                        .show();
            }
        });
    }

    ActivityResultLauncher<Uri> startForResult = registerForActivityResult(new TakePicture(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            byte[] image = stream.toByteArray();
            StorageReference storage = FirebaseStorage.getInstance().getReference().child(String.format("images/%s/%s", username, filename));
            storage.putBytes(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("PHOTO", "Stored photo in Firebase Storage!");
                }
            });
            setResult(RESULT_OK);
            finish();
        }
    });

    /**
     * Creates an AlertDialog prompting the user to accept or reject permission when storing an image of the QR Code
     * @return AlertDialog to be displayed
     */
    public AlertDialog getPhotoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScannedQRCodeActivity.this);
        return builder.setTitle("Do you want to store a photo of the QR Code?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            invokeCamera();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                  }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .create();
    }

    /**
     * Opens up the app camera that will temporarily store the captured image in the uri
     * @throws IOException
     */
    private void invokeCamera() throws IOException {
        File file = createImgFile();
        uri = FileProvider.getUriForFile(this, "com.example.myapplication.fileprovider", file);
        startForResult.launch(uri);
    }

    /**
     * Creates a file that will store a photo
     * @return File object of the photo
     * @throws IOException
     */
    private File createImgFile() throws IOException {
        String qrName = (String) QRname.getText();      // use QR code name as file name
        File imgDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        filename = qrName + ".jpg";
        return File.createTempFile(qrName, ".jpg", imgDir);
    }
}

