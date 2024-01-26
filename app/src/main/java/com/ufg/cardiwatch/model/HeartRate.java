package com.ufg.cardiwatch.model;

import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Value;

public class HeartRate {
    private Long day;
    private Value bpm;

    public HeartRate() {
    }

    public HeartRate(Long day, Value bpm) {
        this.day = day;
        this.bpm = bpm;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public Value getBpm() {
        return bpm;
    }

    public void setBpm(Value bpm) {
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
