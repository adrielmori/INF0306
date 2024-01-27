package com.ufg.cardiwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ufg.cardiwatch.model.Calory;
import com.ufg.cardiwatch.model.Pessoa;
import com.ufg.cardiwatch.util.Mqtt;

import java.util.List;
import java.util.Map;

public class CaloriesActivity extends AppCompatActivity {

    private Pessoa pessoa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories);

        Intent intent = getIntent();
        pessoa = (Pessoa) intent.getSerializableExtra("pessoa");

        if (pessoa == null) {
            pessoa = new Pessoa();
        }
    }

    public void enviaParaMqtt(View view) {
        EditText domingo = (EditText) findViewById(R.id.editTextNumber);
        EditText segunda = (EditText) findViewById(R.id.editTextNumber3);
        EditText terca = (EditText) findViewById(R.id.editTextNumber4);
        EditText quarta = (EditText) findViewById(R.id.editTextNumber5);
        EditText quinta = (EditText) findViewById(R.id.editTextNumber8);
        EditText sexta = (EditText) findViewById(R.id.editTextNumber6);
        EditText sabado = (EditText) findViewById(R.id.editTextNumber7);

        List<Calory> calories = List.of(
                new Calory("Sunday", Float.parseFloat(domingo.getText().toString())),
                new Calory("Monday", Float.parseFloat(segunda.getText().toString())),
                new Calory("Tuesday", Float.parseFloat(terca.getText().toString())),
                new Calory("Wednesday", Float.parseFloat(quarta.getText().toString())),
                new Calory("Thursday", Float.parseFloat(quinta.getText().toString())),
                new Calory("Friday", Float.parseFloat(sexta.getText().toString())),
                new Calory("Saturday", Float.parseFloat(sabado.getText().toString()))
        );

        pessoa.setCalories(calories);

        Gson gson = new Gson();

        String json = gson.toJson(pessoa);

        Mqtt.publishMessage("messager", json);
    }
}