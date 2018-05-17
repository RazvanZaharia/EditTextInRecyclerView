package com.example.razvan.edittextinrecyclerview.main;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.razvan.edittextinrecyclerview.base.Presenter;
import com.example.razvan.edittextinrecyclerview.model.Rate;
import com.example.razvan.edittextinrecyclerview.retrofit.ExampleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class PresenterMain implements Presenter<MvpViewMain> {
    private static final String TAG = "PresenterMain";

    private ExampleService mService;
    private CompositeSubscription mSubscriptions;
    private HashMap<String, Float> mReferenceRates = new HashMap<>();

    private MvpViewMain viewMain;
    private PublishSubject<Rate> mEditRatePublisher = PublishSubject.create();

    @Inject
    public PresenterMain(ExampleService service) {
        mService = service;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(MvpViewMain mvpView) {
        this.viewMain = mvpView;
    }

    @Override
    public void detachView() {
        viewMain = null;
        if (mSubscriptions.hasSubscriptions()) {
            mSubscriptions.clear();
        }
    }

    public void init() {
        mSubscriptions.add(mService.getCurrencyRates("EUR")
                .compose(applySchedulers())
                .subscribe(response -> {
                    Log.d(TAG, "response: " + response.toString());
                    getView().showData(convertToRates(response.getRates()), mEditRatePublisher);
                }));

        mSubscriptions.add(mEditRatePublisher
                .compose(applyUISchedulers())
                .subscribe(rate -> {

                }));
    }

    private List<Rate> convertToRates(@NonNull HashMap<String, String> ratesMap) {
        List<Rate> rates = new ArrayList<>();

        for (String rateName : ratesMap.keySet()) {
            float rateValue = Float.parseFloat(ratesMap.get(rateName));
            rates.add(new Rate(rateName, rateValue));
            saveReferenceRate(rateName, rateValue);
        }

        return rates;
    }

    private void saveReferenceRate(String rateName, float rateValue) {
        mReferenceRates.put(rateName, rateValue);
    }

    private float getRateValueInBaseCurrency(@NonNull Rate rate) {
        float baseValue;
        float referenceValue = mReferenceRates.get(rate.getName());

        baseValue = rate.getRate() / referenceValue;

        return baseValue;
    }

    private float getRateValueFromBaseCurrency(@NonNull Rate rate, float baseValue) {

        return 0.0f;
    }

    public MvpViewMain getView() {
        return viewMain;
    }

    private <T> Observable.Transformer<T, T> applySchedulers() {
        return (Observable<T> observable) -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private <T> Observable.Transformer<T, T> applyIOSchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    private <T> Observable.Transformer<T, T> applyUISchedulers() {
        return observable -> observable.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
