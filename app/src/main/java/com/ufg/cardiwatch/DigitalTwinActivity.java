package com.ufg.cardiwatch;

import static com.ufg.cardiwatch.MainActivity.pessoa;
import static com.ufg.cardiwatch.util.Mqtt.brokerURI;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.ufg.cardiwatch.model.Pessoa;
import com.ufg.cardiwatch.model.WeekHorizon;
import com.ufg.cardiwatch.util.Mqtt;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DigitalTwinActivity extends AppCompatActivity {
    private static final String BALANCE_ADDRESS = "88:22:B2:FF:6A:87";
    private static final String UUID_CHARACTER = "0000181b-0000-1000-8000-00805f9b34fb";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private ArrayList<String> receivedData = new ArrayList<>();
    private boolean isFirstConnection = true; // Adicione esta linha
    private TextView pesoAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_twin);

        pesoAtual = findViewById(R.id.textView10);

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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth não disponível", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        connectToDevice();
    }

    private void connectToDevice() {
        BluetoothDevice balanceDevice = bluetoothAdapter.getRemoteDevice(BALANCE_ADDRESS);
        if (ContextCompat.checkSelfPermission(DigitalTwinActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(DigitalTwinActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }
        }

        bluetoothGatt = balanceDevice.connectGatt(this, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("DigitalTwinActivity", "Conectado ao dispositivo");
                    if (isFirstConnection) { // Adicione esta condição
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DigitalTwinActivity.this, "Dispositivo conectado: " + BALANCE_ADDRESS, Toast.LENGTH_LONG).show();
                            }
                        });
                        isFirstConnection = false; // Adicione esta linha
                    }
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d("DigitalTwinActivity", "Desconectado do dispositivo");
                    Log.d("DigitalTwinActivity", "Dados recebidos: " + receivedData.toString());
                    receivedData.clear();
                    connectToDevice(); // Tente reconectar
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    for (BluetoothGattService service : gatt.getServices()) {
                        if (service.getUuid().toString().equals(UUID_CHARACTER)) {
                            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                                gatt.setCharacteristicNotification(characteristic, true);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                byte[] data = characteristic.getValue();
                StringBuilder hexString = new StringBuilder();
                for (byte b : data) {
                    hexString.append(String.format("%02X-", b));
                }
                receivedData.add(hexString.toString());

                // KG witght Decoder
                if (data.length >= 13) {
                    int weight = ((data[12] & 0xFF) << 8) | (data[11] & 0xFF);
                    double weightInKg = weight / 200.0;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pesoAtual.setText(weightInKg + " kg");
                        }
                    });
                }
            }
        });
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