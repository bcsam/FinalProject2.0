package com.codepath.finalproject;

/**
 * Created by andreadeoli on 7/19/17.
 */

import android.content.Context;

public class ContextHolder {
    public static Context context = null;

    public static void setContext(Context context){
        ContextHolder.context = context;
    }

    public static Context getContext(){
        return context;
    }
}
