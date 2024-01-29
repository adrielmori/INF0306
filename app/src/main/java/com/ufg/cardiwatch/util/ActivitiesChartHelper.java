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

public class ActivitiesChartHelper {
    private BarChart barChart_act;

    public ActivitiesChartHelper(BarChart barChart) {
        this.barChart_act = barChart;
    }

    public void plotActivityChart(JSONArray activitiesArray) {
        ArrayList<BarEntry> activitiesEntries = parseActivityDataFromJson(activitiesArray);
        plotBarChart(activitiesEntries);
    }

    private ArrayList<BarEntry> parseActivityDataFromJson(JSONArray activitiesArray) {
        ArrayList<BarEntry> activitiesEntries = new ArrayList<>();
        HashMap<Integer, Integer> activityMap = new HashMap<>();

        try {
            for (int i = 0; i < activitiesArray.length(); i++) {
                JSONObject activityObject = activitiesArray.getJSONObject(i);

                int day = activityObject.getInt("day");
                int activityLevel = activityObject.getInt("activity");

                // Verificar se já existe uma entrada para o mesmo dia
                if (activityMap.containsKey(day)) {
                    // Se o novo valor for maior, atualize
                    if (activityLevel > activityMap.get(day)) {
                        activityMap.put(day, activityLevel);
                    }
                } else {
                    // Se não existe uma entrada para o dia, adicione ao mapa
                    activityMap.put(day, activityLevel);
                }

                Log.d("MonitoryActivity", "Dia: " + day + ", Nível de Atividade: " + activityLevel);
            }

            // Converter o mapa para entradas do gráfico
            for (Map.Entry<Integer, Integer> entry : activityMap.entrySet()) {
                int day = entry.getKey();
                int activityLevel = entry.getValue();
                activitiesEntries.add(new BarEntry(day, activityLevel));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return activitiesEntries;
    }


    private void plotBarChart(ArrayList<BarEntry> stepsEntries) {
        BarDataSet barDataSet = new BarDataSet(stepsEntries, "Meta de Atividade");
        BarData barData = new BarData(barDataSet);
        barChart_act.setData(barData);

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        // Ajuste o tamanho da fonte dos números
        barDataSet.setValueTextSize(12f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte dos valores nas barras (opcional)
        barData.setValueTextSize(12f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte da legenda (opcional)
        Legend legend = barChart_act.getLegend();
        legend.setTextSize(12f);  // Altere o valor conforme necessário
    }
}
