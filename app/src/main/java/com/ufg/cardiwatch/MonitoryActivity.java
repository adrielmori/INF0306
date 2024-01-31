package com.ufg.cardiwatch;

import static com.ufg.cardiwatch.MainActivity.pessoa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
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
import com.ufg.cardiwatch.util.WeightsPredictionChartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MonitoryActivity extends AppCompatActivity {

    private StepsChartHelper stepsChartHelper;
    private ActivitiesChartHelper activitiesChartHelper;
    private WeightsChartHelper weightChartHelper;
    private WeightsPredictionChartHelper weightsPredictionChartHelper;
    private BpmChartHelper bpmChartHelper;
    private SleepsChartHelper sleepChartHelper;
    // Plots Variables
    private BarChart barChart;
    private HorizontalBarChart horizontalBarChartChart;
    private LineChart lineChart;
    private CombinedChart combinedChart;

    // API response get
    private String json;
    private String dataAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitory);

        Gson gson = new Gson();
        json = gson.toJson(pessoa);
        listKeysFromJson(json);

        // getData Logic Implementation
        barChart = findViewById(R.id.barchart_weight);
        if (!checkWeightsPredict(pessoa)) {
            Log.d("MonitoryActivity", "'weights_predict' não existe ou está vazia.");
            weightChartHelper = new WeightsChartHelper(barChart);
        } else {
            weightsPredictionChartHelper = new WeightsPredictionChartHelper(barChart);
        }

        barChart = findViewById(R.id.barchart_steps);
        stepsChartHelper = new StepsChartHelper(barChart);

        horizontalBarChartChart = findViewById(R.id.barchart_act);
        activitiesChartHelper = new ActivitiesChartHelper(horizontalBarChartChart);

        lineChart = findViewById(R.id.barchart_bpm);
        bpmChartHelper = new BpmChartHelper(lineChart);

        lineChart = findViewById(R.id.barchart_sleeps);
        sleepChartHelper = new SleepsChartHelper(lineChart);

        dataAPI = getChartsFromAPI(pessoa, checkWeightsPredict(pessoa));

        Log.d("MonitoryActivity", "Enviando JSON para MQTT: " + dataAPI);
    }

    private String getChartsFromAPI(Pessoa pessoa, boolean checkWeightsPredict) {
        Gson gson = new Gson();
        json = gson.toJson(pessoa);

        // Exibir chaves e valores do JSON
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                Iterator<String> keys = jsonObject.keys();

                while(keys.hasNext()) {
                    String key = keys.next();
//                    Log.d("MonitoryActivity", key);
                }

                /*
                 * weights Chart
                 * */
                if (jsonObject.has("weights") && !checkWeightsPredict) {
                    JSONArray weightsArray = jsonObject.getJSONArray("weights");

//                    Log.d("MonitoryActivity", "Valores para a chave weights -> " + weightsArray.toString());
                    weightChartHelper.plotWeightChart(weightsArray);
                }

                /*
                 * weights Predicted Chart
                 * */
                if (jsonObject.has("weights") && checkWeightsPredict) {
                    JSONArray weightsArray = jsonObject.getJSONArray("weights");
                    JSONArray weightsPredictedArray = jsonObject.getJSONArray("weights_predict");

//                    Log.d("MonitoryActivity", "Valores para a chave weights -> " + weightsArray.toString());
                    weightsPredictionChartHelper.plotWeightChart(weightsArray, weightsPredictedArray);
                }

                /*
                 * Steps Chart
                 * */
                if (jsonObject.has("steps")) {
                    JSONArray stepsArray = jsonObject.getJSONArray("steps");

//                    Log.d("MonitoryActivity", "Valores para a chave steps -> " + stepsArray.toString());
                    stepsChartHelper.plotStepsChart(stepsArray);
                }

                /*
                 * Activity Chart
                 * */
                if (jsonObject.has("activities")) {
                    JSONArray activitiesArray = jsonObject.getJSONArray("activities");

//                    Log.d("MonitoryActivity", "Valores para a chave activities -> " + activitiesArray.toString());
                    activitiesChartHelper.plotActivityChart(activitiesArray);
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
                 * heartRates Chart
                 * */
                if (jsonObject.has("heartRates")) {
                    JSONArray bpmArray = jsonObject.getJSONArray("heartRates");

//                    Log.d("MonitoryActivity", "Valores para a chave bpm -> " + bpmArray.toString());
                    bpmChartHelper.plotBpmChart(bpmArray);
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

    private boolean checkWeightsPredict(Pessoa pessoa) {
        Gson gson = new Gson();
        String json = gson.toJson(pessoa);

        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                if (!jsonObject.has("weights_predict") || jsonObject.getJSONArray("weights_predict").length() == 0) {
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("MonitoryActivity", "A string JSON está nula.");
        }

        return true;
    }


}
