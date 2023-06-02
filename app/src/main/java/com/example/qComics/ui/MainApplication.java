package com.example.qComics.ui;

import android.app.Application;
import android.content.Context;

import com.example.qComics.data.utils.SharedPrefs;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        SharedPrefs.setSharedPrefInstance(base);
    }
}
