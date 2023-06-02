package com.example.qComics.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.qComics.data.utils.SharedPrefs;
import com.example.qComics.ui.auth.AuthenticationActivity;
import com.example.qComics.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        Intent intent;
//        if (isAuthorized()) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
//        } else {
//            intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
//        }
        finish();
        startActivity(intent);

    }

//    private boolean isAuthorized() {
//        Log.e("ASD", "token = " + SharedPrefs.getToken());
//        return !SharedPrefs.getToken().isEmpty();
//        return false;
//    }
}