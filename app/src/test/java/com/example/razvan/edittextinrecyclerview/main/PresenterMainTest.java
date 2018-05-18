package com.example.razvan.edittextinrecyclerview.main;

import android.util.Log;

import com.example.razvan.edittextinrecyclerview.model.DataModel;
import com.example.razvan.edittextinrecyclerview.model.Rate;
import com.example.razvan.edittextinrecyclerview.retrofit.ExampleService;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.subjects.PublishSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class PresenterMainTest {

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Mock
    ExampleService mExampleService;

    @Mock
    MvpViewMain mViewMain;

    private PresenterMain mPresenterMain;
    private PublishSubject<DataModel> mApiRequestObservable = PublishSubject.create();
    private PublishSubject<DataModel> mApiRepeatedRequestObservable = PublishSubject.create();
    private PublishSubject<Rate> mEditRatePublisher = PublishSubject.create();
    private PublishSubject<Float> mBaseValueChanges = PublishSubject.create();

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Log.class);

        when(mExampleService.getCurrencyRates(anyString())).thenReturn(mApiRequestObservable);

        mPresenterMain = new PresenterMain(mExampleService);
        mPresenterMain.attachView(mViewMain);
        mPresenterMain.init();

        // this will be returned to do the repeated request
        when(mExampleService.getCurrencyRates(anyString())).thenReturn(mApiRepeatedRequestObservable);

        mEditRatePublisher = mPresenterMain.getEditRatePublisher();
        mBaseValueChanges = mPresenterMain.getBaseValueChanges();
    }

    @Test
    public void test_fetchRates() {
        mApiRequestObservable.onNext(getMockData());
        //called by postponed request when response for first one is received
        verify(mExampleService, times(2)).getCurrencyRates(eq("EUR"));
        verify(mViewMain).showData(anyListOf(Rate.class),
                eq(mEditRatePublisher),
                eq(mBaseValueChanges),
                any());

        verify(mViewMain).moveBaseCurrencyToTop(eq("EUR"));
    }

    @Test
    public void test_editRate() {
        Rate editedRate = new Rate("BGN", 10);
        mPresenterMain.onNewBaseCurrency("BGN", 10);
        mEditRatePublisher.onNext(editedRate);
        mApiRepeatedRequestObservable.onNext(getMockData());

        verify(mViewMain).moveBaseCurrencyToTop(eq("BGN"));
        verify(mExampleService).getCurrencyRates(eq("BGN"));
    }

    private String mResponse = "{\n" +
            "\"base\": \"EUR\",\n" +
            "\"date\": \"2018-05-16\",\n" +
            "\"rates\": {\n" +
            "\"AUD\": 1.5711,\n" +
            "\"BGN\": 1.9521,\n" +
            "\"BRL\": 4.3211,\n" +
            "\"CAD\": 1.5132,\n" +
            "\"CHF\": 1.1769,\n" +
            "\"CNY\": 7.4993,\n" +
            "\"CZK\": 25.496,\n" +
            "\"DKK\": 7.4344,\n" +
            "\"GBP\": 0.87251,\n" +
            "\"HKD\": 9.2327,\n" +
            "\"HRK\": 7.3689,\n" +
            "\"HUF\": 316.66,\n" +
            "\"IDR\": 16550,\n" +
            "\"ILS\": 4.227,\n" +
            "\"INR\": 79.74,\n" +
            "\"ISK\": 122.76,\n" +
            "\"JPY\": 129.58,\n" +
            "\"KRW\": 1272.1,\n" +
            "\"MXN\": 23.24,\n" +
            "\"MYR\": 4.6623,\n" +
            "\"NOK\": 9.5532,\n" +
            "\"NZD\": 1.7082,\n" +
            "\"PHP\": 61.597,\n" +
            "\"PLN\": 4.2807,\n" +
            "\"RON\": 4.6261,\n" +
            "\"RUB\": 73.457,\n" +
            "\"SEK\": 10.271,\n" +
            "\"SGD\": 1.5794,\n" +
            "\"THB\": 37.802,\n" +
            "\"TRY\": 5.2078,\n" +
            "\"USD\": 1.1761,\n" +
            "\"ZAR\": 14.73\n" +
            "}\n" +
            "}";

    private DataModel getMockData() {
        return new Gson().fromJson(mResponse, DataModel.class);
    }
}
