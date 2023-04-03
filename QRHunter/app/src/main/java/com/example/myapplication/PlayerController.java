package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Controller class that handles events related to Player data
 * Updates Player collection in firebase
 * @authors: Jessie, Shirley, Angela
 */
public class PlayerController {
    private Integer score = 93;
    private static String username;
    private Integer highestScore;

    private Integer lowestScore;
    Player player;
    FirebaseFirestore db;

    /**
     * Constructor function for PlayerController
     * @param scores - Integer representation of player's score
     * @param highest - Integer representation of player's highest score
     * @param lowest - Integer representation of player's lowest score
     * @param username - String representation of player's username
     * @param db - FirebaseFirestore instance; connection to database
     */
    public PlayerController(Integer scores, Integer highest, Integer lowest, String username, FirebaseFirestore db) {
        this.username = username;
        this.db = db;

        if (scores != null) {
            this.score = scores;
            this.highestScore = highest;
            this.lowestScore = lowest;
            player = new Player(null, score, username, highestScore, lowestScore, null, null, null);
        }

    }

    /**
     * Deletes QR code from user's profile/ history of previously scanned QR codes
     * @param qrName - String representation of the name of the QR code to be deleted
     */
    public void deleteQRFromHistory(String qrName) {
        DocumentReference docRef = db.collection("Player").document(username);

        docRef.update("QRcode", FieldValue.arrayRemove(qrName))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("PlayerController", "Successfully deleted QR Code!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("PlayerController", "Failed to delete QR Code");
                    }
                });
    }

    /**
     * Adds QR code to user's history of scanned QR codes
     * @param qr_name String representation of the name of the QR code to be added
     */
    public void addToHistoryofQRCodes(String qr_name) {
        db.collection("Player").document(username)
                .update("QRcode", FieldValue.arrayUnion(qr_name))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("PlayerController", "Successfully added QR code to history!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("PlayerController", "Could not add QR Code");
                    }
                });
    }



    /**
     * Increases or decreases user's score based on delete or add event
     * @param addOrdelete - Integer representation indicating what event occured
     * @param qrName - String representation of the qrCode that was added or deleted
     */
    public void updateScore(int addOrdelete, String qrName) {
        DocumentReference docRef = db.collection("Player").document(username);
        db.collection("QR Code").document(qrName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        long score = (long) task.getResult().get("Score");
                        if (addOrdelete == -1) {
                            score *= -1;
                        }
                        docRef.update("Score", FieldValue.increment(score));
                    }
                });
    }


    /**
     * Updates the player's highest score or lowest score when QR code is deleted, if needed
     */
    public void deleteUpdateHighLowScore() {
        CollectionReference qrColRef = db.collection("QR Code");
        DocumentReference playerDocRef = db.collection("Player").document(username);
        playerDocRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> playerTask) {
                        ArrayList<String> qrCodes = (ArrayList<String>) playerTask.getResult().get("QRcode");
                        if (qrCodes.isEmpty()) {
                            Log.e("PlayerController", "There are no QR codes");
                            playerDocRef.update("highestScore", 0);
                            playerDocRef.update("lowestScore", 0);
                        } else {
                            Log.e("PlayerController", "qrCodes:" + String.valueOf(qrCodes));
                            for (int i = 0; i < qrCodes.size(); i++) {
                                qrColRef.document(qrCodes.get(i))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> qrTask) {
                                                if ((long) qrTask.getResult().get("Score") > (long) playerTask.getResult().get("highestScore")) {
                                                    playerDocRef.update("highestScore", qrTask.getResult().get("Score"));
                                                    Log.e("PlayerController", "Successfully updated highest score");
                                                }
                                                if ((long) qrTask.getResult().get("Score") < (long) playerTask.getResult().get("lowestScore")) {
                                                    playerDocRef.update("lowestScore", qrTask.getResult().get("Score"));
                                                    Log.e("PlayerController", "Successfully updated lowest score");
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    /**
     * Updates player's highest or lowest score when they scan a new QR code, if needed
     * @param qrName - String representation of the name of the recently added QR Code
     */
    public void addUpdateHighLow(String qrName) {
        DocumentReference playerDocRef = db.collection("Player").document(username);
        playerDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> ptask) {
                db.collection("QR Code").document(qrName)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> qtask) {
                                long qr_score = qtask.getResult().getLong("Score");
                                if (((ArrayList<String>) ptask.getResult().get("QRcode")).size() == 1) {
                                    playerDocRef.update("highestScore", qr_score);
                                    playerDocRef.update("lowestScore", qr_score);
                                }
                                else if ((long) ptask.getResult().get("highestScore") < qr_score) {
                                    playerDocRef.update("highestScore", qr_score);
                                }
                                else if ((long) ptask.getResult().get("lowestScore") > qr_score) {
                                    playerDocRef.update("lowestScore", qr_score);
                                }
                            }
                        });
            }
        });

    }
}



