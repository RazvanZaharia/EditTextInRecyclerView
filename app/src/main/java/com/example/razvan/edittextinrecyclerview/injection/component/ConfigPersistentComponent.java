package com.example.razvan.edittextinrecyclerview.injection.component;


import com.example.razvan.edittextinrecyclerview.injection.ConfigPersistent;
import com.example.razvan.edittextinrecyclerview.injection.module.ActivityModule;

import dagger.Component;

@ConfigPersistent
@Component(dependencies = ApplicationComponent.class)
public interface ConfigPersistentComponent {

    ActivityComponent activityComponent(ActivityModule activityModule);

}