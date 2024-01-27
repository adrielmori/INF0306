package com.ufg.cardiwatch.model;

public class Calory {
    private String weyDay;
    private Float colorie;

    public Calory() {
    }

    public Calory(String weyDay, Float calore) {
        this.weyDay = weyDay;
        this.colorie = calore;
    }

    public String getWeyDay() {
        return weyDay;
    }

    public void setWeyDay(String weyDay) {
        this.weyDay = weyDay;
    }

    public Float getColorie() {
        return colorie;
    }

    public void setColorie(Float colorie) {
        this.colorie = colorie;
    }

    @Override
    public String toString() {
        return "Calory{" +
                "weyDay='" + weyDay + '\'' +
                ", colorie=" + colorie +
                '}';
    }
}
