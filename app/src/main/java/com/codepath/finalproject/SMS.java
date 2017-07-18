package com.codepath.finalproject;

import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.SmsManager;

/**
 * Created by andreadeoli on 7/13/17.
 */

public class SMS implements Parcelable {
    // Number from which the sms was sent for incoming
    //Number to which the sms should be sent for outgoing
    private String number;
    // SMS text body
    private String body;
    private String contact;
    private String date;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    //---sends an SMS message to another device---
    public void sendSMS(String contact, String phoneNumber, String message)
    {
        //PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0); todo for success/failure feedback
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    //---sends an SMS message to another device---
    public void sendSMS()
    {
        //PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0); todo for success/failure feedback
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number, null, body, null, null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.number);
        dest.writeString(this.body);
        dest.writeString(this.contact);
        dest.writeString(this.date);
    }

    public SMS() {
    }

    protected SMS(Parcel in) {
        this.number = in.readString();
        this.body = in.readString();
        this.contact = in.readString();
        this.date = in.readString();
    }

    public static final Parcelable.Creator<SMS> CREATOR = new Parcelable.Creator<SMS>() {
        @Override
        public SMS createFromParcel(Parcel source) {
            return new SMS(source);
        }

        @Override
        public SMS[] newArray(int size) {
            return new SMS[size];
        }
    };
}
