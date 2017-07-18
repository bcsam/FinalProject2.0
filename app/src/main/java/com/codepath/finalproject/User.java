package com.codepath.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vf608 on 7/13/17.
 */

public class User implements Parcelable{
    private int averageToneLevels[];
    private int averageStyleLevels[];
    private int averageSocialLevels[];
    private int averageUtteranceLevels[];
    private int messageCount;
    private String name;
    private String number;
    private String[] darkToneColors;

    public User(){
        averageToneLevels = new int[5];
        averageStyleLevels =  new int[3];
        averageSocialLevels = new int[5];
        averageUtteranceLevels = new int[7];
        messageCount = 0;
        name = "";
        number = "";
        darkToneColors = new String[]{"#b30000", "#267326", "#5900b3", "#e6b800", "#004d99"};
    }

    public void updateScores(TextBody textBody){
        messageCount++;
        for(int i=0; i<7; i++){
            if(i<3)
                averageStyleLevels[i] = (averageStyleLevels[i] + textBody.getStyleLevel(i)) / messageCount;
            if(i<5){
                averageToneLevels[i] = (averageToneLevels[i] + textBody.getToneLevel(i)) / messageCount;
                averageSocialLevels[i] = (averageSocialLevels[i] + textBody.getSocialLevel(i)) / messageCount;
            }
            averageUtteranceLevels[i] = (averageUtteranceLevels[i] + textBody.getUtteranceLevel(i)) / messageCount;
        }
    }

    public int getAverageToneLevels(int tone){ return averageToneLevels[tone]; }

    public void setAverageToneLevels(int tone, int level){ averageToneLevels[tone] = level; }

    public int getAverageStyleLevels(int style){ return averageStyleLevels[style]; }

    public void setAverageStyleLevels(int style, int level){ averageStyleLevels[style] = level; }

    public int getAverageSocialLevels(int social){ return averageSocialLevels[social]; }

    public void setAverageSocialLevels(int social, int level){ averageSocialLevels[social] = level; }

    public int getAverageUtteranceLevels(int utterance){ return averageUtteranceLevels[utterance]; }

    public void setAverageUtteranceLevels(int utterance, int level){ averageUtteranceLevels[utterance] = level; }

    public void setName(String name){
        this.name = name;
    }

    public void setNumber(String number){ this.number = number; }

    public String toStringNumber(){
        String newNumber = number;
        if(newNumber.length() > 7)
            newNumber = number.substring(0,2) + " (" + number.substring(2,5) + ") " + number.substring(5,8) + "-" + number.substring(8);
        return newNumber;
    }

    public String getName(){ return name; }

    public String getNumber(){ return number; }

    public String getStyleColor(){
        return "#00334d";
    }

    public String getSocialColor(){
        return "#2eb8b8";
    }

    public String getUtteranceColor(){
        return "#600080";
    }

    public String getToneColor(int tone){
        return darkToneColors[tone];
    }

    public void clear(){
        messageCount = 0;
        for(int i=0; i<7; i++){
            if(i<4)
                averageStyleLevels[i] = 0;
            if(i<6){
                averageToneLevels[i] = 0;
                averageSocialLevels[i] = 0;
            }
            averageUtteranceLevels[i] = 0;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.averageToneLevels);
        dest.writeIntArray(this.averageStyleLevels);
        dest.writeIntArray(this.averageSocialLevels);
        dest.writeIntArray(this.averageUtteranceLevels);
        dest.writeInt(this.messageCount);
        dest.writeString(this.name);
        dest.writeString(this.number);
        dest.writeStringArray(this.darkToneColors);
    }

    protected User(Parcel in) {
        this.averageToneLevels = in.createIntArray();
        this.averageStyleLevels = in.createIntArray();
        this.averageSocialLevels = in.createIntArray();
        this.averageUtteranceLevels = in.createIntArray();
        this.messageCount = in.readInt();
        this.name = in.readString();
        this.number = in.readString();
        this.darkToneColors = in.createStringArray();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
