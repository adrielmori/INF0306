package com.ufg.cardiwatch;

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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


import android.util.Log;

public class BalanceActivity extends AppCompatActivity {

    private static final String BALANCE_ADDRESS = "88:22:B2:FF:6A:87";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private ArrayList<String> receivedData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth não disponível", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

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
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d("BalanceActivity", "Desconectado do dispositivo");
                    Log.d("BalanceActivity", "Dados recebidos: " + receivedData.toString());
                    receivedData.clear();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    for (BluetoothGattService service : gatt.getServices()) {
                        if (service.getUuid().toString().equals("0000181b-0000-1000-8000-00805f9b34fb")) { // Substitua pelo UUID do serviço
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

                // Decodificar o peso em kg
                if (data.length >= 13) { // Verifique se o payload tem pelo menos 13 bytes
                    int weight = ((data[12] & 0xFF) << 8) | (data[11] & 0xFF); // Bytes 11 e 12: peso (little endian)
                    double weightInKg = weight / 200.0; // Peso em kg
                    Log.d("BalanceActivity", "Weight in kg: " + weightInKg);
                }
            }


            public String bytesToHex(byte[] bytes) {
                StringBuilder builder = new StringBuilder();
                for (byte b: bytes) {
                    builder.append(String.format("%02x", b));
                }
                return builder.toString();
            }
        });
    }
}
