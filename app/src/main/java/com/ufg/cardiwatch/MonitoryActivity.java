package com.ufg.cardiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.ufg.cardiwatch.model.Pessoa;
import com.ufg.cardiwatch.util.ActivitiesChartHelper;
import com.ufg.cardiwatch.util.BpmChartHelper;
import com.ufg.cardiwatch.util.Mqtt;
import com.ufg.cardiwatch.util.SleepsChartHelper;
import com.ufg.cardiwatch.util.StepsChartHelper;
import com.ufg.cardiwatch.util.WeightsChartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MonitoryActivity extends AppCompatActivity {

    Pessoa pessoa = new Pessoa();

    private StepsChartHelper stepsChartHelper;
    private ActivitiesChartHelper activitiesChartHelper;
    private WeightsChartHelper weightChartHelper;
    private BpmChartHelper bpmChartHelper;
    private SleepsChartHelper sleepChartHelper;
    // Plots Variables
    private BarChart barChart;
    private LineChart lineChart;

    private String json;
    private String dataAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitory);

        // MQTT data publish
        Intent intent = getIntent();
        pessoa = (Pessoa) intent.getSerializableExtra("pessoa");

        // getData Logic Implementation
        barChart = findViewById(R.id.barchart_steps);
        stepsChartHelper = new StepsChartHelper(barChart);

        barChart = findViewById(R.id.barchart_act);
        activitiesChartHelper = new ActivitiesChartHelper(barChart);

        barChart = findViewById(R.id.barchart_weight);
        weightChartHelper = new WeightsChartHelper(barChart);

        barChart = findViewById(R.id.barchart_bpm);
        bpmChartHelper = new BpmChartHelper(barChart);

        barChart = findViewById(R.id.barchart_sleeps);
        sleepChartHelper = new SleepsChartHelper(barChart);

        dataAPI = getChartsFromAPI(pessoa);
        listKeysFromJson(dataAPI);

        Log.d("MonitoryActivity", "Enviando JSON para MQTT: " + dataAPI);
    }

    private String getChartsFromAPI(Pessoa pessoa) {
        Gson gson = new Gson();
        json = gson.toJson(pessoa);

        // Exibir chaves e valores do JSON
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                Iterator<String> keys = jsonObject.keys();

                while(keys.hasNext()) {
                    String key = keys.next();
                    Log.d("MonitoryActivity", key);
                }

                /*
                 * Steps Chart
                 * */
                if (jsonObject.has("steps")) {
                    JSONArray stepsArray = jsonObject.getJSONArray("steps");

                    Log.d("MonitoryActivity", "Valores para a chave steps -> " + stepsArray.toString());
                    stepsChartHelper.plotStepsChart(stepsArray);
                }

                /*
                 * Activity Chart
                 * */
                if (jsonObject.has("activities")) {
                    JSONArray activitiesArray = jsonObject.getJSONArray("activities");

                    Log.d("MonitoryActivity", "Valores para a chave activities -> " + activitiesArray.toString());
                    activitiesChartHelper.plotActivityChart(activitiesArray);
                }

                /*
                 * heartRates Chart
                 * */
                if (jsonObject.has("heartRates")) {
                    JSONArray bpmArray = jsonObject.getJSONArray("heartRates");

                    Log.d("MonitoryActivity", "Valores para a chave bpm -> " + bpmArray.toString());
                    bpmChartHelper.plotBpmChart(bpmArray);
                }

                /*
                 * sleeps Chart
                 * */
                if (jsonObject.has("sleeps")) {
                    JSONArray sleepsArray = jsonObject.getJSONArray("sleeps");

                    Log.d("MonitoryActivity", "Valores para a chave sleeps -> " + sleepsArray.toString());
                    sleepChartHelper.plotSleepsChart(sleepsArray);
                }

                /*
                 * weights Chart
                 * */
                if (jsonObject.has("weights")) {
                    JSONArray weightsArray = jsonObject.getJSONArray("weights");

                    Log.d("MonitoryActivity", "Valores para a chave weights -> " + weightsArray.toString());
                    weightChartHelper.plotWeightChart(weightsArray);
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

    private void listKeysFromJson(String jsonData) {
        try {
            if (jsonData != null) {
                JSONObject jsonObject = new JSONObject(jsonData);

                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Log.d("MonitoryActivity", "Chave: " + key);

                    // Se o valor associado à chave for outro objeto JSON, você pode
                    // chamar recursivamente esta função para listar as chaves desse objeto.
                    if (jsonObject.get(key) instanceof JSONObject) {
                        listKeysFromJson(jsonObject.get(key).toString());
                    }
                }
            } else {
                Log.d("MonitoryActivity", "A string JSON está nula.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
