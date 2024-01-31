package com.ufg.cardiwatch;

import static com.ufg.cardiwatch.MainActivity.pessoa;

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

        String message = "0";

        if (pessoa.getWeights() != null) {
            message = pessoa.getWeights().get(pessoa.getWeights().size() - 1).getWeight().toString();
        }

        TextView textView = findViewById(R.id.textViewWeight);
        textView.setText(message + " kg");
    }
}