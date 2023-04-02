package com.example.myapplication;

/**
 * Idea to use Apache Common Codecs
 * --URL: https://www.baeldung.com/sha-256-hashing-java
 * Apache Common Codecs:
 * --URL: https://mvnrepository.com/artifact/commons-codec/commons-codec/1.15
 * Converting hex string to binary string:
 * -- From: www.stackoverflow.com
 * -- URL:https://stackoverflow.com/q/9246326
 * -- Author(s): https://stackoverflow.com/users/20394/mike-samuel
 * -- License: CC BY-SA
 * Iterating over key-val pairs in a HashMap:
 * -- From: www.stackoverflow.com
 * -- URL: https://stackoverflow.com/q/585654
 * -- Author(s): https://stackoverflow.com/users/40342/joachim-sauer
 * -- License: CC BY-SA
 * Creating and executing a query:
 * -- From www.firebase.google.com
 * -- URL: https://firebase.google.com/docs/firestore/query-data/queries
 * Updating a document and using arrayUnion() to add items to an array field:
 * --URL: https://cloud.google.com/firestore/docs/manage-data/add-data#javaandroid_12
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Parcelable;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for adding, modifying, deleting QR codes from database and starting ScannedQRCodeActivity
 */
public class QRCodeControllerDB {
    private String name;
    private int score;
    public String sha256hex;
    private String codeContents;
    private String user;
    private QRCode qrCode;
    private FirebaseFirestore db;
    private ArrayList<String> features;
    private PlayerController pc;
    private Location location;
    private ActivityResultLauncher<Intent> updateScore;


    /**
     * Constructor function for QRCodeController
     */
    public QRCodeControllerDB(String codeContents, String username, FirebaseFirestore db) {
        this.codeContents = codeContents;
        user = username;
        this.db = db;
        pc = new PlayerController(null, null, null, username, db);

        if (codeContents != null) {
            qrCode = new QRCode(codeContents, null);
            name = qrCode.getName();
            score = qrCode.getScore();
            sha256hex = qrCode.getHash();
            features = qrCode.getAvatarList();
            Log.e("SCORE", String.valueOf(score));
        }
    }

    /**
     * Sets Activity Launcher that will be used to launch ScannedQRCodeActivity
     * @param launcher - ActivityResultLauncher to be used
     */
    public void setLauncher(ActivityResultLauncher<Intent> launcher) {
        updateScore = launcher;
    }

    /**
     * Checks if QR code exists in database and adds it to the firebase if it does not.
     */
    public void validateAndAdd(Context context) {
        db.collection("QR Code")
                .whereEqualTo("Name", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {       // add new qr code
                                addQRCodetoDatabase(context);
                                pc.updateScore(1, name);
                            } else {
                                checkIfScanned(context);
                            }
                            pc.addToHistoryofQRCodes(name);
                            pc.addUpdateHighLow(name);
                        }
                    }
                });
    }

    /**
     * Checks if user has already scanned QR code and adds username to list of users who have scanned QR code if not previously scanned by current user
     */
    public void checkIfScanned(Context context) {
       db.collection("QR Code")
               .whereEqualTo("Name", name)
               .whereArrayContains("Username", user)
               .get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
                           if(task.getResult().isEmpty()) {     // user has not scanned QR code before
                               Log.e("QRCodeControllerDB", "User has not scanned it before");
                               addToHistoryofUsers(context);
                           } else {
                               Log.e("QRCodeControllerDB", "User has scanned it previously");
                               AlertDialog.Builder builder = new AlertDialog.Builder(context);     // Creates window telling user they have already scanned it
                               builder.setTitle("Sorry!");
                               builder.setMessage("You have already scanned this QR code! Keep searching :)");
                               builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {
                                       dialogInterface.dismiss();
                                   }
                               }).show();
                           }
                       } else {
                           Log.e("TAG", "Error getting data");
                       }
                   }
               });
    }

    /**
     * Adds current user to list of users that have scanned QR code
     */
    public void addToHistoryofUsers(Context context) {
        db.collection("QR Code").document(name)
                .update("Username", FieldValue.arrayUnion(user))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // increase user score
                        Log.e("QRCodeControllerDB", "Successfully added user!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("QRCodeControllerDB", "Error adding user");
                    }
                });
       pc.updateScore(1, name);
       start(context);
    }


    /**
     * Start ScannedQRCodeActivity
     */
    private void start(Context context) {
        Intent intent = new Intent(context, ScannedQRCodeActivity.class);
        intent.putExtra("qrName", name);
        intent.putExtra("location", location);
        intent.putExtra("username", user);
        intent.putExtra("Avatar", features);
        updateScore.launch(intent);
    }

    /**
     * Adds QR code to Firestore Firebase database
     */
    public void addQRCodetoDatabase(Context context) {
        ArrayList<HashMap<String, String>> comments = new ArrayList<>();
        ArrayList<String> usernames = new ArrayList<>();
        Map<String, Object> qrCodeContents = new HashMap<>();
        usernames.add(user);

        qrCodeContents.put("Comment", comments);
        qrCodeContents.put("Hash", sha256hex);
        qrCodeContents.put("Location", null);
        qrCodeContents.put("Name", name);
        qrCodeContents.put("Photo", "code.png");
        qrCodeContents.put("Score", score);
        qrCodeContents.put("Username", usernames);
        qrCodeContents.put("Avatar", features);
        qrCodeContents.put("Player Count", 0);


        db.collection("QR Code").document(name)
            .set(qrCodeContents)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void avoid) {
                Log.e("QRCodeControllerDB", "Added QR code successfully!");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error adding QR code");
            }
        });
        start(context);
    }

    public void deleteUser(String qrName) {

        db.collection("QR Code").document(qrName)
                .update("Username", FieldValue.arrayRemove(user))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("QRCodeControllerDB", "Successfully deleted " + user + " from history!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("QRCodeControllerDB", "Failed to delete user");
                    }
                });
    }
    public void setLocation(Location location) {
        this.location = location;
    }
}
