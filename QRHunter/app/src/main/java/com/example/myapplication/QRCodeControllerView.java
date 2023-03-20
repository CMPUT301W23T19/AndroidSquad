package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QRCodeControllerView {
    private FirebaseFirestore db;
    QRCode qrCode;
    private int playerCount;

    public QRCodeControllerView(String codeContents, FirebaseFirestore db){
        this.db = db;
        qrCode = new QRCode(codeContents, null);
        playerCount = 0;
    }

    /**
     * Gets name of QR code
     * @return string representation of the name
     */
    public String getName() {
        return qrCode.getName();
    }

    /**
     * Gets QR code score
     * @return integer representation of the QR code score
     */
    public int getScore() {
        return qrCode.getScore();
    }

    /**
     * Gets QR code location
     * @return string representation of the QR code's geolocation
     */
    public String getLocation() {
        return qrCode.getLocation();
    }

    /**
     * Gets an Integer list containing ids of the QR code features ids
     * @return
     */
    public ArrayList<Integer> getAvatarlist() {
        return qrCode.getAvatarList();
    }


    /**
     * Counts number of users who have scanned the QR code
     * @param names - ArrayList containing usernames in string format
     * @return integer representation of the user count
     */
    public int assignPlayerCount(ArrayList<String> names) {
        for (int i = 0; i < names.size(); i++) {
            this.playerCount += 1;
        }
        Log.d("TAG", String.valueOf(playerCount));
        return playerCount;
    }






}
