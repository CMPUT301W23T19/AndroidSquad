package com.example.myapplication;

import java.util.ArrayList;

public class HistoryModel {
    private String name;
    private Long score;
    private String location;
    private int playerCount;
    private Long count;
    private ArrayList<String> features;
    public HistoryModel(String name,Long score,String location, Long count, ArrayList<String> features){
        this.name= name;
        this.score= score;
        this.location=location;
        this.count=count;
        this.features = features;
    }

    public ArrayList<String> getFeatures() {return features;}
    public String getName(){
        return name;
    }
    public Long getScore(){
        return score;
    }

    public String getLocation(){return location;}

    public Long getCount(){return count;}

    //TODO: get location
    //TODO: get count
}
