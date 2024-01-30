package com.ufg.cardiwatch.model;

import java.io.Serializable;

public class WeekHorizon implements Serializable {
    private Integer n_weeks;

    public WeekHorizon() {
    }

    public WeekHorizon(Integer n_weeks) {
        this.n_weeks = n_weeks;
    }

    public Integer getN_weeks() {
        return n_weeks;
    }

    public void setN_weeks(Integer n_weeks) {
        this.n_weeks = n_weeks;
    }
}
