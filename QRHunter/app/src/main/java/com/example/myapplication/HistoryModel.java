package com.example.myapplication;

public class HistoryModel {
    private String name;
    private Long score;

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
}
