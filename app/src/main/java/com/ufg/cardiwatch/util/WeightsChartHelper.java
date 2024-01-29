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

public class WeightsChartHelper {
    private BarChart barchart_weight;

    public WeightsChartHelper(BarChart barChart) {
        this.barchart_weight = barChart;
        this.barchart_weight.getDescription().setEnabled(false);
    }

    public void plotWeightChart(JSONArray weightArray) {
        ArrayList<BarEntry> weightEntries = parseWeightDataFromJson(weightArray);
        plotBarChart(weightEntries);
    }

    private ArrayList<BarEntry> parseWeightDataFromJson(JSONArray weightArray) {
        ArrayList<BarEntry> weightEntries = new ArrayList<>();
        HashMap<Integer, Float> weightMap = new HashMap<>();

        try {
            // Exclua o primeiro valor
            if (weightArray.length() > 0) {
                weightArray.remove(0);
            }

            for (int i = 0; i < weightArray.length(); i++) {
                JSONObject weightObject = weightArray.getJSONObject(i);

                int day = weightObject.getInt("day");
                float weightLevel = (float) weightObject.getDouble("weight");

                // Verificar se já existe uma entrada para o mesmo dia
                if (weightMap.containsKey(day)) {
                    // Se o novo valor for maior, atualize
                    if (weightLevel > weightMap.get(day)) {
                        weightMap.put(day, weightLevel);
                    }
                } else {
                    // Se não existe uma entrada para o dia, adicione ao mapa
                    weightMap.put(day, weightLevel);
                }
            }

            // Converter o mapa para entradas do gráfico
            for (Map.Entry<Integer, Float> entry : weightMap.entrySet()) {
                int day = entry.getKey();
                Float weightLevel = entry.getValue();
                weightEntries.add(new BarEntry(day, weightLevel));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weightEntries;
    }



    private void plotBarChart(ArrayList<BarEntry> stepsEntries) {
        BarDataSet barDataSet = new BarDataSet(stepsEntries, "Peso");
        BarData barData = new BarData(barDataSet);
        barchart_weight.setData(barData);

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        // Ajuste o tamanho da fonte dos números
        barDataSet.setValueTextSize(12f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte dos valores nas barras (opcional)
        barData.setValueTextSize(12f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte da legenda (opcional)
        Legend legend = barchart_weight.getLegend();
        legend.setTextSize(12f);  // Altere o valor conforme necessário
    }
}
