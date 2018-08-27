package com.movies.app.util;

import android.util.Log;
import com.movies.app.BuildConfig;


public class AppLog {
    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) Log.d(tag, message);
    }

    public static void d(String message) {
        if (BuildConfig.DEBUG) Log.d("debug", message);
    }

    public static void i(String tag, String message) {
        if (BuildConfig.DEBUG) Log.i(tag, message);
    }

    public static void i(String message) {
        if (BuildConfig.DEBUG) Log.i("info", message);
    }

    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG) Log.e(tag, message);
    }

    public static void e(String message) {
        if (BuildConfig.DEBUG) Log.e("error", message);
    }
}
