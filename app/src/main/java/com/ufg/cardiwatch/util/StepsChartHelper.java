package com.ufg.cardiwatch.util;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StepsChartHelper {
    private BarChart barChart;

    public StepsChartHelper(BarChart barChart) {
        this.barChart = barChart;
    }

    public void plotStepsChart(JSONArray stepsArray) {
        ArrayList<BarEntry> stepsEntries = parseStepsDataFromJson(stepsArray);
        plotBarChart(stepsEntries);
    }

    private ArrayList<BarEntry> parseStepsDataFromJson(JSONArray stepsArray) {
        ArrayList<BarEntry> stepsEntries = new ArrayList<>();

        try {
            for (int i = 0; i < stepsArray.length(); i++) {
                JSONObject stepObject = stepsArray.getJSONObject(i);

                int day = stepObject.getInt("day");
                int steps = stepObject.getInt("steps");

                stepsEntries.add(new BarEntry(day, steps));

                Log.d("StepsChartHelper", "Dia: " + day + ", Passos: " + steps);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return stepsEntries;
    }

    private void plotBarChart(ArrayList<BarEntry> stepsEntries) {
        BarDataSet barDataSet = new BarDataSet(stepsEntries, "Número de Passos por Dia");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(18f);
    }
}
