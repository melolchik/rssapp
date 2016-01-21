package ru.android.rssapp;

import android.util.Log;

/**
 * Created by olga.melehina on 12.09.2015.
 */
 public class AppLogger {
    public static String TAG = "RSS_APP";
    public  static void log(String text){

        String time = "" + System.currentTimeMillis();
        Log.d(TAG,time + " " + text);
    }


}
