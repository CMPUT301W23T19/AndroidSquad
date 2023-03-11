package com.example.myapplication;

/**
 * Idea to use Apache Common Codecs https://www.baeldung.com/sha-256-hashing-java
 * Apache Common Codecs: https://mvnrepository.com/artifact/commons-codec/commons-codec/1.15
 * Converting hex string to binary string: https://stackoverflow.com/q/9246326
 * -- Author(s): https://stackoverflow.com/users/20394/mike-samuel
 * Iterating over key-val pairs in a HashMap: https://stackoverflow.com/q/585654
 * -- Author(s): https://stackoverflow.com/users/40342/joachim-sauer
 */

import java.util.Set;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.apache.commons.codec.digest.DigestUtils;

import java.lang.reflect.GenericArrayType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Responsible for adding, modifying, deleting QR codes from database
 */
public class QRCodeController {
    private String name;
    private int score;
    private String sha256hex;
    FirebaseFirestore db;

    private String codeContents;
    private String user;
    // codeContents and username should be passed in from Camera Activity or different class

//    CameraActivity will scan a QR code and get the code contents. Then it will call a different class
//    and pass username and code contents.
//    In a different class (HandleScannedQrCode), create an instance of QRCodeController and pass in username and
//    code contents. In this class, call function checkIfExists to determine whether or not the QR code is
//    already in the database. If not, then call then
//    store in database by calling addQRCode. If it already exists, check if user has already scanned it
//    by calling checkIfScanned. If it is, then display error message. If not, then get QR code info from
//    QR code collection and display relevant information + add to user's history of QR codes.

    /**
     * Constructor function for QRCodeController
     */
    public QRCodeController( String codeContents, String username) {
        this.codeContents = codeContents;     // testing purposes
        sha256hex = DigestUtils.sha256Hex(codeContents);
        user = username;        // testing purposes
        db = FirebaseFirestore.getInstance();

        setName();
    }

    /**
     * Creates a unique name for a newly scanned QR code based on the binary representation of its hash
     */
    public void setName() {
        name = "";
        HashMap<Integer, String[]> names = new HashMap<>();
        names.put(0, new String[]{"Lunar", "Solar"});
        names.put(1, new String[]{"Flo", "Glo"});
        names.put(2, new String[]{"Stel", "Gal"});
        names.put(3, new String[]{"Mega", "Ultra"});
        names.put(4, new String[]{"Spectral", "Sonic"});
        names.put(5, new String[]{"Titan", "Supernova"});

        String sixHash = sha256hex.substring(0,5);
        String binaryRep = new BigInteger(sixHash, 16).toString(2);

        for (int i = 0; i < 6; i++) {
            if (binaryRep.charAt(i) == '0') {
                name += names.get(i)[0];
            }
            else {
                name += names.get(i)[1];
            }
        }
    }
    public String getName() {
        return name;
    }

//    /**
//     * Creates a score for the QR code based on its hash
//     */
//    public void setScore() {
//        HashMap<Character, Integer> scores = new HashMap<Character, Integer>();
//        for (int i = 0; i < (sha256hex.length()-1); i++) {
//            char curChar = sha256hex.charAt(i);
//            if (curChar == sha256hex.charAt(i+1)) {
//                if (scores.containsKey(curChar) == false) {
//                    scores.put(curChar, 2);
//                } else {
//                    scores.put(curChar, scores.get(curChar) + 1);
//                }
//            }
//        }
//
//        for (Map.Entry<Character, Integer> set : scores.entrySet()) {
//            Character key = Character.toUpperCase(set.getKey());
//            int count = set.getValue();
//            if (key == '0') {
//                score += (int) Math.pow(20,(count-1));
//            }
//            else if (Character.isDigit(key)) {
//                score += (int) Math.pow((int)(key-48),(count-1));
//            }
//            else {
//                score += (int) Math.pow((int)(key-55),(count-1));
//            }
//        }
//    }
//

//
//    public int getScore() {
//        return score;
//    }
//
//    public void checkIfExists() {
//        DocumentReference qrCodeRef = db.collection("QR Code").document(name);
//        qrCodeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot qrCode = task.getResult();
//                    if (!qrCode.exists()) {
//                        addQRCode();
//                    } else {
//                        Log.d("TAG", "Existing data" /** document.getData */);
//                    }
//                }
//            }
//        });
//
//    }
//
//    public void checkIfScanned() {
//        Query scanned = db.collection("Player").whereEqualTo("Username", user).whereArrayContains("QRCode", sha256hex);
//        System.out.println(scanned.get());
//
//    }
//
//    /**
//     * Adds QR code to Firestore Firebase database
//     */
//    public void addQRCode() {
//        ArrayList<String> comments = new ArrayList<>();
//        ArrayList<String> usernames = new ArrayList<>();
//
//        Map<String, Object> qrCode = new HashMap<>();
//        qrCode.put("Comment", comments);
//        qrCode.put("Hash", sha256hex);
//        qrCode.put("Location", null);
//        qrCode.put("Name", name);
//        qrCode.put("Photo", "code.png");
//        qrCode.put("Score", score);
//        qrCode.put("Username", usernames);
//
//        db.collection("QR Code").document(name)
//            .set(qrCode)
//            .addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void avoid) {
//                Log.d("TAG", "Added QR code successfully!");
//            }
//        })
//        .addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w("TAG", "Error adding QR code");
//            }
//        });
//
//    }


}
