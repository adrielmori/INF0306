package com.ufg.cardiwatch.model;

import java.util.List;

public class Pessoa {
    List<Activity> activities;
    List<HeartRate> heartRates;
    List<Sleep> sleeps;
    List<Step> steps;
    List<Weight> weights;

    public Pessoa() {
    }

    public Pessoa(List<Activity> activities, List<HeartRate> heartRates, List<Sleep> sleeps, List<Step> steps, List<Weight> weights) {
        this.activities = activities;
        this.heartRates = heartRates;
        this.sleeps = sleeps;
        this.steps = steps;
        this.weights = weights;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public List<HeartRate> getHeartRates() {
        return heartRates;
    }

    public void setHeartRates(List<HeartRate> heartRates) {
        this.heartRates = heartRates;
    }

    public List<Sleep> getSleeps() {
        return sleeps;
    }

    public void setSleeps(List<Sleep> sleeps) {
        this.sleeps = sleeps;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Weight> getWeights() {
        return weights;
    }

    public void setWeights(List<Weight> weights) {
        this.weights = weights;
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "activities=" + activities +
                ", heartRates=" + heartRates +
                ", sleeps=" + sleeps +
                ", steps=" + steps +
                ", weights=" + weights +
                '}';
    }
}
