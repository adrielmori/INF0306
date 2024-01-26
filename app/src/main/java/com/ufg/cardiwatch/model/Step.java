package com.ufg.cardiwatch.model;

import com.google.android.gms.fitness.data.Value;

public class Step {
    private Long day;
    private Value steps;

    public Step() {
    }

    public Step(Long day, Value steps) {
        this.day = day;
        this.steps = steps;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public Value getSteps() {
        return steps;
    }

    public void setSteps(Value steps) {
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
