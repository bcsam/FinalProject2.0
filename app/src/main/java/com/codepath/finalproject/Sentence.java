package com.codepath.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bcsam on 7/11/17.
 */

public class Sentence implements Parcelable {
    private double angerLevel;
    private double digustLevel;
    private double fearLevel;
    private double joyLevel;
    private double sadnessLevel;
    private String message;

    public Sentence() {

    }

    protected Sentence(Parcel in) {
        angerLevel = in.readDouble();
        digustLevel = in.readDouble();
        fearLevel = in.readDouble();
        joyLevel = in.readDouble();
        sadnessLevel = in.readDouble();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(angerLevel);
        dest.writeDouble(digustLevel);
        dest.writeDouble(fearLevel);
        dest.writeDouble(joyLevel);
        dest.writeDouble(sadnessLevel);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sentence> CREATOR = new Creator<Sentence>() {
        @Override
        public Sentence createFromParcel(Parcel in) {
            return new Sentence(in);
        }

        @Override
        public Sentence[] newArray(int size) {
            return new Sentence[size];
        }
    };

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
}
