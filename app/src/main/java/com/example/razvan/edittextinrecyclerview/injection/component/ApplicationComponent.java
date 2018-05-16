package com.example.razvan.edittextinrecyclerview.injection.component;

import android.app.Application;
import android.content.Context;

import com.example.razvan.edittextinrecyclerview.ExampleApplication;
import com.example.razvan.edittextinrecyclerview.injection.ApplicationContext;
import com.example.razvan.edittextinrecyclerview.injection.module.ApplicationModule;
import com.example.razvan.edittextinrecyclerview.injection.module.NetworkModule;
import com.example.razvan.edittextinrecyclerview.injection.module.ServiceModule;
import com.example.razvan.edittextinrecyclerview.retrofit.ExampleService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        NetworkModule.class,
        ServiceModule.class
})
public interface ApplicationComponent {

    void inject(ExampleApplication exampleApplication);

    @ApplicationContext
    Context context();

    Application application();

    ExampleService retrofitService();
}
