package com.example.myapplication;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private String Avatar;
    private String Machinecode;
    private static String Name;
    private ArrayList<String> Qrcode;
    private Number score;
    private static String username;
    private Number highestscore;
    private Number lowestscore;

    public String getAvatar() {
        return Avatar;
    }


    public Player(Number score, String username, Number highestscore, Number lowestscore) {
//        Avatar = avatar;
//        Machinecode = machinecode;
//        Name = name;
//        Qrcode = qrcode;
        this.score = score;
        this.username = username;
        this.highestscore = highestscore;
        this.lowestscore = lowestscore;
    }

    public String getMachinecode() {
        return Machinecode;
    }

    public void setMachinecode(String machinecode) {
        Machinecode = machinecode;
    }

    public Number getHighestscore() {
        return highestscore;
    }

    public void setHighestscore(Number highestscore) {
        this.highestscore = highestscore;
    }

    public static String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Number getLowestscore() {
        return lowestscore;
    }

    public Number getScore() {
        return score;
    }

    public void setScore(Number score) {
        this.score = score;
    }

    public void setLowestscore(Number lowestscore) {
        this.lowestscore = lowestscore;
    }

    public ArrayList<String> getQrcode() {
        return Qrcode;
    }

    public void setQrcode(ArrayList<String> qrcode) {
        Qrcode = qrcode;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public static String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
