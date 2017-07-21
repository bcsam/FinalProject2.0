package com.codepath.finalproject;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.telephony.SmsManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
    private String read;
    private int type;
    private int[] toneLevels;
    private int[] styleLevels;
    private int[] socialLevels;
    private int[] utteranceLevels;
    private String[] darkToneColors;
    private String[] lightToneColors;
    private Uri uri;
    private Context context;
    private String contactId;
    private int imageResource;

    public boolean isOther() {
        return other;
    }

    public void setOther(boolean other) {
        this.other = other;
    }

    private boolean other;

    public SMS(){
        toneLevels = new int[5];
        styleLevels = new int[3];
        socialLevels = new int[5];
        utteranceLevels = new int[7];
        darkToneColors = new String[]{"#b30000", "#267326", "#5900b3", "#e6b800", "#004d99"};
        lightToneColors = new String[]{"#e29c9c", "#9ce29c", "#c5a6d9", "#ffe680", "#a3c4f5"};
    }

    public String getNumber() {
        return number;
    }

    public int getType() { return type; }

    public void setType(int type) { this.type = type; }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public InputStream openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
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

    public String getRead() { return read; }

    public void setRead(String read) { this.read = read; }

    public int getToneLevel(int tone){
        return toneLevels[tone];
    }

    public void setToneLevel(int tone, double level){toneLevels[tone] = (int)(level*100);
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

    public String getStyleColor(){ return "#00334d"; }

    public String getSocialColor(){
        return "#2eb8b8";
    }

    public String getUtteranceColor(){
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
        return "#000000";
    }
    public String getBubbleColor() {
        int tone = 6;
        int level = 0;
        for(int i=0; i<5; i++) {
            if (toneLevels[i] > level) {
                level = toneLevels[i];
                tone = i;
            }
        }
        if(level > 50)
            return lightToneColors[tone];
        return "#D3D3D3";
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

    public SMS(Context context) {
        this.context = context;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
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
        dest.writeString(this.read);
        dest.writeInt(this.type);
        dest.writeIntArray(this.toneLevels);
        dest.writeIntArray(this.styleLevels);
        dest.writeIntArray(this.socialLevels);
        dest.writeIntArray(this.utteranceLevels);
        dest.writeStringArray(this.darkToneColors);
        dest.writeStringArray(this.lightToneColors);
        dest.writeParcelable(this.uri, flags);
        dest.writeString(this.contactId);
        dest.writeInt(this.imageResource);
    }

    protected SMS(Parcel in) {
        this.number = in.readString();
        this.body = in.readString();
        this.contact = in.readString();
        this.date = in.readString();
        this.read = in.readString();
        this.type = in.readInt();
        this.toneLevels = in.createIntArray();
        this.styleLevels = in.createIntArray();
        this.socialLevels = in.createIntArray();
        this.utteranceLevels = in.createIntArray();
        this.darkToneColors = in.createStringArray();
        this.lightToneColors = in.createStringArray();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.contactId = in.readString();
        this.imageResource = in.readInt();
    }

    public static final Creator<SMS> CREATOR = new Creator<SMS>() {
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
