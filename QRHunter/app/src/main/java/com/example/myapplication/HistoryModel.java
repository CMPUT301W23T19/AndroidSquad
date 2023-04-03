package com.example.myapplication;

import java.util.ArrayList;

/**
 * Class that stores QR code information queried from firebase
 * @authors: Randy, Angela
 */
public class HistoryModel {
    private String name;
    private Long score;
    private String location;
    private int playerCount;
    private Long count;
    private ArrayList<String> features;

    /**
     * Constructor function for HistoryModel
     * @param name - String representation of the QR Code name
     * @param score - Long representation of the QR Code score
     * @param location - String representation of the QR Code location
     * @param count - Long representation of the number of players who have scanned the QR Code
     * @param features - ArrayList<String> containing a series of "0" and "1" that determines what
     *                 features will be drawn
     */
    public HistoryModel(String name,Long score,String location, Long count, ArrayList<String> features){
        this.name= name;
        this.score= score;
        this.location=location;
        this.count=count;
        this.features = features;
    }

    /**
     * Gets ArrayList containing features that will be included in the QR code's
     * visual representation
     * @return ArrayList<String> containing a series of "0" and "1" that determines what
     * features will be drawn
     */
    public ArrayList<String> getFeatures() {return features;}

    /**
     * Gets name of QR Code
     * @return String representation of QR Code's name
     */
    public String getName(){
        return name;
    }

    /**
     * Gets score of QR Code
     * @return Long representation of QR Code's score
     */
    public Long getScore(){
        return score;
    }

    /**
     * Gets location of QR Code
     * @return String representation of QR Code's location
     */
    public String getLocation(){return location;}

    /**
     * Gets count of players who have scanned the QR Code
     * @return Long representation of count
     */
    public Long getCount(){return count;}

}
