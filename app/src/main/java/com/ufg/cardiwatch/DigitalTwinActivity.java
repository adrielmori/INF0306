package com.ufg.cardiwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ufg.cardiwatch.model.Pessoa;
import com.ufg.cardiwatch.model.WeekHorizon;
import com.ufg.cardiwatch.util.Mqtt;

import java.util.List;

public class DigitalTwinActivity extends AppCompatActivity {

    private Pessoa pessoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_twin);

        getWeekData();

        pessoa = (Pessoa) getIntent().getSerializableExtra("pessoa");

        TextView pesoAtual = findViewById(R.id.textView10);

        if (pessoa.getWeights().size() == 0) {
            pesoAtual.setText("0 kg");
        } else {
            pesoAtual.setText(pessoa.getWeights().get(pessoa.getWeights().size() - 1).getWeight().toString() + " kg");
        }
    }

    private void getWeekData() {
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void enviarParaMqtt(View view) {
        Spinner spinner = findViewById(R.id.spinner);
        String week = spinner.getSelectedItem().toString();

        List<WeekHorizon> weekHorizon = List.of(new WeekHorizon(Integer.parseInt(week.substring(0, 1))));

        pessoa.setWeek_horizon(weekHorizon);

        Gson gson = new Gson();
        String json = gson.toJson(pessoa);

        Mqtt.publishMessage("cardiwatch", json);
    }
}