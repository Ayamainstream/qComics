package com.example.qComics.mvp;

public interface MvpPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();

    void destroy();


}
