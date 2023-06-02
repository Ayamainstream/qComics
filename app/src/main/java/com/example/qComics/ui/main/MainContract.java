package com.example.qComics.ui.main;

import com.example.qComics.mvp.MvpPresenter;
import com.example.qComics.mvp.MvpView;

public class MainContract {

    interface View extends MvpView {
        void startLoginActivity();
    }

    interface Presenter extends MvpPresenter<View> {
        void setNotAuthorized();
    }

}
