package com.ufg.cardiwatch.model;

import com.google.android.gms.fitness.data.Value;

public class Weight{
    private Long day;
    private Value weight;

    public Weight() {
    }

    public Weight(Long day, Value weight) {
        this.day = day;
        this.weight = weight;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public Value getWeight() {
        return weight;
    }

    public void setWeight(Value weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Height{" +
                "day=" + day +
                ", height=" + weight +
                '}';
    }
}
