package com.movies.app;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    public static Context getContext() {
        return mInstance.getApplicationContext();
    }

    private static App mInstance;

    public static synchronized App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}

