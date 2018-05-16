package com.example.razvan.edittextinrecyclerview.retrofit;

import com.example.razvan.edittextinrecyclerview.model.DataModel;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ExampleService {
    @GET("latest")
    Observable<DataModel> getCurrencyRates(@Query("base") String base);
}
