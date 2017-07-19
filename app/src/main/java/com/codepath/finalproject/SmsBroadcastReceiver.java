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
                long date = smsMessage.getTimestampMillis();
                dateString = ListAdapter.millisToDate(date);

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
            }

            MainActivity inst = MainActivity.getInstace();
            MessagingActivity instance = MessagingActivity.instance();
            //instance.hello();
            //instance.updateInbox(smsBody, address, dateString);
            inst.updateInbox(smsMessageStr);
            //instance.updateInbox(smsMessageStr);
        }
    }
}
