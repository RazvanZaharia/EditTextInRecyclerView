package com.example.razvan.edittextinrecyclerview.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.razvan.edittextinrecyclerview.ExampleApplication;
import com.example.razvan.edittextinrecyclerview.R;
import com.example.razvan.edittextinrecyclerview.adapter.RvAdapterRates;
import com.example.razvan.edittextinrecyclerview.injection.component.ActivityComponent;
import com.example.razvan.edittextinrecyclerview.injection.component.ConfigPersistentComponent;
import com.example.razvan.edittextinrecyclerview.injection.component.DaggerConfigPersistentComponent;
import com.example.razvan.edittextinrecyclerview.injection.module.ActivityModule;
import com.example.razvan.edittextinrecyclerview.model.Rate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity implements MvpViewMain {

    private static final String KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID";
    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    private static final Map<Long, ConfigPersistentComponent> sComponentsMap = new HashMap<>();

    private long activityId;
    private ActivityComponent activityComponent;

    @Inject
    PresenterMain mPresenter;

    private RvAdapterRates mRvAdapterRates;
    @BindView(R.id.rv_rates)
    RecyclerView mRvRates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDependencies(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupRecyclerView();
        init();
    }

    private void init() {
        mPresenter.attachView(this);
        mPresenter.init();
    }

    // Setup

    private void setupRecyclerView() {
        mRvAdapterRates = new RvAdapterRates(this, mPresenter, mPresenter);
        mRvRates.setLayoutManager(new LinearLayoutManager(this));
        mRvRates.setAdapter(mRvAdapterRates);
    }

    private void setupDependencies(Bundle savedInstanceState) {
        activityId = savedInstanceState != null ? savedInstanceState.getLong(KEY_ACTIVITY_ID) : NEXT_ID.getAndIncrement();
        ConfigPersistentComponent configPersistentComponent;
        if (!sComponentsMap.containsKey(activityId)) {
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .applicationComponent(((ExampleApplication) getApplicationContext()).getComponent())
                    .build();
            sComponentsMap.put(activityId, configPersistentComponent);
        } else {
            configPersistentComponent = sComponentsMap.get(activityId);
        }
        activityComponent = configPersistentComponent.activityComponent(new ActivityModule(this));
        activityComponent.inject(this);
    }

    // MvpView
    @Override
    public void showData(@NonNull List<Rate> rates,
                         @NonNull PublishSubject<Rate> editRatePublisher,
                         @NonNull PublishSubject<Float> baseValueChangesPublisher,
                         @NonNull PublishSubject<Void> ratesValuesChange) {
        mRvAdapterRates.setEditRatePublisher(editRatePublisher);
        mRvAdapterRates.setBaseValuePublisher(baseValueChangesPublisher);
        mRvAdapterRates.setRatesValuesChange(ratesValuesChange);
        mRvAdapterRates.setDataSet(rates);
        mRvAdapterRates.notifyDataSetChanged();
    }

    @Override
    public void notifyRatesValuesChanged() {
        mRvAdapterRates.notifyDataSetChanged();
    }

    @Override
    public void moveBaseCurrencyToTop(@NonNull String baseCurrencyName) {
        int baseCurrencyPosition = getCurrencyPositionInList(baseCurrencyName, mRvAdapterRates.getDataSet());
        if (baseCurrencyPosition > -1) {
            Rate baseRate = mRvAdapterRates.getDataSet().remove(baseCurrencyPosition);
            mRvAdapterRates.getDataSet().add(0, baseRate);
            mRvAdapterRates.notifyItemMoved(baseCurrencyPosition, 0);
        }
    }

    private int getCurrencyPositionInList(@NonNull String baseCurrencyName, @NonNull List<Rate> currencyRates) {
        for (int i = 0; i < currencyRates.size(); i++) {
            if (currencyRates.get(i).getName().equals(baseCurrencyName)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }
}
