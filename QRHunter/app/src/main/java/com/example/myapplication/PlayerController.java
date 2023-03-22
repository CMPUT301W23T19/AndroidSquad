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

import java.util.ArrayList;

public class PlayerController {

    private Number score = 93;

    private static String username;

    private Number highestScore;

    private Number lowestScore;

    Player player;

    FirebaseFirestore db;

    public PlayerController(Number scores, Number highest, Number lowest, String username, FirebaseFirestore db) {
        this.username = username;
        this.db = db;

        if (scores != null) {
            this.score = scores;
            this.highestScore = highest;
            this.lowestScore = lowest;

            player = new Player(score,username,highestScore,lowestScore);
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
                            if(task.getResult().isEmpty()) {     // user has not scanned QR code before
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
    public void checkHighestScore(){
        db.collection("Player")
                .whereEqualTo("Username", username)
                .whereArrayContains("highestScore", score)
                .whereGreaterThan("highestScore", score)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()) {     // user has not scanned QR code before
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

    public void checkLowestScore(){
        db.collection("Player")
                .whereEqualTo("Username", username)
                .whereArrayContains("lowestScore", score)
                .whereGreaterThan("lowestScore", score)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()) {     // user has not scanned QR code before
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
     * @param qrName - String representation of the name of the QR code to be deleted
     */
    public void deleteQRFromHistory(String qrName) {
        db.collection("Player").document(username)
                .update("QRcode", FieldValue.arrayRemove(qrName))
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
     * @param qr_score - Integer representation of the recently added or deleted qr code
     */
    public void updateScore(int qr_score) {
        db.collection("Player")
                .document(username)
                .update("Score", FieldValue.increment(qr_score))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("PlayerController", "Updated user score successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("PlayerController", "Failed to update user's score");
                    }
                });
    }

    /**
     * Updates the player's highest score or lowest score when QR code is deleted, if needed
     * @param highLow - String that represents whether the high score or low score should be updated
     */
    public void deleteUpdateHighLowScore(String highLow){
        CollectionReference qrColRef = db.collection("QR Code");
        DocumentReference playerDocRef = db.collection("Player").document(username);
        playerDocRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> playerTask) {
                        ArrayList<String> qrCodes = (ArrayList<String>)playerTask.getResult().get("QRcode");
                        if ((qrCodes.isEmpty())){
                            Log.e("PlayerController", "There are no QR codes");
                        } else {
                            Log.e("PlayerController", "qrCodes:" + String.valueOf(qrCodes));

                            for (int i = 0; i < qrCodes.size(); i++) {
                                qrColRef.document(qrCodes.get(i))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> qrTask) {
                                            if (highLow.compareTo("high") == 0) {
                                                if ((long)qrTask.getResult().get("Score") > (long) playerTask.getResult().get("highestScore")) {
                                                    playerDocRef.update("highestScore", qrTask.getResult().get("Score"));
                                                    Log.e("PlayerController", "Successfully updated highest score");
                                                }
                                            } else {
                                                if ((long)qrTask.getResult().get("Score") < (long) playerTask.getResult().get("lowestScore")) {
                                                    playerDocRef.update("lowestScore", qrTask.getResult().get("Score"));
                                                    Log.e("PlayerController", "Successfully updated lowest score");
                                                }
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
     * @param qr_score - Integer representation of the score of the recently added QR code
     */
    public void addUpdateHighLow(int qr_score){
        DocumentReference playerDocRef = db.collection("Player").document(username);
        playerDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if ((long) task.getResult().get("highestScore") < (long)qr_score) {
                    playerDocRef.update("highestScore", qr_score);
                    Log.e("PlayerController", "Updated High Score");
                }
                if ((long) task.getResult().get("lowestScore") > (long)qr_score) {
                    playerDocRef.update("lowestScore", qr_score);
                    Log.e("PlayerController", "Updated Low Score");

                }
            }
        });

    }

}

