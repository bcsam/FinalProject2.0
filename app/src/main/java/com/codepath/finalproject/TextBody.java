package com.codepath.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bcsam on 7/11/17.
 */

public class TextBody implements Parcelable{
    private double angerLevel;
    private double disgustLevel;
    private double fearLevel;
    private double joyLevel;
    private double sadnessLevel;
    private String message;

    protected TextBody(Parcel in) {
        angerLevel = in.readDouble();
        disgustLevel = in.readDouble();
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

    public double getDisgustLevel() {
        return disgustLevel;
    }

    public void setDisgustLevel(double digustLevel) {
        this.disgustLevel = digustLevel;
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

    public String getColor() {
        String tone = "";
        Double level = 0.00;
        if (angerLevel > level) {
            level = angerLevel;
            tone = "Anger";
        }
        else if (disgustLevel > level) {
            level = disgustLevel;
            tone = "Disgust";
        }
        else if (fearLevel > level) {
            level = fearLevel;
            tone = "Fear";
        }
        else if (joyLevel > level) {
            level = joyLevel;
            tone = "Joy";
        }
        else if (sadnessLevel > level) {
            level = sadnessLevel;
            tone = "Sadness";
        }
        if(level < .5)
            return "#00000000";
        switch (tone) {
            case ("Anger"):
                if (level > .75)
                    return "#b30000";
                else
                    return "#ff8080";
            case ("Disgust"):
                if (level > .75)
                    return "#5900b3";
                else
                    return "#cc99ff";
            case ("Fear"):
                if (level > .75)
                    return "#267326";
                else
                    return "#8cd98c";
            case ("Joy"):
                if (level > .75)
                    return "#e6b800";
                else
                    return "#ffdb4d";
            case ("Sadness"):
                if (level > .75)
                    return "#004d99";
                else
                    return "#80bfff";
        }
        return "#00000000";
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
        parcel.writeDouble(disgustLevel);
        parcel.writeDouble(fearLevel);
        parcel.writeDouble(joyLevel);
        parcel.writeDouble(sadnessLevel);
        parcel.writeString(message);
    }
}
