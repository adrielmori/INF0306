package com.ufg.cardiwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ufg.cardiwatch.util.Mqtt;

import java.util.Map;

public class CaloriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories);
    }

    public void enviaParaMqtt(View view) {
        EditText domingo = (EditText) findViewById(R.id.editTextNumber);
        EditText segunda = (EditText) findViewById(R.id.editTextNumber3);
        EditText terca = (EditText) findViewById(R.id.editTextNumber4);
        EditText quarta = (EditText) findViewById(R.id.editTextNumber5);
        EditText quinta = (EditText) findViewById(R.id.editTextNumber8);
        EditText sexta = (EditText) findViewById(R.id.editTextNumber6);
        EditText sabado = (EditText) findViewById(R.id.editTextNumber7);

        Map<String, String> message = Map.of(
                "Sunday", domingo.getText().toString(),
                "Monday", segunda.getText().toString(),
                "Tuesday", terca.getText().toString(),
                "Wednesday", quarta.getText().toString(),
                "Thursday", quinta.getText().toString(),
                "Friday", sexta.getText().toString(),
                "Saturday", sabado.getText().toString()
        );

        Gson gson = new Gson();
        String json = gson.toJson(message);

        Mqtt.publishMessage("calorias", json);
    }
}