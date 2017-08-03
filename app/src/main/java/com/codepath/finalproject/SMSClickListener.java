package com.codepath.finalproject;

import android.widget.TextView;

/**
 * Created by bcsam on 8/2/17.
 */

public interface SMSClickListener {
    void onSMSClick(int pos, SMS smsItem, TextView sharedTextView);
}
