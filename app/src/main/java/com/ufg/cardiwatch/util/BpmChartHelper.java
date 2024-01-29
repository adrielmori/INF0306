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
import java.util.HashMap;
import java.util.Map;

public class BpmChartHelper {
    private BarChart barchart_bpm;

    public BpmChartHelper(BarChart barChart) {
        this.barchart_bpm = barChart;
    }

    public void plotBpmChart(JSONArray bpmArray) {
        ArrayList<BarEntry> bpmEntries = parseBpmDataFromJson(bpmArray);
        plotBarChart(bpmEntries);
    }

    private ArrayList<BarEntry> parseBpmDataFromJson(JSONArray bpmArray) {
        ArrayList<BarEntry> bpmEntries = new ArrayList<>();

        try {
            for (int i = 0; i < bpmArray.length(); i++) {
                JSONObject bpmObject = bpmArray.getJSONObject(i);

                int day = bpmObject.getInt("day");
                float bpmLevel = (float) bpmObject.getDouble("bpm");

                bpmEntries.add(new BarEntry(day, bpmLevel));
                Log.d("MonitoryActivity", "Dia: " + day + ", BPM: " + bpmLevel);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bpmEntries;
    }


    private void plotBarChart(ArrayList<BarEntry> stepsEntries) {
        BarDataSet barDataSet = new BarDataSet(stepsEntries, "BPM");
        BarData barData = new BarData(barDataSet);
        barchart_bpm.setData(barData);

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        // Ajuste o tamanho da fonte dos números
        barDataSet.setValueTextSize(12f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte dos valores nas barras (opcional)
        barData.setValueTextSize(12f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte da legenda (opcional)
        Legend legend = barchart_bpm.getLegend();
        legend.setTextSize(12f);  // Altere o valor conforme necessário
    }
}
