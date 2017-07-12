package com.codepath.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bcsam on 7/11/17.
 */

public class TextBody implements Parcelable{
    private double angerLevel;
    private double digustLevel;
    private double fearLevel;
    private double joyLevel;
    private double sadnessLevel;
    private String message;

    protected TextBody(Parcel in) {
        angerLevel = in.readDouble();
        digustLevel = in.readDouble();
        fearLevel = in.readDouble();
        joyLevel = in.readDouble();
        sadnessLevel = in.readDouble();
        message = in.readString();
    }

    public TextBody(){}

    public double getAngerLevel() {
        return angerLevel;
    }

    public void setAngerLevel(double angerLevel) {
        this.angerLevel = angerLevel;
    }

    public double getDigustLevel() {
        return digustLevel;
    }

    public void setDigustLevel(double digustLevel) {
        this.digustLevel = digustLevel;
    }

    public double getFearLevel() {
        return fearLevel;
    }

    public void setFearLevel(double fearLevel) {
        this.fearLevel = fearLevel;
    }

    public double getJoyLevel() {
        return joyLevel;
    }

    public void setJoyLevel(double joyLevel) {
        this.joyLevel = joyLevel;
    }

    public double getSadnessLevel() {
        return sadnessLevel;
    }

    public void setSadnessLevel(double sadnessLevel) {
        this.sadnessLevel = sadnessLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static final Creator<TextBody> CREATOR = new Creator<TextBody>() {
        @Override
        public TextBody createFromParcel(Parcel in) {
            return new TextBody(in);
        }

        @Override
        public TextBody[] newArray(int size) {
            return new TextBody[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(angerLevel);
        parcel.writeDouble(digustLevel);
        parcel.writeDouble(fearLevel);
        parcel.writeDouble(joyLevel);
        parcel.writeDouble(sadnessLevel);
        parcel.writeString(message);
    }
}
