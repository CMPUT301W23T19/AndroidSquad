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

public class PlayerController {

    private Integer score = 93;

    private static String username;

    private Integer highestScore;

    private Integer lowestScore;

    Player player;

    FirebaseFirestore db;

    public PlayerController(Integer scores, Integer highest, Integer lowest, String username, FirebaseFirestore db) {
        this.username = username;
        this.db = db;

        if (scores != null) {
            this.score = scores;
            this.highestScore = highest;
            this.lowestScore = lowest;

            player = new Player(score, username, highestScore, lowestScore);
        }

    }


    public void checkSumScore() {
        db.collection("Player")
                .whereEqualTo("Username", username)
                .whereArrayContains("Score", score)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {     // user has not scanned QR code before
                                addToHistoryofUserscore();
                            } else {
                                Log.d("TAG", "Has the same score with previous one");
                            }
                        } else {
                            Log.e("TAG", "Error getting data");
                        }
                    }
                });
//                .update("Score", FieldValue.arrayUnion(score))

    }

    public void addToHistoryofUserscore() {
        db.collection("Username").document(username)

                .update("Score", FieldValue.arrayUnion(score))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "Successfully added score!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error adding score");
                    }
                });
    }

    public void checkHighestScore() {
        db.collection("Player")
                .whereEqualTo("Username", username)
                .whereArrayContains("highestScore", score)
                .whereGreaterThan("highestScore", score)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {     // user has not scanned QR code before
                                addToHighestscoreofUsers();
                            } else {
                                Log.d("TAG", "New highest score has been updated");
                            }
                        } else {
                            Log.e("TAG", "Error getting data");
                        }
                    }
                });
//                .update("Score", FieldValue.arrayUnion(score))
    }

    public void addToHighestscoreofUsers() {
        db.collection("Username").document(username)
                .update("highestScore", FieldValue.arrayUnion(score))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "Successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Error updating");
                    }
                });
    }

    public void checkLowestScore() {
        db.collection("Player")
                .whereEqualTo("Username", username)
                .whereArrayContains("lowestScore", score)
                .whereGreaterThan("lowestScore", score)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {     // user has not scanned QR code before
                                addToLowestScoreOfUsers();
                            } else {
                                Log.d("TAG", "New lowest score has been updated");
                            }
                        } else {
                            Log.e("TAG", "Error getting data");
                        }
                    }
                });
//                .update("Score", FieldValue.arrayUnion(score))

    }

    public void addToLowestScoreOfUsers() {
        db.collection("Username").document(username)
                .update("lowestScore", FieldValue.arrayUnion(score))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("PlayerController", "Successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("PlayerController", "Error updating");
                    }
                });
    }

    /**
     * Deletes QR code from user's profile/ history of previously scanned QR codes
     *
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
     *
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
     * @param qr_score - Integer representation of the recently added or deleted qr code
     */
    public void updateScore(int qr_score, String qrName) {
        DocumentReference docRef = db.collection("Player").document(username);
        if (qr_score == 0) {       // derive from db
            db.collection("QR Code").document(qrName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            docRef.update("Score", FieldValue.increment((Long) task.getResult().get("Score")));
                        }
                    });
        } else {
            docRef.update("Score", FieldValue.increment(qr_score));
        }
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



