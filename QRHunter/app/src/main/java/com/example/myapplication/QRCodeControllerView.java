package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QRCodeControllerView {
    private FirebaseFirestore db;
    QRCode qrCode;

    public QRCodeControllerView(String codeContents, String username, FirebaseFirestore db){
        this.db = db;
        qrCode = new QRCode(codeContents, username);
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
     * Gets the number of users who have scanned the QR code
     */
    public void playerCount() {
        int sum = 0;
//        db.collection("QR Code")
//                .whereEqualTo("Name", qrCode.getName())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()) {
//                            for (QueryDocumentSnapshot doc : task.getResult()) {
//                                for (int i = 0; i <  doc.getData().size(); i++) {
//                                    sum += 1;
//                                }
//
//                            }
//                        }
//                    }
//                });
    }






}
