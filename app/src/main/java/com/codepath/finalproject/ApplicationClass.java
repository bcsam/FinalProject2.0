package com.codepath.finalproject;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by andreadeoli on 7/31/17.
 */

public class ApplicationClass extends Application {
    public ArrayList<SMS> getSmsList() {
        return smsList;
    }

    public void setSmsList(ArrayList<SMS> smsList) {
        this.smsList = smsList;
    }

    private ArrayList<SMS> smsList;
}
