package com.ufg.cardiwatch;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.ufg.cardiwatch.model.Pessoa;
import com.ufg.cardiwatch.util.Mqtt;
import com.ufg.cardiwatch.util.StepsChartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class MonitoryActivity extends AppCompatActivity {

    Pessoa pessoa = new Pessoa();
    private StepsChartHelper stepsChartHelper;

    // Plots Variables
    private BarChart barChart;
    private BarData barData;
    private BarDataSet barDataSet;

    private String json;

    private String dataAPI;

    ArrayList<BarEntry> barentries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitory);

        // MQTT data publish
        Intent intent = getIntent();
        pessoa = (Pessoa) intent.getSerializableExtra("pessoa");

        // getData Logic Implementation
        barChart = findViewById(R.id.barchart);
        stepsChartHelper = new StepsChartHelper(barChart);

        dataAPI = getChartsFromAPI(pessoa);

        Log.d("MonitoryActivity", "Enviando JSON para MQTT: " + dataAPI);

        getData();
        barChart = findViewById(R.id.barchart2);

        barDataSet = new BarDataSet(barentries, "Data set");
        barData = new BarData(barDataSet);
        barChart.setData(barData);

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(18f);
    }
    private void getData() {
        barentries = new ArrayList<>();
        barentries.add(new BarEntry(1f, 2));
        barentries.add(new BarEntry(2f, 3));
        barentries.add(new BarEntry(5f, 7));
        barentries.add(new BarEntry(6f, 10));
        barentries.add(new BarEntry(7f, 13));
    }

    private String getChartsFromAPI(Pessoa pessoa) {
        Gson gson = new Gson();
        json = gson.toJson(pessoa);

        // Exibir chaves e valores do JSON
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);

                //Plotes para os passos
                if (jsonObject.has("steps")) {
                    JSONArray stepsArray = jsonObject.getJSONArray("steps");

                    // Usar a classe StepsChartHelper para plotar o gráfico
                    stepsChartHelper.plotStepsChart(stepsArray);
                }
                if (jsonObject.has("steps")) {
                    JSONArray stepsArray = jsonObject.getJSONArray("steps");

                    // Usar a classe StepsChartHelper para plotar o gráfico
                    stepsChartHelper.plotStepsChart(stepsArray);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("MonitoryActivity", "A string JSON está nula.");
        }

        return json;
    }
    public void enviaParaMqtt(View view) {
        Gson gson = new Gson();
        json = gson.toJson(pessoa);

        Log.d("MonitoryActivity", "Enviando JSON para MQTT: " + json);

        Mqtt.publishMessage("cardiwatch", json);
    }
}
