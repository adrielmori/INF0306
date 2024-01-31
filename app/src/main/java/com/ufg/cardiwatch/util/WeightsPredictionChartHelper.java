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

public class WeightsPredictionChartHelper {
    private BarChart barchart_weight;

    public WeightsPredictionChartHelper(BarChart barChart) {
        this.barchart_weight = barChart;
        this.barchart_weight.getDescription().setEnabled(false);
    }

    public void plotWeightChart(JSONArray weightArray, JSONArray weightPredictedArray) {
        Log.d("MonitoryActivity", "PREDICAO DEU CERTO: -> " + weightPredictedArray.toString());
        ArrayList<BarEntry> weightEntries = parseWeightDataFromJson(weightArray);
        ArrayList<BarEntry> weightPredictedEntries = parseWeightDataFromJson(weightPredictedArray);

        plotBarChart(weightEntries, weightPredictedEntries);
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



    private void plotBarChart(ArrayList<BarEntry> weightEntries, ArrayList<BarEntry> weightPredictedEntries) {
        BarDataSet weightDataSet = new BarDataSet(weightEntries, "Peso");
        weightDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        weightDataSet.setValueTextColor(Color.BLACK);
        weightDataSet.setValueTextSize(12f);

        BarDataSet weightPredictedDataSet = new BarDataSet(weightPredictedEntries, "Peso Previsto");
        weightPredictedDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        weightPredictedDataSet.setValueTextColor(Color.BLACK);
        weightPredictedDataSet.setValueTextSize(12f);

        BarData barData = new BarData(weightDataSet, weightPredictedDataSet);
        barData.setValueTextSize(12f);

        barchart_weight.setData(barData);
        barchart_weight.invalidate();  // Atualize o gráfico com os novos dados

        Legend legend = barchart_weight.getLegend();
        legend.setTextSize(12f);
    }
}

