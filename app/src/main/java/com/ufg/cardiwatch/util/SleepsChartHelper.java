package com.ufg.cardiwatch.util;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SleepsChartHelper {
    private BarChart barchart_sleeps;

    public SleepsChartHelper(BarChart barChart) {
        this.barchart_sleeps = barChart;
    }

    public void plotSleepsChart(JSONArray sleepsArray) {
        ArrayList<BarEntry> sleepsEntries = parseSleepsDataFromJson(sleepsArray);
        plotBarChart(sleepsEntries);
    }

    private ArrayList<BarEntry> parseSleepsDataFromJson(JSONArray sleepsArray) {
        ArrayList<BarEntry> sleepsEntries = new ArrayList<>();

        try {
            for (int i = 0; i < sleepsArray.length(); i++) {
                JSONObject stepObject = sleepsArray.getJSONObject(i);

                int day = stepObject.getInt("day");
                int sleeps = stepObject.getInt("sleep");

                sleepsEntries.add(new BarEntry(day, sleeps));

                Log.d("MonitoryActivity", "Dia: " + day + ", Passos: " + sleeps);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sleepsEntries;
    }

    private void plotBarChart(ArrayList<BarEntry> sleepsEntries) {
        BarDataSet barDataSet = new BarDataSet(sleepsEntries, "Quality of Sleep Score");
        BarData barData = new BarData(barDataSet);
        barchart_sleeps.setData(barData);

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        // Ajuste o tamanho da fonte dos números
        barDataSet.setValueTextSize(12f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte dos valores nas barras (opcional)
        barData.setValueTextSize(12f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte da legenda (opcional)
        Legend legend = barchart_sleeps.getLegend();
        legend.setTextSize(12f);  // Altere o valor conforme necessário
    }
}
