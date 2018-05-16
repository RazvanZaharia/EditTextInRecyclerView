package com.example.razvan.edittextinrecyclerview.injection.module;

import com.example.razvan.edittextinrecyclerview.retrofit.ExampleService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by filipradon on 15/07/16.
 */
@Module(includes = ApplicationModule.class)
public class ServiceModule {

    @Singleton
    @Provides
    ExampleService providesRetrofitService(Retrofit retrofit) {
        return retrofit.create(ExampleService.class);
    }

}
