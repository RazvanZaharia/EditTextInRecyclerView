package com.example.razvan.edittextinrecyclerview.injection.component;

import com.example.razvan.edittextinrecyclerview.injection.PerActivity;
import com.example.razvan.edittextinrecyclerview.injection.module.ActivityModule;
import com.example.razvan.edittextinrecyclerview.main.MainActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity mainActivity);
}
