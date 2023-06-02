package com.example.qComics.data.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private static final String PREF_GROUP_CHECKER = "com.example.qComics";
    private static final String PREF_USER_TOKEN = "com.example.qComics.user.token";

    private static SharedPreferences INSTANCE;

    public static void setSharedPrefInstance(Context context) {
        if (INSTANCE != null) {
            return;
        }
        INSTANCE = context.getSharedPreferences(PREF_GROUP_CHECKER, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getPrefGroupGeneric() {
        return INSTANCE;
    }

    public static void setToken(String token) {
        SharedPreferences.Editor editor = getPrefGroupGeneric().edit();
        editor.putString(PREF_USER_TOKEN, token);
        editor.apply();
    }

    public static String getToken() {
        return getPrefGroupGeneric().getString(PREF_USER_TOKEN, "");
    }

}
