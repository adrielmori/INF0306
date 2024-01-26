package com.ufg.cardiwatch.model;

import com.google.android.gms.fitness.data.Value;

import java.io.Serializable;

public class Sleep implements Serializable {
    private Long day;
    private Integer sleep;

    public Sleep() {
    }

    public Sleep(Long day, Integer sleep) {
        this.day = day;
        this.sleep = sleep;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public Integer getSleep() {
        return sleep;
    }

    public void setSleep(Integer sleep) {
        this.sleep = sleep;
    }

    @Override
    public String toString() {
        return "Sleep{" +
                "day=" + day +
                ", start=" + sleep+
                '}';
    }
}
