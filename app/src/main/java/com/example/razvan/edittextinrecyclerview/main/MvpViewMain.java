package com.example.razvan.edittextinrecyclerview.main;

import android.support.annotation.NonNull;

import com.example.razvan.edittextinrecyclerview.base.MvpView;
import com.example.razvan.edittextinrecyclerview.model.Rate;

import java.util.List;

public interface MvpViewMain extends MvpView {
    void showData(@NonNull List<Rate> rates);
}
