package com.ufg.cardiwatch.model;

import com.google.android.gms.fitness.data.Value;

public class Activity {
    private Long day;
    private Value activity;

    public Activity() {
    }

    public Activity(Long day, Value activity) {
        this.day = day;
        this.activity = activity;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public Value getActivity() {
        return activity;
    }

    public void setActivity(Value activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "day=" + day +
                ", activity=" + activity +
                '}';
    }
}
