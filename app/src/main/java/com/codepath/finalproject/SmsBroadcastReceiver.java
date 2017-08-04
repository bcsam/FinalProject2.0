package com.codepath.finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;

/**
 * Created by andreadeoli on 7/18/17.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {
    public static final String SMS_BUNDLE = "pdus";
    String smsBody;
    String address;
    String dateString;
    long date;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";

            for (int i = 0; i < sms.length; ++i) {

                String format = intentExtras.getString("format");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);


                smsBody = smsMessage.getMessageBody().toString();
                address = smsMessage.getOriginatingAddress();
                date = smsMessage.getTimestampMillis();
                dateString = date + "";

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
            }

            MainActivity inst = MainActivity.getInstance();
            MessagingActivity instance = MessagingActivity.instance();
            if(instance != null)
                instance.updateInbox(smsBody, address, dateString);
            inst.updateInbox(smsBody, address, dateString);
            //instance.updateInbox(smsMessageStr);
        }
    }
}
