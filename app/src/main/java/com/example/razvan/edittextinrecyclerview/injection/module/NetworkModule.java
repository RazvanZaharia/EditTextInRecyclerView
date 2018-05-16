package com.example.razvan.edittextinrecyclerview.injection.module;

import com.example.razvan.edittextinrecyclerview.BuildConfig;
import com.example.razvan.edittextinrecyclerview.retrofit.RxErrorHandlingCallAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = {ApplicationModule.class, ServiceModule.class})
public class NetworkModule {

    private static final String ENDPOINT = BuildConfig.ENDPOINT;

    @Provides
    @Singleton
    Gson providesGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    @Provides
    @Singleton
    GsonConverterFactory providesGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }


    @Provides
    @Singleton
    RxJavaCallAdapterFactory providesRxJavaCallAdapterFactory() {
        return RxJavaCallAdapterFactory.create();
    }

    @Provides
    @Singleton
    RxErrorHandlingCallAdapterFactory providesRxErrorHandligCallAdapterFactory() {
        return (RxErrorHandlingCallAdapterFactory) RxErrorHandlingCallAdapterFactory.create();
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(GsonConverterFactory gsonConverterFactory, OkHttpClient okHttpClient,
                              RxErrorHandlingCallAdapterFactory rxErrorHandlingCallAdapterFactory) {

        return new Retrofit.Builder().addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxErrorHandlingCallAdapterFactory)
                .baseUrl(ENDPOINT)
                .client(okHttpClient)
                .build();
    }
}
