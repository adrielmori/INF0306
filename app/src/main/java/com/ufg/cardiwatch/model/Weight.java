package com.ufg.cardiwatch.model;

import com.google.android.gms.fitness.data.Value;

import java.io.Serializable;

public class Weight implements Serializable {
    private Long day;
    private Float weight;

    public Weight() {
    }

    public Weight(Long day, Float weight) {
        this.day = day;
        this.weight = weight;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "weight{" +
                "day=" + day +
                ", weight=" + weight +
                '}';
    }
}
