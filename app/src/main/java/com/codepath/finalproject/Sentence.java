package com.codepath.finalproject;

/**
 * Created by bcsam on 7/11/17.
 */

public class Sentence {
    private double angerLevel;
    private double digustLevel;
    private double fearLevel;
    private double joyLevel;
    private double sadnessLevel;
    private String message;

    public Sentence() {

    }

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
