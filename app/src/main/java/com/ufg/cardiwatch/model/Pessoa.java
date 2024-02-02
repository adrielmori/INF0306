package com.ufg.cardiwatch.model;

import java.io.Serializable;
import java.util.List;

public class Pessoa implements Serializable {
    private List<Activity> activities;
    private List<HeartRate> heartRates;
    private List<Sleep> sleeps;
    private List<Step> steps;
    private List<Weight> weights;
    private List<Calory> calories;
    private List<WeekHorizon> week_horizon;
    private List<Weight> weights_predict;
    private Weight weightBalance;

    public Pessoa() {
        List<Calory> calories = List.of(
                new Calory("Sunday", 0f),
                new Calory("Monday", 0f),
                new Calory("Tuesday", 0f),
                new Calory("Wednesday", 0f),
                new Calory("Thursday", 0f),
                new Calory("Friday", 0f),
                new Calory("Saturday", 0f)
        );

        this.calories = calories;
    }

    public Pessoa(List<Activity> activities, List<HeartRate> heartRates, List<Sleep> sleeps, List<Step> steps, List<Weight> weights, List<Calory> calories,  List<WeekHorizon> week_horizon, List<Weight> weights_predict, Weight weightBalance) {
        this.activities = activities;
        this.heartRates = heartRates;
        this.sleeps = sleeps;
        this.steps = steps;
        this.weights = weights;
        this.calories = calories;
        this.week_horizon = week_horizon;
        this.weights_predict = weights_predict;
        this.weightBalance = weightBalance;
    }

    public Weight getWeightBalance() {
        return weightBalance;
    }

    public void setWeightBalance(Weight weightBalance) {
        this.weightBalance = weightBalance;
    }

    public List<Weight> getWeights_predict() {
        return weights_predict;
    }

    public void setWeights_predict(List<Weight> weights_predict) {
        this.weights_predict = weights_predict;
    }

    public List<WeekHorizon> getWeek_horizon() {
        return week_horizon;
    }

    public void setWeek_horizon(List<WeekHorizon> week_horizon) {
        this.week_horizon = week_horizon;
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
                ", week_horizon=" + week_horizon +
                '}';
    }
}
