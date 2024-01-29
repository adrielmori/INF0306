package com.ufg.cardiwatch.util;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.charts.LineChart;

public class SleepsChartHelper {
    private LineChart linechart_sleeps;

    public SleepsChartHelper(LineChart lineChart) {
        this.linechart_sleeps = lineChart;
    }

    public void plotSleepsChart(JSONArray sleepsArray) {
        ArrayList<Entry> sleepsEntries = parseSleepsDataFromJson(sleepsArray);
        plotLineChart(sleepsEntries);
    }

    private ArrayList<Entry> parseSleepsDataFromJson(JSONArray sleepsArray) {
        ArrayList<Entry> sleepsEntries = new ArrayList<>();
        int hour = 0;

        try {
            for (int i = 0; i < sleepsArray.length(); i++) {
                JSONObject stepObject = sleepsArray.getJSONObject(i);

                int day = stepObject.getInt("day");
                int sleeps = stepObject.getInt("sleep");

                // Adicione horas ao dia
                float dayInHours = day * 24f + hour;
                sleepsEntries.add(new Entry(dayInHours, sleeps));

                Log.d("MonitoryActivity", "Hora: " + dayInHours + ", Sleep Score: " + sleeps);

                // Atualize a hora
                hour = (hour + 1) % 24;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sleepsEntries;
    }

    private void plotLineChart(ArrayList<Entry> sleepsEntries) {
        LineDataSet lineDataSet = new LineDataSet(sleepsEntries, "Quality of Sleep Score");
        LineData lineData = new LineData(lineDataSet);
        linechart_sleeps.setData(lineData);

        lineDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        // Ajuste o tamanho da fonte dos números
        lineDataSet.setValueTextSize(12f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte dos valores nas linhas (opcional)
        lineData.setValueTextSize(12f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte da legenda (opcional)
        Legend legend = linechart_sleeps.getLegend();
        legend.setTextSize(12f);  // Altere o valor conforme necessário
    }
}
