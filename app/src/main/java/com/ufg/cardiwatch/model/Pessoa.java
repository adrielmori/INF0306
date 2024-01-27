package com.ufg.cardiwatch.model;

import java.io.Serializable;
import java.util.List;

public class Pessoa implements Serializable {
    List<Activity> activities;
    List<HeartRate> heartRates;
    List<Sleep> sleeps;
    List<Step> steps;
    List<Weight> weights;
    List<Calory> calories;

    public Pessoa() {
    }

    public Pessoa(List<Activity> activities, List<HeartRate> heartRates, List<Sleep> sleeps, List<Step> steps, List<Weight> weights, List<Calory> calories) {
        this.activities = activities;
        this.heartRates = heartRates;
        this.sleeps = sleeps;
        this.steps = steps;
        this.weights = weights;
        this.calories = calories;
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

    public List<Calory> getCalories() {
        return calories;
    }

    public void setCalories(List<Calory> calories) {
        this.calories = calories;
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "activities=" + activities +
                ", heartRates=" + heartRates +
                ", sleeps=" + sleeps +
                ", steps=" + steps +
                ", weights=" + weights +
                ", calories=" + calories +
                '}';
    }
}
