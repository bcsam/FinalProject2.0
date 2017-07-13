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
    private int[] utteranceLevels;
    private String[] darkToneColors;
    private String message;

    public TextBody(){
        toneLevels = new int[5];
        styleLevels = new int[3];
        socialLevels = new int[5];
        utteranceLevels = new int[7];
        darkToneColors = new String[]{"#b30000", "#267326", "#5900b3", "#e6b800", "#004d99"};
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

    public void setSocialLevel(int social, double level){ socialLevels[social] = (int)(level*100); }

    public int getUtteranceLevel(int utterance){
        return utteranceLevels[utterance];
    }

    public void setUtteranceLevel(int utterance, double level){ utteranceLevels[utterance] = (int)(level*100); }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStyleColor(int level){
        return "#00334d";
    }

    public String getSocialColor(int level){
        return "#2eb8b8";
    }

    public String getUtteranceColor(int level){
        return "#600080";
    }

    public String getToneColor(int tone){
        return darkToneColors[tone];
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
        if(level > 50)
            return darkToneColors[tone];
        return "#000000"; // TODO: 7/12/17 abstract this
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
        dest.writeIntArray(this.utteranceLevels);
        dest.writeStringArray(this.darkToneColors);
        dest.writeString(this.message);
    }

    protected TextBody(Parcel in) {
        this.toneLevels = in.createIntArray();
        this.styleLevels = in.createIntArray();
        this.socialLevels = in.createIntArray();
        this.utteranceLevels = in.createIntArray();
        this.darkToneColors = in.createStringArray();
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
