package com.ufg.cardiwatch;

import static com.ufg.cardiwatch.util.Mqtt.brokerURI;

import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.gson.Gson;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.ufg.cardiwatch.controller.GoogleFit;
import com.ufg.cardiwatch.model.Activity;
import com.ufg.cardiwatch.model.HeartRate;
import com.ufg.cardiwatch.model.Pessoa;
import com.ufg.cardiwatch.model.Sleep;
import com.ufg.cardiwatch.model.Step;
import com.ufg.cardiwatch.model.WeekHorizon;
import com.ufg.cardiwatch.model.Weight;
import com.ufg.cardiwatch.util.Mqtt;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static final Pessoa pessoa = new Pessoa();
    private static final String BALANCE_ADDRESS = "88:22:B2:FF:6A:87";
    private static final String UUID_CHARACTER = "0000181b-0000-1000-8000-00805f9b34fb";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private ArrayList<String> receivedData = new ArrayList<>(); //Aqui eu guardo o que a balaça rebenbe

    private FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_WEIGHT_SUMMARY, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
            .build();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationChannel channel = new NotificationChannel("channel1", "channel1", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    1, // e.g. 1
                    account,
                    fitnessOptions);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                accessGoogleFit();
            }
        }


        Intent intent = getIntent();
        if (intent != null) {
            Serializable pessoaSerializable = intent.getSerializableExtra("pessoa");
            if (pessoaSerializable != null) {
                Pessoa pessoa = (Pessoa) pessoaSerializable;
                this.pessoa.setCalories(pessoa.getCalories());
            }
        }

        Mqtt.sendSubscriptionSendNotification("cardiwatch_request", this, manager);
        sendSubscriptionSendColocaPesosPreditos("cardiwatch_request", this);

//        Bluetooth Phase
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth não disponível", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        connectToDevice();
    }

    public void profileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void digitalTwinActivity(View view) {
        Intent intent = new Intent(this, DigitalTwinActivity.class);
        startActivity(intent);
    }

    public void monitoringActivity(View view) {
        Intent intent = new Intent(this, MonitoryActivity.class);
        startActivity(intent);
    }

    public void caloriesActivity(View view) {
        Intent intent = new Intent(this, CaloriesActivity.class);
        startActivity(intent);
    }

    public void balanceActivity(View view) {
        Intent intent = new Intent(this, BalanceActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            if (requestCode == 1) {
                accessGoogleFit();
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void accessGoogleFit() {
        // criar uma thread para pegar os dados
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Esta parte será executada em uma thread separada
            List<Step> steps = GoogleFit.getSteps(MainActivity.this);
            List<HeartRate> heartRates = GoogleFit.getHeartRate(MainActivity.this);
            List<Activity> activities = GoogleFit.getActivities(MainActivity.this);
            List<Weight> weights = GoogleFit.getWeight(MainActivity.this);
            List<Sleep> sleeps = GoogleFit.getSleep(MainActivity.this);

            runOnUiThread(() -> {
                // Este código será executado na thread principal
                pessoa.setSteps(steps);
                pessoa.setHeartRates(heartRates);
                pessoa.setActivities(activities);
                pessoa.setWeights(weights);
                pessoa.setSleeps(sleeps);
                Mqtt.publishMessage("pesoAtual", Objects.requireNonNull(pessoa.getWeights().get(pessoa.getWeights().size() - 1).getWeight()).toString());
            });
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendSubscriptionSendColocaPesosPreditos(String topicName, AppCompatActivity activity) {
        List<Weight> weights = new ArrayList<>();

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
                            message = message.substring(11, message.length() - 1);
                            try {
                                JSONArray jsonArray = new JSONArray(message);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Gson gson = new Gson();
                                    Weight weight = gson.fromJson(jsonArray.getJSONObject(i).toString(), Weight.class);
                                    weights.add(weight);
                                }

                                pessoa.setWeights_predict(weights);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                })
                .send();
    }


    //      Complementar para permirtir a Digital Twin sem precisar em na aba de Digital Twin
    public void enviarParaMqtt() {
        Log.d("MainActivity", "Oie, estou indo para o MQTT");
        Gson gson = new Gson();
        pessoa.setWeights_predict(null);
        String json = gson.toJson(pessoa);

        Mqtt.publishMessage("cardiwatch", json);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void connectToDevice() {
        BluetoothDevice balanceDevice = bluetoothAdapter.getRemoteDevice(BALANCE_ADDRESS);
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }
        }
        bluetoothGatt = balanceDevice.connectGatt(this, false, new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("MainActivity", "Conectado ao dispositivo");
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if (receivedData != null && !receivedData.isEmpty()) {
                        String lastValue = receivedData.get(receivedData.size() - 1); // Aqui eu pego o útimo valor para a balança
                        double lastWeight = Double.parseDouble(lastValue);
                        if (lastWeight > 100.0) { // Adicione esta condição
                            enviarParaMqtt();
                            Log.d("MainActivity", "Dados recebidos: " + receivedData.toString());
                            Log.d("MainActivity", "Last Value: " + lastValue);
                        }
                    } else {
                        Log.d("MainActivity", "Array Vazio ou usuário incorreto");
                    }
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

                // KG weight Decoder
                if (data.length >= 13) {
                    int weight = ((data[12] & 0xFF) << 8) | (data[11] & 0xFF);
                    double weightInKg = weight / 200.0;
                    receivedData.add(Double.toString(weightInKg));
                    Log.d("MainActivity", "Weights Array " + receivedData);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bluetoothGatt != null) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothGatt.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothGatt != null) {
            connectToDevice();
        }
    }
}