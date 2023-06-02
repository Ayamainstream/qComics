package com.example.qComics.data.network;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager{
    private static SharedPreferences.Editor editor;
    private static String USER_TOKEN = "user_token";
    private static String REFRESH_TOKEN = "refresh_token";
    private static SessionManager sInstance;
    private static SharedPreferences pref;

    public SessionManager(Context context) {
        if(pref == null){
            pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        }
    }

    /**
     * Function to save auth token
     */
    public void saveAuthToken(String token) {
        editor = pref.edit();
        editor.putString(USER_TOKEN, token);
        editor.apply();
    }

    public void saveRefreshToken(String token) {
        editor = pref.edit();
        editor.putString(REFRESH_TOKEN, token);
        editor.apply();
    }

    /**
     * Function to fetch auth token
     */
    public String fetchAuthToken(){
        return pref.getString(USER_TOKEN, null);
    }

    public String fetchRefreshToken(){
        return pref.getString(REFRESH_TOKEN, null);
    }

}