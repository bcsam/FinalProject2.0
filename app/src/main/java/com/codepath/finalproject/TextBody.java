package com.codepath.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bcsam on 7/11/17.
 */

public class TextBody implements Parcelable{
    private int[] toneLevels;
    private int[] styleLevels;
    private int[] socialLevels;
    private String[] lightToneColors;
    private String[] darkToneColors;
    private String message;

    public TextBody(){
        toneLevels = new int[5];
        styleLevels = new int[3];
        socialLevels = new int[5];
        lightToneColors = new String[]{"#ff8080", "#cc99ff", "#8cd98c", "#ffdb4d", "#80bfff"};
        darkToneColors = new String[]{"#b30000", "#5900b3", "#267326", "#e6b800", "#004d99"};
    }

    public int getToneLevel(int tone){
        return toneLevels[tone];
    }

    public void setToneLevel(int tone, double level){
        toneLevels[tone] = (int)(level*100);
    }

    public int getStyleLevel(int style){
        return styleLevels[style];
    }

    public void setStyleLevel(int style, double level){
        styleLevels[style] = (int)(level*100);
    }

    public int getSocialLevel(int social){
        return socialLevels[social];
    }

    public void setSocialLevel(int social, double level){
        socialLevels[social] = (int)(level*100);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStyleColor(int level){
        if(level < 50)
            return "#80a6b3";
        else
            return "#00334d";
    }

    public String getSocialColor(int level){
        if(level < 50)
            return "#9cc9c9";
        else
            return "#2eb8b8";
    }

    public String getToneColor(int tone){
        int level = toneLevels[tone];
        if(level > 50)
            return darkToneColors[tone];
        return lightToneColors[tone];
    }

    public String getTextColor() {
        int tone = 6;
        int level = 0;
        for(int i=0; i<5; i++) {
            if (toneLevels[i] > level) {
                level = toneLevels[i];
                tone = i;
            }
        }
        if(level < 50)
            return "#000000";
        if(level > 75)
            return darkToneColors[tone];
        return lightToneColors[tone];
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.toneLevels);
        dest.writeIntArray(this.styleLevels);
        dest.writeIntArray(this.socialLevels);
        dest.writeString(this.message);
    }

    protected TextBody(Parcel in) {
        this.toneLevels = in.createIntArray();
        this.styleLevels = in.createIntArray();
        this.socialLevels = in.createIntArray();
        this.message = in.readString();
    }

    public static final Creator<TextBody> CREATOR = new Creator<TextBody>() {
        @Override
        public TextBody createFromParcel(Parcel source) {
            return new TextBody(source);
        }

        @Override
        public TextBody[] newArray(int size) {
            return new TextBody[size];
        }
    };
}
