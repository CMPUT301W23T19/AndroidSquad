package com.example.myapplication;

public class HistoryModel {
    private String name;
    private Long score;
    private String location;
    private int playerCount;

    public HistoryModel(String name,Long score){
        this.name= name;
        this.score= score;
    }

    public String getName(){
        return name;
    }
    public Long getScore(){
        return score;
    }

    //TODO: get location
    //TODO: get count
}
