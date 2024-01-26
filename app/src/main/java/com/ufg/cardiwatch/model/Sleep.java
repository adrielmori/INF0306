package com.ufg.cardiwatch.model;

import com.google.android.gms.fitness.data.Value;

public class Sleep {
    private Long day;
    private Value sleep;

    public Sleep() {
    }

    public Sleep(Long day, Value sleep) {
        this.day = day;
        this.sleep = sleep;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public Value getSleep() {
        return sleep;
    }

    public void setSleep(Value sleep) {
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
