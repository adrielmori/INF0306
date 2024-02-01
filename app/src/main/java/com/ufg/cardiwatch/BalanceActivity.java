package com.ufg.cardiwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
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
    private static final UUID BALANCE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  // UUID padrão para dispositivos Bluetooth SPP

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;

    // ...

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
        try {
            if (ContextCompat.checkSelfPermission(BalanceActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                {
                    ActivityCompat.requestPermissions(BalanceActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 2);
                    return;
                }
            }

            bluetoothSocket = balanceDevice.createRfcommSocketToServiceRecord(BALANCE_UUID);
            try {
                bluetoothSocket.connect();
                Log.d("BalanceActivity", "Conectado ao dispositivo");
            } catch (IOException e) {
                Log.d("BalanceActivity", "Falha ao conectar ao dispositivo");
                return;
            }

            inputStream = bluetoothSocket.getInputStream();

            byte[] buffer = new byte[1024];
            int bytes;
            while ((bytes = inputStream.read(buffer)) != -1) {
                String readMessage = new String(buffer, 0, bytes);
                Toast.makeText(this, "Peso: " + readMessage, Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ...
}

