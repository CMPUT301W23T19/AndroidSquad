package com.example.myapplication;

public class HistoryModel {
    private String name;
    private Long score;
    private String location;
    private int playerCount;
    private Long count;
    public HistoryModel(String name,Long score,String location, Long count ){
        this.name= name;
        this.score= score;
        this.location=location;
        this.count=count;
    }

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
