package com.ufg.cardiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.ufg.cardiwatch.model.Pessoa;
import com.ufg.cardiwatch.util.Mqtt;

public class MonitoryActivity extends AppCompatActivity {

    Pessoa pessoa = new Pessoa();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitory);

        Intent intent = getIntent();
        pessoa = (Pessoa) intent.getSerializableExtra("pessoa");
    }

    public void enviaParaMqtt(View view) {
        Gson gson = new Gson();
        String json = gson.toJson(pessoa);

        Mqtt.publishMessage("cardiwatch", json);
    }
}