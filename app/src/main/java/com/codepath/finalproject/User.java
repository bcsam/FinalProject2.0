package com.codepath.finalproject;

/**
 * Created by vf608 on 7/13/17.
 */

public class User {
    private int averageToneLevels[];
    private int averageStyleLevels[];
    private int averageSocialLevels[];
    private int averageUtteranceLevels[];
    private int messageCount;
    private String name;
    private String number;

    public User(){
        averageToneLevels = new int[5];
        averageStyleLevels =  new int[3];
        averageSocialLevels = new int[5];
        averageUtteranceLevels = new int[7];
        messageCount = 0;
        name = "";
        number = "";
    }

    public void updateScores(TextBody textBody){
        messageCount++;
        for(int i=0; i<7; i++){
            if(i<4)
                averageStyleLevels[i] = (averageStyleLevels[i] + textBody.getStyleLevel(i)) / messageCount;
            if(i<6){
                averageToneLevels[i] = (averageToneLevels[i] + textBody.getToneLevel(i)) / messageCount;
                averageSocialLevels[i] = (averageSocialLevels[i] + textBody.getSocialLevel(i)) / messageCount;
            }
            averageUtteranceLevels[i] = (averageUtteranceLevels[i] + textBody.getUtteranceLevel(i)) / messageCount;
        }
    }

    public int getAverageToneLevels(int tone){ return averageToneLevels[tone]; }

    public int getAverageStyleLevels(int style){ return averageStyleLevels[style]; }

    public int getAverageSocialLevels(int social){ return averageToneLevels[social]; }

    public int getAverageUtteranceLevels(int utterance){ return averageUtteranceLevels[utterance]; }

    public void setName(String name){
        this.name = name;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public String getName(){ return name; }

    public String getNumber(){ return number; }
}
