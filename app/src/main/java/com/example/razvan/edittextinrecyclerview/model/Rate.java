package com.example.razvan.edittextinrecyclerview.model;

import java.io.Serializable;

public class Rate implements Serializable {
    private static final long serialVersionUID = 5579226264904096727L;

    private String name;
    private float rate;

    public Rate(String name, float rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
