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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    private ArrayList<Integer> features;


    /**
     * Constructor function for QRCodeController
     */
    public QRCodeControllerDB(String codeContents, String username, FirebaseFirestore db) {
        this.codeContents = codeContents;
        user = username;
        this.db = db;

        qrCode = new QRCode(codeContents, null);
        name = qrCode.getName();
        score = qrCode.getScore();
        sha256hex = qrCode.getHash();
        features = qrCode.getAvatarList();
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
                            } else {
                                checkIfScanned(context);
                            }
                            addToHistoryofQRCodes();
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
                               Log.d("TAG", "User has not scanned it before");
                               addToHistoryofUsers(context);
                           } else {
                               Log.d("TAG", "User has scanned it previously");
                               AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                        Log.d("TAG", "Successfully added user!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error adding user");
                    }
                });
       start(context);
    }

    /**
     * Start ScannedQRCodeActivity
     */
    private void start(Context context) {
        Intent intent = new Intent(context, ScannedQRCodeActivity.class);
        intent.putExtra("contents", codeContents);
        context.startActivity(intent);
    }

    /**
     * Adds QR code to user's history of scanned QR codes
     */
    public void addToHistoryofQRCodes() {
        db.collection("Player").document(user)
                .update("QRcode", FieldValue.arrayUnion(sha256hex))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "Successfully added QR code to history!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Could not add QR Code");
                    }
                });
    }

    /**
     * Adds QR code to Firestore Firebase database
     */
    public void addQRCodetoDatabase(Context context) {
        Map<String, String> comments = new HashMap<>();
        ArrayList<String> usernames = new ArrayList<>();

        // Testing adding a comment
        comments.put(user, "Wow!");
        usernames.add(user);

        Map<String, Object> qrCode = new HashMap<>();
        qrCode.put("Comment", comments);
        qrCode.put("Hash", sha256hex);
        qrCode.put("Location", null);
        qrCode.put("Name", name);
        qrCode.put("Photo", "code.png");
        qrCode.put("Score", score);
        qrCode.put("Username", usernames);
        qrCode.put("Avatar", features);

        db.collection("QR Code").document(name)
            .set(qrCode)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void avoid) {
                Log.d("TAG", "Added QR code successfully!");
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

}
