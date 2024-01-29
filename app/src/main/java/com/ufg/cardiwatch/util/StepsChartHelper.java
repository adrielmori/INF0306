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

public class StepsChartHelper {
    private BarChart barChart_steps;

    public StepsChartHelper(BarChart barChart) {

        this.barChart_steps = barChart;
        this.barChart_steps.getDescription().setEnabled(false);
    }

    public void plotStepsChart(JSONArray stepsArray) {
        ArrayList<BarEntry> stepsEntries = parseStepsDataFromJson(stepsArray);
        plotBarChart(stepsEntries);
    }

    private ArrayList<BarEntry> parseStepsDataFromJson(JSONArray stepsArray) {
        HashMap<Integer, Integer> stepsMap = new HashMap<>();

        try {
            for (int i = 0; i < stepsArray.length(); i++) {
                JSONObject stepObject = stepsArray.getJSONObject(i);

                int day = stepObject.getInt("day");
                int steps = stepObject.getInt("steps");

                // Verificar se já existe uma entrada para o mesmo dia
                if (stepsMap.containsKey(day)) {
                    // Se o novo valor for maior, atualize
                    if (steps > stepsMap.get(day)) {
                        stepsMap.put(day, steps);
                    }
                } else {
                    // Se não existe uma entrada para o dia, adicione ao mapa
                    stepsMap.put(day, steps);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Converter o mapa para entradas do gráfico
        ArrayList<BarEntry> stepsEntries = new ArrayList<>();
        for (HashMap.Entry<Integer, Integer> entry : stepsMap.entrySet()) {
            int day = entry.getKey();
            int steps = entry.getValue();
            stepsEntries.add(new BarEntry(day, steps));
        }

        return stepsEntries;
    }

    private void plotBarChart(ArrayList<BarEntry> stepsEntries) {
        BarDataSet barDataSet = new BarDataSet(stepsEntries, "Count Steps by Day");
        BarData barData = new BarData(barDataSet);
        barChart_steps.setData(barData);

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        // Ajuste o tamanho da fonte dos números
        barDataSet.setValueTextSize(8f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte dos valores nas barras (opcional)
        barData.setValueTextSize(8f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte da legenda (opcional)
        Legend legend = barChart_steps.getLegend();
        legend.setTextSize(8f);  // Altere o valor conforme necessário
    }
}