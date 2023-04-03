package com.example.myapplication;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that models a Player
 * @authors: Jessie, Shirley, Aamna
 */
public class Player implements Serializable {
    private String avatar;
    private String machinecode;
    private String name;
    private ArrayList<String> qrcode;
    private Integer score;
    private static String username;
    private Integer highestscore;
    private Integer lowestscore;

    /**
     * Constructor function for Player
     * @param name - String representation of Player's name
     * @param score- Integer representation of Player's score
     * @param username- String representation of Player's username
     * @param highestscore- Integer representation of Player's highest score
     * @param lowestscore- Intger representation of Player's lowest score
     * @param qrcode- ArrayList containing the names of QR codes scanned by user
     * @param machineCode- String representation of Player's machine code
     * @param avatar- String representation of Player's avatar
     */
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

    /**
     * Gets the player's highest score
     * @return Integer representation of their highest score
     */
    public Integer getHighestscore() {
        return highestscore;
    }

    /**
     * Gets the player's username
     * @return String representation of their username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the player's username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the player's lowest score
     * @return Integer representation of their lowest score
     */
    public Integer getLowestscore() {
        return lowestscore;
    }

    /**
     * Gets the player's total score
     * @return Integer representation of their score
     */
    public Integer getScore() {
        return score;
    }

    /**
     * Sets the player's score
     */
    public void setScore(Integer score) {
        this.score = score;
    }

    /**
     * Gets the player's history of scanned QR codes
     * @return ArrayList containing QR codes they have scanned
     */
    public ArrayList<String> getQrcode() {
        return this.qrcode;
    }

    /**
     * Sets the player's history of scanned QR codes
     */
    public void setQrcode(ArrayList<String> qrcode) {
        this.qrcode = qrcode;
    }

    /**
     * Sets the player's avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Gets the player's name
     * @return String representation of their name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the player's name
     */
    public void setName(String name) {
        this.name = name;
    }
}
