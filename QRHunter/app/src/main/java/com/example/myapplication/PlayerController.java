package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PlayerController {



    private Number score = 93;

    private static String username;

    private Number highestScore;

    private Number lowestScore;

    Player player;

    FirebaseFirestore db;

    public PlayerController(Number scores, Number highest, Number lowest, String usersname, FirebaseFirestore db) {

        username = usersname;
        this.db = db;

        this.score = scores;
        this.highestScore = highest;
        this.lowestScore = lowest;


        player = new Player(score,username,highestScore,lowestScore);

    }


    public void checkSumScore(){
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







}

