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
