package com.ufg.cardiwatch.util;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeightsChartHelper {
    private LineChart lineChart_weight;

    public WeightsChartHelper(LineChart lineChart) {
        this.lineChart_weight = lineChart;
        // Desabilitar a descrição do gráfico
        lineChart_weight.getDescription().setEnabled(false);

        // Configurações adicionais para um LineChart
        lineChart_weight.setDrawGridBackground(false);
        lineChart_weight.setDrawBorders(false);

        // Configuração do eixo X
        XAxis xAxis = lineChart_weight.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // Configuração do eixo Y
        YAxis leftAxis = lineChart_weight.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = lineChart_weight.getAxisRight();
        rightAxis.setEnabled(false);
    }

    public void plotWeightChart(JSONArray weightArray) {
        ArrayList<Entry> weightEntries = parseWeightDataFromJson(weightArray);
        plotLineChart(weightEntries);
    }

    private ArrayList<Entry> parseWeightDataFromJson(JSONArray weightArray) {
        HashMap<Integer, ArrayList<Float>> weightMap = new HashMap<>();

        try {
            // Preencher o mapa com os valores de peso para cada dia
            for (int i = 0; i < weightArray.length(); i++) {
                JSONObject weightObject = weightArray.getJSONObject(i);

                int day = weightObject.getInt("day");
                float weightLevel = (float) weightObject.getDouble("weight");

                if (weightMap.containsKey(day)) {
                    weightMap.get(day).add(weightLevel);
                } else {
                    ArrayList<Float> weightList = new ArrayList<>();
                    weightList.add(weightLevel);
                    weightMap.put(day, weightList);
                }

                Log.d("MonitoryActivity", "Dia: " + day + ", Peso: " + weightLevel);
            }

            // Calcular a média para cada dia
            ArrayList<Entry> weightEntries = new ArrayList<>();
            for (Map.Entry<Integer, ArrayList<Float>> entry : weightMap.entrySet()) {
                int day = entry.getKey();
                ArrayList<Float> weightList = entry.getValue();
                float averageWeight = calculateAverage(weightList);
                weightEntries.add(new Entry(day, averageWeight));
            }

            Log.d("MonitoryActivity", "Mean Weight" + weightEntries);
            return weightEntries;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private float calculateAverage(ArrayList<Float> values) {
        float sum = 0;
        for (float value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    private void plotLineChart(ArrayList<Entry> weightEntries) {
        LineDataSet lineDataSet = new LineDataSet(weightEntries, "Peso Médio");
        lineDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        lineDataSet.setCircleColors(ColorTemplate.MATERIAL_COLORS);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setValueTextSize(12f);

        LineData lineData = new LineData(lineDataSet);
        lineChart_weight.setData(lineData);

        // Ajuste o tamanho da fonte dos valores nas linhas (opcional)
        lineData.setValueTextSize(12f);

        // Desativar a legenda
        Legend legend = lineChart_weight.getLegend();
        legend.setEnabled(false);
    }
}
