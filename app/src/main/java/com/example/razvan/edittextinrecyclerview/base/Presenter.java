package com.example.razvan.edittextinrecyclerview.base;

public interface Presenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}