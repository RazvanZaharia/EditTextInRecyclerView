package com.example.razvan.edittextinrecyclerview.main;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.razvan.edittextinrecyclerview.adapter.RvAdapterRates;
import com.example.razvan.edittextinrecyclerview.base.Presenter;
import com.example.razvan.edittextinrecyclerview.model.Rate;
import com.example.razvan.edittextinrecyclerview.retrofit.ExampleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class PresenterMain implements Presenter<MvpViewMain>,
        RvAdapterRates.OnRateListener,
        RvAdapterRates.OnBaseCurrencyChangesListener {
    private static final String TAG = "PresenterMain";

    private ExampleService mService;
    private CompositeSubscription mSubscriptions;
    private HashMap<String, Float> mReferenceRates = new HashMap<>();

    private MvpViewMain viewMain;
    private PublishSubject<Rate> mEditRatePublisher = PublishSubject.create();
    private PublishSubject<Float> mBaseValueChanges = PublishSubject.create();
    private PublishSubject<Void> mOnRateValuesChange = PublishSubject.create();
    private Rate mBaseRate;
    private Subscription mUpdateRatesSubscription;

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
        mBaseRate = new Rate("EUR", 1.0f);

        mSubscriptions.add(mEditRatePublisher
                .compose(applyUISchedulers())
                .subscribe(rate -> {
                    mBaseRate.setValue(getBaseValueFromRate(rate));
                    mBaseValueChanges.onNext(mBaseRate.getValue());
                }));

        mSubscriptions.add(mService.getCurrencyRates(mBaseRate.getName())
                .compose(applySchedulers())
                .subscribe(response -> {
                            Log.d(TAG, "response: " + response.toString());
                            updateReferenceRatesList(response.getRates(), mBaseRate);
                            getView().showData(convertToRates(mReferenceRates), mEditRatePublisher, mBaseValueChanges, mOnRateValuesChange);
                            moveBaseCurrencyOnFirstPosition();
                            postponeRatesUpdates();
                        },
                        throwable -> {
                            Log.e(TAG, "request: ", throwable);
                        }));
    }

    private void postponeRatesUpdates() {
        if (mUpdateRatesSubscription != null) {
            mUpdateRatesSubscription.unsubscribe();
        }
        mUpdateRatesSubscription = mService.getCurrencyRates(mBaseRate.getName())
                .repeatWhen(completed -> completed.delay(1, TimeUnit.SECONDS))
                .compose(applySchedulers())
                .subscribe(response -> {
                            Log.d(TAG, "update response: " + response.toString());
                            updateReferenceRatesList(response.getRates(), mBaseRate);
                            mOnRateValuesChange.onNext(null);
                        },
                        throwable -> {
                            Log.e(TAG, "update request: ", throwable);
                        });

        mSubscriptions.add(mUpdateRatesSubscription);
    }

    private List<Rate> convertToRates(@NonNull HashMap<String, Float> ratesMap) {
        List<Rate> rates = new ArrayList<>();

        for (String rateName : ratesMap.keySet()) {
            float rateValue = ratesMap.get(rateName);
            rates.add(new Rate(rateName, rateValue));
        }

        return rates;
    }

    private void updateReferenceRatesList(@NonNull HashMap<String, String> ratesMap, Rate baseRate) {
        mReferenceRates.put(baseRate.getName(), baseRate.getValue());
        for (String rateName : ratesMap.keySet()) {
            float rateValue = Float.parseFloat(ratesMap.get(rateName));
            mReferenceRates.put(rateName, rateValue);
        }
    }

    private float getBaseValueFromRate(@NonNull Rate rate) {
        if (mBaseRate.getName().equals(rate.getName())) {
            return rate.getValue();
        } else {
            float referenceValue = mReferenceRates.get(rate.getName());
            return rate.getValue() / referenceValue;
        }
    }

    private float getRateValueFromBase(@NonNull String rateName) {
        if (mBaseRate.getName().equals(rateName)) {
            return mBaseRate.getValue();
        } else {
            float referenceValue = mReferenceRates.get(rateName);
            return mBaseRate.getValue() * referenceValue;
        }
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

    @Override
    public float getValueForRate(String rateName) {
        float rateValue = getRateValueFromBase(rateName);
        Log.d(TAG, "getValueForRate: " + rateName + " rateValue: " + rateValue + " \nBaseRate: " + mBaseRate.getName() + " BaseValue: " + mBaseRate.getValue());
        return rateValue;
    }

    @Override
    public void onNewBaseCurrency(String rateName, float currentRateValue) {
        Log.d(TAG, "onNewBaseCurrency: " + rateName + " Value: " + currentRateValue);
        mBaseRate = new Rate(rateName, currentRateValue);
        postponeRatesUpdates();
        moveBaseCurrencyOnFirstPosition();
    }

    private void moveBaseCurrencyOnFirstPosition() {
        getView().moveBaseCurrencyToTop(mBaseRate.getName());
    }
}
