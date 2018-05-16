package com.example.razvan.edittextinrecyclerview;

import android.app.Application;

import com.example.razvan.edittextinrecyclerview.injection.component.ApplicationComponent;
import com.example.razvan.edittextinrecyclerview.injection.component.DaggerApplicationComponent;
import com.example.razvan.edittextinrecyclerview.injection.module.ApplicationModule;
import com.example.razvan.edittextinrecyclerview.injection.module.NetworkModule;
import com.example.razvan.edittextinrecyclerview.injection.module.ServiceModule;

public class ExampleApplication extends Application {
    protected ApplicationComponent applicationComponent;

    public ApplicationComponent getComponent() {
        if (applicationComponent == null) {
            applicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .networkModule(new NetworkModule())
                    .serviceModule(new ServiceModule())
                    .build();
        }
        return applicationComponent;
    }
}
