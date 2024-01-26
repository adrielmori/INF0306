package com.ufg.cardiwatch.model;

import com.google.android.gms.fitness.data.Value;

import java.io.Serializable;

public class Step implements Serializable {
    private Long day;
    private Integer steps;

    public Step() {
    }

    public Step(Long day, Integer steps) {
        this.day = day;
        this.steps = steps;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "Step{" +
                "day=" + day +
                ", steps=" + steps +
                '}';
    }
}
