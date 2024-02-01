package com.ufg.cardiwatch;

import static com.ufg.cardiwatch.MainActivity.pessoa;
import static com.ufg.cardiwatch.util.Mqtt.brokerURI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.ufg.cardiwatch.model.Pessoa;
import com.ufg.cardiwatch.model.WeekHorizon;
import com.ufg.cardiwatch.util.Mqtt;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class DigitalTwinActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_twin);


        TextView pesoAtual = findViewById(R.id.textView10);

        if (pessoa.getWeights().size() == 0) {
            pesoAtual.setText("0 kg");
        } else {
            pesoAtual.setText(pessoa.getWeights().get(pessoa.getWeights().size() - 1).getWeight().toString() + " kg");
        }

        Mqtt.publishMessage("ping", "ping");
        enviaSpinner("week", findViewById(R.id.spinner));
        enviaCheckBox("checkbox1", findViewById(R.id.checkBox));
        enviaCheckBox("checkbox2", findViewById(R.id.checkBox2));
        enviaCheckBox("checkbox3", findViewById(R.id.checkBox3));
        enviaCheckBox("checkbox4", findViewById(R.id.checkBox4));
        getWeekData();

        pegaCheckBox("checkbox1", this, findViewById(R.id.checkBox));
        pegaCheckBox("checkbox2", this, findViewById(R.id.checkBox2));
        pegaCheckBox("checkbox3", this, findViewById(R.id.checkBox3));
        pegaCheckBox("checkbox4", this, findViewById(R.id.checkBox4));

        sendSubscriptionSemanaSelecionada("week", this);
    }
    private void enviaSpinner(String topicName, Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spinnerValue = parent.getItemAtPosition(position).toString().equals("1 week") ? "1" : "2";
                Mqtt.publishMessage(topicName, spinnerValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void pegaCheckBox(String topicName, AppCompatActivity activity, CheckBox checkBox) {
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();

        // Use a callback to show the message on the screen
        client.toAsync().subscribeWith()
                .topicFilter(topicName)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            String message = new String(msg.getPayloadAsBytes(), StandardCharsets.UTF_8);
                            if (message.equals("1")) {
                                checkBox.setChecked(true);
                            } else {
                                checkBox.setChecked(false);
                            }
                        }
                    });
                })
                .send();
    }

    private void enviaCheckBox(String topicName, CheckBox checkBox) {
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Mqtt.publishMessage(topicName, "1");
                } else {
                    Mqtt.publishMessage(topicName, "0");
                }
            }
        });
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
        pessoa.setWeights_predict(null);
        String json = gson.toJson(pessoa);

        Mqtt.publishMessage("cardiwatch", json);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void sendSubscriptionSemanaSelecionada(String topicName, AppCompatActivity activity) {
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();

        // Use a callback to show the message on the screen
        client.toAsync().subscribeWith()
                .topicFilter(topicName)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            String message = new String(msg.getPayloadAsBytes(), StandardCharsets.UTF_8);
                            Spinner spinner = findViewById(R.id.spinner);
                            if (message.equals("1")) {
                                spinner.setSelection(0);
                            } else {
                                spinner.setSelection(1);
                            }
                        }
                    });
                })
                .send();
    }
}