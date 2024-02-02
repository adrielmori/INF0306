package com.ufg.cardiwatch;

import static com.ufg.cardiwatch.MainActivity.pessoa;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import android.util.Log;

import com.google.gson.Gson;
import com.ufg.cardiwatch.model.Weight;
import com.ufg.cardiwatch.util.Mqtt;

public class BalanceActivity extends AppCompatActivity {

    private static final String BALANCE_ADDRESS = "88:22:B2:FF:6A:87";
    private static final String UUID_CHARACTER = "0000181b-0000-1000-8000-00805f9b34fb";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private ArrayList<String> receivedData = new ArrayList<>();
    private TextView weightTextView;
    private boolean isFirstConnection = true; // Adicione esta linha

    private Handler handler = new Handler();
    private Runnable runnableCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        weightTextView = findViewById(R.id.weightTextView);

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
        if (ContextCompat.checkSelfPermission(BalanceActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(BalanceActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }
        }

        bluetoothGatt = balanceDevice.connectGatt(this, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("BalanceActivity", "Conectado ao dispositivo");
                    if (isFirstConnection) { // Adicione esta condição
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BalanceActivity.this, "Dispositivo conectado: " + BALANCE_ADDRESS, Toast.LENGTH_LONG).show();
                            }
                        });
                        isFirstConnection = false; // Adicione esta linha
                    }
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if (receivedData != null && !receivedData.isEmpty()) {
                        String lastValue = receivedData.get(receivedData.size() - 1); // Aqui eu pego o útimo valor para a balança
                        double lastWeight = Double.parseDouble(lastValue);
                        if (lastWeight > 100.0) { // Adicione esta condição
                            enviarParaMqtt();
                            Log.d("BalanceActivity", "Dados recebidos: " + receivedData.toString());
                            Log.d("BalanceActivity", "Last Value: " + lastValue);
                            receivedData.clear();
                            gatt.disconnect();
                        }
                    }else {
                        Log.d("BalanceActivity", "Array Vazio ou usuário incorreto");
                        gatt.disconnect(); // Desconecte se não houver fluxo de dados
                    }
                    receivedData.clear();
//        connectToDevice(); // Tente reconectar
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    for (BluetoothGattService service : gatt.getServices()) {
                        if (service.getUuid().toString().equals(UUID_CHARACTER)) { // Substitua pelo UUID do serviço
                            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                                gatt.setCharacteristicNotification(characteristic, true); // Habilita notificações para a característica
                            }
                        }
                    }
                } else {
                    Log.w("BalanceActivity", "onServicesDiscovered received: " + status);
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
                    double finalWeightInKg = weightInKg;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weightTextView.setText(finalWeightInKg + " kg");
                        }
                    });

                    // Cancela qualquer Runnable pendente
                    if (runnableCode != null) {
                        handler.removeCallbacks(runnableCode);
                    }

                    // Cria um novo Runnable para verificar se receivedData foi alterado após 5 segundos
                    runnableCode = new Runnable() {
                        @Override
                        public void run() {
                            String lastValue = receivedData.get(receivedData.size() - 1);
                            double lastWeight = Double.parseDouble(lastValue);
                            if (lastWeight > 100.0) {
                                enviarParaMqtt();
                                receivedData.clear();
                            }
                        }
                    };

                    // Executa o Runnable após 5 segundos
                    handler.postDelayed(runnableCode, 5000);
                }
            }

        });
    }
    //      Complementar para permirtir a Digital Twin sem precisar em na aba de Digital Twin
    public void enviarParaMqtt() {
        Log.d("BalanceActivity", "Oie, estou indo para o MQTT");
        Gson gson = new Gson();
        pessoa.setWeights_predict(null);
        String json = gson.toJson(pessoa);

        Mqtt.publishMessage("cardiwatch", json);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
