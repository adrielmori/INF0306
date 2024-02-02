package com.ufg.cardiwatch;

import static com.ufg.cardiwatch.MainActivity.pessoa;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.bluetooth.BluetoothDevice;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import android.util.Log;

import com.ufg.cardiwatch.model.Weight;

public class BalanceActivity extends AppCompatActivity {

    private static final String BALANCE_ADDRESS = "88:22:B2:FF:6A:87";
    private static final String UUID_CHARACTER = "0000181b-0000-1000-8000-00805f9b34fb";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private ArrayList<String> receivedData = new ArrayList<>();
    private TextView weightTextView;
    private boolean isFirstConnection = true; // Adicione esta linha

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
                    Log.d("BalanceActivity", "Desconectado do dispositivo");
                    Log.d("BalanceActivity", "Dados recebidos: " + receivedData.toString());
                    receivedData.clear();
                    connectToDevice(); // Tente reconectar
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
                StringBuilder hexString = new StringBuilder();
                for (byte b : data) {
                    hexString.append(String.format("%02X-", b));
                }
                receivedData.add(hexString.toString());
                Log.d("BalanceActivity", "Data changed: " + hexString.toString());
                Log.d("BalanceActivity", "All received data: " + receivedData.toString());

                // KG witght Decoder
                double weightInKg = 0;
                if (data.length >= 13) {
                    int weight = ((data[12] & 0xFF) << 8) | (data[11] & 0xFF);
                    weightInKg = weight / 200.0;
    //                    Log.d("BalanceActivity", "Weight in kg: " + weightInKg);
                    double finalWeightInKg = weightInKg;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weightTextView.setText(finalWeightInKg + " kg");
                        }
                    });

                }

    //               Parte para o IGOR -> Fazer peso da balança dinamicamente
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                Long time = date.getTime();
                pessoa.setWeightBalance(new Weight(time, (float) weightInKg));
                Log.d("BalanceActivity","Peso kg " + pessoa.getWeightBalance().getWeight());
            }

        });
    }
}
