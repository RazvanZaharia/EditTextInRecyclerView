package com.example.razvan.edittextinrecyclerview.model;

import java.io.Serializable;

public class Rate implements Serializable {
    private static final long serialVersionUID = 5579226264904096727L;

    private String name;
    private float value;

    public Rate(String name, float value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
