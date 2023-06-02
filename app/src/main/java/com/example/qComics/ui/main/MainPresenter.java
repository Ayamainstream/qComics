package com.example.qComics.ui.main;

import android.content.Context;

import com.example.qComics.data.utils.SharedPrefs;
import com.example.qComics.mvp.PresenterBase;


public class MainPresenter extends PresenterBase<MainContract.View> implements MainContract.Presenter {

    Context context;

    public MainPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setNotAuthorized() {
        SharedPrefs.setToken("");
        getView().startLoginActivity();
    }


}
