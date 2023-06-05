package com.example.qComics.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.qComics.data.utils.SharedPrefs;
import com.example.qComics.ui.auth.AuthenticationActivity;
import com.example.qComics.ui.main.MainActivity;
import com.example.q_comics.R;

import java.util.Locale;

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
        SharedPreferences prefs = this.getSharedPreferences("DeviceToken", MODE_PRIVATE);
        setLanguage(prefs.getString("language", getString(R.string.russian)));
        finish();
        startActivity(intent);
    }

    private void setLanguage(String language) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        Locale locale;

        if (getString(R.string.kazakh).equals(language)) {
            locale = new Locale("kk"); // Kazakh language code
        } else {
            locale = Locale.getDefault();
        }
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
//    private boolean isAuthorized() {
//        Log.e("ASD", "token = " + SharedPrefs.getToken());
//        return !SharedPrefs.getToken().isEmpty();
//        return false;
//    }
}