/**
 * Converting string to sha256hex:
 * From: www.stackoverflow.com
 * URL: https://stackoverflow.com/q/5531455
 * Author: https://stackoverflow.com/users/69875/jonathan
 * License: CC BY-SA
 * Converting hex string to binary string: https://stackoverflow.com/q/9246326
 * -- Author(s): https://stackoverflow.com/users/20394/mike-samuel
 * Iterating over key-val pairs in a HashMap: https://stackoverflow.com/q/585654
 * -- Author(s): https://stackoverflow.com/users/40342/joachim-sauer
 * Creating and executing a query: https://firebase.google.com/docs/firestore/query-data/queries
 * Updating a document and using arrayUnion() to add items to an array field: https://cloud.google.com/firestore/docs/manage-data/add-data#javaandroid_12
 * Inspired by youtube video to create parts of the avatar (https://www.youtube.com/watch?v=L1E_7FoTrik
 * -- Author(s): https://www.youtube.com/@TheTechTrain/about
 * Used PNG to SVG converter (https://image.online-convert.com/convert-to-svg)
 * Site used to make avatar (https://boxy-svg.com)
 */



package com.example.myapplication;

import android.util.Log;
import android.widget.ImageView;

import com.google.common.hash.Hashing;


import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents a QR code
 * @authors: Angela, Aamna
 */
public class QRCode implements Serializable {
    private String name;
    private int score;
    private String sha256hex;
    private String location;


    /**
     * Constructor function for QRCode
     * @param codeContents - String representation of the QR code contents
     * @param location - String representation of QR Code location
     */
    public QRCode(String codeContents, String location) {
        this.location = location;
        sha256hex = Hashing.sha256()
                .hashString(codeContents, StandardCharsets.UTF_8)
                .toString();
        setName();
        Log.e("QRCode", name);
        setScore();
        Log.e("QRCode", name);
    }

    /**
     * Creates a unique name for a newly scanned QR code based on the binary representation of its hash
     */
    private void setName() {
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
        Log.e("QRCode", binaryRep);
        for (int i = 0; i < 6; i++) {
            if (binaryRep.charAt(i+1) == '0') {
                name += names.get(i)[0];
            }
            else {
                name += names.get(i)[1];
            }
        }
    }

    /**
     * Creates a score for the QR code based on its hash
     */
    private void setScore() {
        HashMap<Character, Integer> scores = new HashMap<Character, Integer>();
        for (int i = 0; i < (sha256hex.length()-1); i++) {
            char curChar = sha256hex.charAt(i);
            if (curChar == sha256hex.charAt(i+1)) {
                if (scores.containsKey(curChar) == false) {
                    scores.put(curChar, 2);
                } else {
                    scores.put(curChar, scores.get(curChar) + 1);
                }
            }
        }

        for (Map.Entry<Character, Integer> set : scores.entrySet()) {
            Character key = Character.toUpperCase(set.getKey());
            int count = set.getValue();
            if (key == '0') {
                score += (int) Math.pow(20,(count-1));
            }
            else if (Character.isDigit(key)) {
                score += (int) Math.pow((int)(key-48),(count-1));
            }
            else {
                score += (int) Math.pow((int)(key-55),(count-1));
            }
        }
    }

    /**
     * Creates a unique avatar image for a newly scanned QR code based on the binary representation of its hash
     * @return an arraylist consisting of "0" and "1" that indicate what features are
     * included in the visual representation
     */

    public ArrayList<String> setAvatar() {
        String sixHash = sha256hex.substring(0,5);
        String binaryRep = new BigInteger(sixHash, 16).toString(2);

        ArrayList<String> avatarList = new ArrayList<>();

        HashMap<Integer, Integer[]> faces = new HashMap<>();
        faces.put(0, new Integer[]{R.id.face1, R.id.face2});
        faces.put(1, new Integer[]{R.id.eyebrow1, R.id.eyebrow2});
        faces.put(2, new Integer[]{R.id.eye1, R.id.eye2});
        faces.put(3, new Integer[]{R.id.nose1, R.id.nose2});
        faces.put(4, new Integer[]{R.id.mouth1, R.id.mouth2});

      for (int i = 0; i < 5; i++) {
          if (binaryRep.charAt(i+1) == '0') {
//              avatarList.add(faces.get(i)[0]);
              avatarList.add("0");
          } else {
//              avatarList.add(faces.get(i)[1]);
              avatarList.add("1");
          }
      }
        return avatarList;
    }

    /**
     * Gets unique visual represented avatar of QR code
     * @return an arraylist consisting of "0" and "1" that indicate what features are
     * to be drawn
     */
    public ArrayList<String> getAvatarList() {
        ArrayList<String> avatarlist = setAvatar();
        return avatarlist;
    }

    /**
     * Gets name of QR code
     * @return string representation of the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets QR code score
     * @return integer representation of the QR code score
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets QR code hash
     * @return String representation of the QR code SHA-256 hex
     */
    public String getHash() {
        return sha256hex;
    }

    /**
     * Gets QR code location
     * @return String representation of the QR code's geolocation
     */
    public String getLocation() {
        return location;
    }



}


