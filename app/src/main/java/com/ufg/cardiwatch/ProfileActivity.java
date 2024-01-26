package com.ufg.cardiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        String message = intent.getStringExtra("peso");

        TextView textView = findViewById(R.id.textViewWeight);
        textView.setText(message + " kg");
    }
}