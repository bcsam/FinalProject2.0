package com.codepath.finalproject;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by vf608 on 7/13/17.
 */

public class User implements Parcelable{
    private int averageToneLevels[];
    private int averageStyleLevels[];
    private int averageSocialLevels[];
    private int messageCount;
    private String name;
    private String number;
    private Uri profileImage;
    private String[] darkToneColors;
    private Context context;
    private ArrayList<SMS> conversation;

    private boolean other;

    private String contactId;

    public User(){
        averageToneLevels = new int[5];
        averageStyleLevels =  new int[3];
        averageSocialLevels = new int[5];
        messageCount = 0;
        name = "";
        number = "";
        contactId = "";
        profileImage = null;
        darkToneColors = new String[]{"#C3412F", "#73A939", "#8943AF", "#EFCF4F", "#277B9C"};
        conversation = new ArrayList<SMS>();
    }

    public User(Context context){
        averageToneLevels = new int[5];
        averageStyleLevels =  new int[3];
        averageSocialLevels = new int[5];
        messageCount = 0;
        name = "";
        number = "";
        contactId = "";
        profileImage = null;
        darkToneColors = new String[]{"#C3412F", "#73A939", "#8943AF", "#EFCF4F", "#277B9C"};
        this.context = context;
        conversation = new ArrayList<SMS>();
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
            for (int i = 0; i < 5; i++) {
                if (i < 3)
                    averageStyleLevels[i] = (averageStyleLevels[i] * (messageCount - 1) + sms.getStyleLevel(i)) / messageCount;
                averageToneLevels[i] = (averageToneLevels[i] * (messageCount - 1) + sms.getToneLevel(i)) / messageCount;
                averageSocialLevels[i] = (averageSocialLevels[i] * (messageCount - 1) + sms.getSocialLevel(i)) / messageCount;
            }

        }
        else{
            for (int i = 0; i < 5; i++) {
                if (i < 3)
                    averageStyleLevels[i] = sms.getStyleLevel(i);
                averageToneLevels[i] = sms.getToneLevel(i);
                averageSocialLevels[i] = sms.getSocialLevel(i);
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

    public void setName(String name){
        this.name = name;
    }

    public void setNumber(String number){ this.number = number; }

    public void setProfileImageUri(Uri profileImage){ this.profileImage = profileImage; }

    //formats numbers +1(555)555-5555
    public String toStringNumber(){
        String newNumber = PhoneNumberUtils.formatNumber(number);
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

    public String getToneColor(int tone){
        return darkToneColors[tone];
    }

    public ArrayList<SMS> getConversation(){ return conversation; }

    public void setConversation(ArrayList<SMS> conversation){ this.conversation = conversation; }

    public void clear(){
        messageCount = 0;
        for(int i=0; i<5; i++){
            if(i<3)
                averageStyleLevels[i] = 0;
            averageToneLevels[i] = 0;
            averageSocialLevels[i] = 0;
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
        dest.writeInt(this.messageCount);
        dest.writeString(this.name);
        dest.writeString(this.number);
        dest.writeParcelable(this.profileImage, flags);
        dest.writeStringArray(this.darkToneColors);
        dest.writeTypedList(this.conversation);
        dest.writeByte(this.other ? (byte) 1 : (byte) 0);
        dest.writeString(this.contactId);
    }

    protected User(Parcel in) {
        this.averageToneLevels = in.createIntArray();
        this.averageStyleLevels = in.createIntArray();
        this.averageSocialLevels = in.createIntArray();
        this.messageCount = in.readInt();
        this.name = in.readString();
        this.number = in.readString();
        this.profileImage = in.readParcelable(Uri.class.getClassLoader());
        this.darkToneColors = in.createStringArray();
        this.conversation = in.createTypedArrayList(SMS.CREATOR);
        this.other = in.readByte() != 0;
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
