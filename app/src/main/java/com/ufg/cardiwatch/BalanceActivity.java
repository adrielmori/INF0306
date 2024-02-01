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
import java.util.UUID;


import android.util.Log;

public class BalanceActivity extends AppCompatActivity {

    private static final String BALANCE_ADDRESS = "88:22:B2:FF:6A:87";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;

    UUID serviceUUID = UUID.fromString("00001530-0000-3512-2118-0009AF100700");
    UUID characteristicUUID = UUID.fromString("YOUR_CHARACTERISTIC_UUID"); // Substitua pelo UUID da característica

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
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    BluetoothGattService service = gatt.getService(serviceUUID);
                    if (service != null) {
                        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
                        if (characteristic != null) {
                            gatt.readCharacteristic(characteristic);
                        }
                    }
                } else {
                    Log.w("BalanceActivity", "onServicesDiscovered received: " + status);
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    byte[] data = characteristic.getValue();
                    // Aqui você pode processar os dados recebidos
                }
            }
        });
    }
}


