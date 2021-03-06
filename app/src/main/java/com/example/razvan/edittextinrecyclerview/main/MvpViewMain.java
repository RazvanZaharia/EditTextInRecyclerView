package com.example.razvan.edittextinrecyclerview.main;

import android.support.annotation.NonNull;

import com.example.razvan.edittextinrecyclerview.base.MvpView;
import com.example.razvan.edittextinrecyclerview.model.Rate;

import java.util.List;

import rx.subjects.PublishSubject;

public interface MvpViewMain extends MvpView {
    void showData(@NonNull List<Rate> rates,
                  @NonNull PublishSubject<Rate> editRatePublisher,
                  @NonNull PublishSubject<Float> baseValueChangesPublisher,
                  @NonNull PublishSubject<Void> ratesValuesChange);

    void moveBaseCurrencyToTop(@NonNull String baseCurrencyName);
}
