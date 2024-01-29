package com.ufg.cardiwatch.util;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BpmChartHelper {
    private LineChart lineChartBpm;

    public BpmChartHelper(LineChart lineChart) {
        this.lineChartBpm = lineChart;
    }

    public void plotBpmChart(JSONArray bpmArray) {
        ArrayList<Entry> bpmEntries = parseBpmDataFromJson(bpmArray);
        plotLineChart(bpmEntries);
    }

    private ArrayList<Entry> parseBpmDataFromJson(JSONArray bpmArray) {
        ArrayList<Entry> bpmEntries = new ArrayList<>();

        try {
            for (int i = 0; i < bpmArray.length(); i++) {
                JSONObject bpmObject = bpmArray.getJSONObject(i);

                int day = bpmObject.getInt("day");
                float bpmLevel = (float) bpmObject.getDouble("bpm");

                bpmEntries.add(new Entry(day, bpmLevel));
                // Log.d("MonitoryActivity", "Dia: " + day + ", BPM: " + bpmLevel);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bpmEntries;
    }

    private void plotLineChart(ArrayList<Entry> bpmEntries) {
        LineDataSet lineDataSet = new LineDataSet(bpmEntries, "BPM");
        lineDataSet.setColor(ColorTemplate.MATERIAL_COLORS[0]);
        lineDataSet.setCircleColor(ColorTemplate.MATERIAL_COLORS[0]);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(4f);

        LineData lineData = new LineData(lineDataSet);
        lineChartBpm.setData(lineData);

        // Ajuste o tamanho da fonte dos números
        lineDataSet.setValueTextSize(12f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte dos valores nas barras (opcional)
        lineData.setValueTextSize(12f);  // Altere o valor conforme necessário

        // Ajuste o tamanho da fonte da legenda (opcional)
        Legend legend = lineChartBpm.getLegend();
        legend.setTextSize(12f);  // Altere o valor conforme necessário

        lineChartBpm.invalidate(); // Refresh chart
    }
}
