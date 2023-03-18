package com.example.myapplication;

import android.graphics.Bitmap;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private String avatar;
    private String machinecode;
    private String name;
    private ArrayList<String> qrcode;
    private Number score;
    private static String username;
    private Number highestscore;
    private Number lowestscore;

    //#TODO: Remove this Constructor is a must. Please change all related usages to Constructor below
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
    public Player(String name, Number score, String username, Number highestscore, Number lowestscore,ArrayList<String> qrcode, String machineCode, String avatar) {
        this.avatar = avatar;
        this.machinecode = machineCode;
        this.name = name;
        this.qrcode = qrcode;
        this.score = score;
        this.username = username;
        this.highestscore = highestscore;
        this.lowestscore = lowestscore;
    }

    public String getMachinecode() {
        return this.machinecode;
    }

    public void setMachinecode(String machinecode) {
        this.machinecode = machinecode;
    }

    public Number getHighestscore() {
        return highestscore;
    }

    public void setHighestscore(Number highestscore) {
        this.highestscore = highestscore;
    }

    public String getUsername() {
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
        return this.qrcode;
    }

    public void setQrcode(ArrayList<String> qrcode) {
        this.qrcode = qrcode;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
