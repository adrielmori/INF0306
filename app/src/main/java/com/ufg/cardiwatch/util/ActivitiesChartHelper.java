package com.ufg.cardiwatch.util;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.HorizontalBarChart;
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
    private HorizontalBarChart horizontalBarChart_act;

    public ActivitiesChartHelper(HorizontalBarChart horizontalBarChart) {
        this.horizontalBarChart_act = horizontalBarChart;
        this.horizontalBarChart_act.getDescription().setEnabled(false);
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

    private void plotBarChart(ArrayList<BarEntry> activitiesEntries) {
        BarDataSet barDataSet = new BarDataSet(activitiesEntries, "Activiteis Goal (0-bad --- 108-good)");
        BarData barData = new BarData(barDataSet);
        horizontalBarChart_act.setData(barData);

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        // Ajuste o tamanho da fonte dos números
        barDataSet.setValueTextSize(8f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte dos valores nas barras (opcional)
        barData.setValueTextSize(8f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte da legenda (opcional)
        Legend legend = horizontalBarChart_act.getLegend();
        legend.setTextSize(8f);  // Altere o valor conforme necessário

        // Desativar a exibição de valores dentro das barras
        barDataSet.setDrawValues(false);
    }
}
