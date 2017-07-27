package com.codepath.finalproject;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by vf608 on 7/13/17.
 */

public class User implements Parcelable{
    private int averageToneLevels[];
    private int averageStyleLevels[];
    private int averageSocialLevels[];
    private int averageUtteranceLevels[];
    private int messageCount;
    private String name;
    private String number;
    private Uri profileImage;
    private String[] darkToneColors;
    private Context context;

    private boolean other;

    private String contactId;

    public User(){
        averageToneLevels = new int[5];
        averageStyleLevels =  new int[3];
        averageSocialLevels = new int[5];
        averageUtteranceLevels = new int[7];
        messageCount = 0;
        name = "";
        number = "";
        contactId = "";
        profileImage = null;
        darkToneColors = new String[]{"#C3412F", "#73A939", "#8943AF", "#EFCF4F", "#277B9C"};
    }

    public User(Context context){
        averageToneLevels = new int[5];
        averageStyleLevels =  new int[3];
        averageSocialLevels = new int[5];
        averageUtteranceLevels = new int[7];
        messageCount = 0;
        name = "";
        number = "";
        contactId = "";
        profileImage = null;
        darkToneColors = new String[]{"#C3412F", "#73A939", "#8943AF", "#EFCF4F", "#277B9C"};
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isOther() {
        return other;
    }

    public void setOther(boolean other) {
        this.other = other;
    }

    public void updateScores(SMS sms){
        messageCount++;
        if(messageCount>1) {
            for (int i = 0; i < 7; i++) {
                if (i < 3)
                    averageStyleLevels[i] = (averageStyleLevels[i] * (messageCount - 1) + sms.getStyleLevel(i)) / messageCount;
                if (i < 5) {
                    averageToneLevels[i] = (averageToneLevels[i] * (messageCount - 1) + sms.getToneLevel(i)) / messageCount;
                    averageSocialLevels[i] = (averageSocialLevels[i] * (messageCount - 1) + sms.getSocialLevel(i)) / messageCount;
                }
                averageUtteranceLevels[i] = (averageUtteranceLevels[i] * (messageCount - 1) + sms.getUtteranceLevel(i)) / messageCount;
            }
        }
        else{
            for (int i = 0; i < 7; i++) {
                if (i < 3)
                    averageStyleLevels[i] = sms.getStyleLevel(i);
                if (i < 5) {
                    averageToneLevels[i] = sms.getToneLevel(i);
                    averageSocialLevels[i] = sms.getSocialLevel(i);
                }
                averageUtteranceLevels[i] = sms.getUtteranceLevel(i);
            }
        }
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

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public int getAverageToneLevels(int tone){ return averageToneLevels[tone]; }

    public void setAverageToneLevels(int tone, int level){ averageToneLevels[tone] = level; }

    public int getAverageStyleLevels(int style){ return averageStyleLevels[style]; }

    public void setAverageStyleLevels(int style, int level){ averageStyleLevels[style] = level; }

    public int getAverageSocialLevels(int social){ return averageSocialLevels[social]; }

    public void setAverageSocialLevels(int social, int level){ averageSocialLevels[social] = level; }

    public int getAverageUtteranceLevels(int utterance){ return averageUtteranceLevels[utterance]; }

    public void setAverageUtteranceLevels(int utterance, int level){ averageUtteranceLevels[utterance] = level; }

    public void setName(String name){
        this.name = name;
    }

    public void setNumber(String number){ this.number = number; }

    public void setProfileImageUri(Uri profileImage){ this.profileImage = profileImage; }

    //formats numbers +1(555)555-5555
    public String toStringNumber(){
        String newNumber = number;
        if(newNumber.length() > 7)
            newNumber = number.substring(0,2) + " (" + number.substring(2,5) + ") " + number.substring(5,8) + "-" + number.substring(8);
        return newNumber;
    }

    public String getName(){ return name; }

    public String getNumber(){ return number; }

    public Uri getProfileImageUri(){ return profileImage; }

    public String getStyleColor(){
        return "#c66a30";
    }

    public String getSocialColor(){
        return "#c66a30";
    }

    public String getUtteranceColor(){
        return "#600080";
    }

    public String getToneColor(int tone){
        return darkToneColors[tone];
    }

    public void clear(){
        messageCount = 0;
        for(int i=0; i<7; i++){
            if(i<4)
                averageStyleLevels[i] = 0;
            if(i<6){
                averageToneLevels[i] = 0;
                averageSocialLevels[i] = 0;
            }
            averageUtteranceLevels[i] = 0;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.averageToneLevels);
        dest.writeIntArray(this.averageStyleLevels);
        dest.writeIntArray(this.averageSocialLevels);
        dest.writeIntArray(this.averageUtteranceLevels);
        dest.writeInt(this.messageCount);
        dest.writeString(this.name);
        dest.writeString(this.number);
        dest.writeStringArray(this.darkToneColors);
        dest.writeString(this.contactId);
    }

    protected User(Parcel in) {
        this.averageToneLevels = in.createIntArray();
        this.averageStyleLevels = in.createIntArray();
        this.averageSocialLevels = in.createIntArray();
        this.averageUtteranceLevels = in.createIntArray();
        this.messageCount = in.readInt();
        this.name = in.readString();
        this.number = in.readString();
        this.darkToneColors = in.createStringArray();
        this.contactId = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
