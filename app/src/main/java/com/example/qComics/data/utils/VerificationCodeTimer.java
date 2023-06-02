package com.example.qComics.data.utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.q_comics.R;

import java.util.Locale;

public class VerificationCodeTimer extends CountDownTimer {
    private final TextView timer;
    private final TextView countdown;
    private final LinearLayout button;
    private final int textColor;

    public VerificationCodeTimer(long millisInFuture, long countDownInterval, TextView timer, TextView countdown, LinearLayout button, int textColor) {
        super(millisInFuture, countDownInterval);
        this.timer = timer;
        this.countdown = countdown;
        this.button = button;
        this.textColor = textColor;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long seconds = millisUntilFinished / 1000;
        timer.setText(String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60));
    }

    @Override
    public void onFinish() {
        countdown.setText(R.string.resend_code);
        timer.setVisibility(View.GONE);
        button.setEnabled(true);
        countdown.setTextColor(textColor);
    }

    public void startTimer() {
        button.setEnabled(false);
        timer.setVisibility(View.VISIBLE);
        countdown.setText(R.string.send_new_code_after);
        countdown.setTextColor(Color.BLACK);
        start();
    }
}

