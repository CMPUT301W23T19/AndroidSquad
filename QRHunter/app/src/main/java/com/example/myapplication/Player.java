package com.example.myapplication;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private String avatar;
    private String machinecode;
    private String name;
    private ArrayList<String> qrcode;
    private Integer score;
    private static String username;
    private Integer highestscore;
    private Integer lowestscore;

    //#TODO: Remove this Constructor is a must. Please change all related usages to Constructor below
    public Player(Integer score, String username, Integer highestscore, Integer lowestscore) {
//        Avatar = avatar;
//        Machinecode = machinecode;
//        Name = name;
//        Qrcode = qrcode;
        this.score = score;
        this.username = username;
        this.highestscore = highestscore;
        this.lowestscore = lowestscore;
    }
    public Player(String name, Integer score, String username, Integer highestscore, Integer lowestscore,ArrayList<String> qrcode, String machineCode, String avatar) {
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

    public Integer getHighestscore() {
        return highestscore;
    }

    public void setHighestscore(Integer highestscore) {
        this.highestscore = highestscore;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getLowestscore() {
        return lowestscore;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setLowestscore(Integer lowestscore) {
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
