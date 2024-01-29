package com.ufg.cardiwatch;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
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

import java.sql.Array;
import java.util.ArrayList;

public class MonitoryActivity extends AppCompatActivity {

    Pessoa pessoa = new Pessoa();

    // Plots Variables
    private BarChart barChart;
    private BarData barData;
    private BarDataSet barDataSet;

    ArrayList barentries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitory);

        getData();
        barChart = findViewById(R.id.barchart);

        barDataSet = new BarDataSet(barentries, "Data set");
        barData = new BarData(barDataSet);
        barChart.setData(barData);

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(18f);


        Intent intent = getIntent();
        pessoa = (Pessoa) intent.getSerializableExtra("pessoa");

    }

    private void getData(){
        barentries = new ArrayList<>();
        barentries.add(new BarEntry(1f, 2));
        barentries.add(new BarEntry(1f, 3));
        barentries.add(new BarEntry(5f, 7));
        barentries.add(new BarEntry(6f, 10));
        barentries.add(new BarEntry(7f, 13));
    }
    public void enviaParaMqtt(View view) {
        Gson gson = new Gson();
        String json = gson.toJson(pessoa);

        Mqtt.publishMessage("cardiwatch", json);
    }
}