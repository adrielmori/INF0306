package com.ufg.cardiwatch.model;

import android.content.Intent;

import com.google.android.gms.fitness.data.Value;

import java.io.Serializable;

public class Activity implements Serializable {
    private Long day;
    private Integer activity;

    public Activity() {
    }

    public Activity(Long day, Integer activity) {
        this.day = day;
        this.activity = activity;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public Integer getActivity() {
        return activity;
    }

    public void setActivity(Integer activity) {
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
