package com.ufg.cardiwatch.model;

import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Value;

import java.io.Serializable;

public class HeartRate implements Serializable {
    private Long day;
    private Float bpm;

    public HeartRate() {
    }

    public HeartRate(Long day, Float bpm) {
        this.day = day;
        this.bpm = bpm;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public Float getBpm() {
        return bpm;
    }

    public void setBpm(Float bpm) {
        this.bpm = bpm;
    }

    @Override
    public String toString() {
        return "HeartRate{" +
                "day=" + day +
                ", bpm=" + bpm +
                '}';
    }
}
