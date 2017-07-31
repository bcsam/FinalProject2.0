package com.codepath.finalproject;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by andreadeoli on 7/31/17.
 */

public class FinalProject extends Application {
    private ArrayList<SMS> incomingList;
    private ArrayList<SMS> outgoingList;
    private ArrayList<SMS> smsList;

    public ArrayList<SMS> getIncomingList() {
        return incomingList;
    }

    public void setIncomingList(ArrayList<SMS> incomingList) {
        this.incomingList = incomingList;
    }

    public ArrayList<SMS> getOutgoingList() {
        return outgoingList;
    }

    public void setOutgoingList(ArrayList<SMS> outgoingList) {
        this.outgoingList = outgoingList;
    }

    public ArrayList<SMS> getSmsList() {
        return smsList;
    }

    public void setSmsList(ArrayList<SMS> smsList) {
        this.smsList = smsList;
    }
}
