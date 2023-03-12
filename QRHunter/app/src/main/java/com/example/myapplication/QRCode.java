package com.example.myapplication;

import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents a QR code
 */
public class QRCode {
    private String name;
    private int score;
    private String sha256hex;
    private String location;

    public QRCode(String codeContents, String location) {
        this.location = location;
        sha256hex = DigestUtils.sha256Hex(codeContents);
        setName();
        setScore();
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

        for (int i = 0; i < 6; i++) {
            if (binaryRep.charAt(i) == '0') {
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


