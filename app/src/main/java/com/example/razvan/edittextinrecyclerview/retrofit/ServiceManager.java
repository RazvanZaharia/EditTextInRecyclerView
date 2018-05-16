package com.example.razvan.edittextinrecyclerview.retrofit;

import android.support.annotation.NonNull;

import com.example.razvan.edittextinrecyclerview.BuildConfig;
import com.google.gson.GsonBuilder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ServiceManager {

    private static final String ENDPOINT = BuildConfig.ENDPOINT;

    private ServiceManager() {
    }

    public static ExampleService getService(Class<ExampleService> classType) {
        return getRetrofit(ENDPOINT, null).create(classType);
    }

    public static ExampleService getService(Class<ExampleService> classType, Interceptor interceptor) {
        return getRetrofit(ENDPOINT, interceptor).create(classType);
    }

    private static RxJavaCallAdapterFactory providesRxJavaCallAdapterFactory() {
        return RxJavaCallAdapterFactory.create();
    }

    private static Retrofit getRetrofit(@NonNull String endpoint,
                                        Interceptor interceptor) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .addCallAdapterFactory(providesRxJavaCallAdapterFactory())
                .baseUrl(endpoint)
                .client(getOkHttpClient(interceptor));
        builder.addConverterFactory(GsonConverterFactory.create(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()));
        return builder.build();
    }

    private static OkHttpClient getOkHttpClient(Interceptor interceptor) {
        OkHttpClient.Builder builder;
        builder = new OkHttpClient.Builder();
        if (interceptor != null) {
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }
}
