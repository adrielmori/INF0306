package com.ufg.cardiwatch;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.ufg.cardiwatch.controller.GoogleFit;
import com.ufg.cardiwatch.model.Activity;
import com.ufg.cardiwatch.model.HeartRate;
import com.ufg.cardiwatch.model.Pessoa;
import com.ufg.cardiwatch.model.Sleep;
import com.ufg.cardiwatch.model.Step;
import com.ufg.cardiwatch.model.Weight;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final Pessoa pessoa = new Pessoa();
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
    }

    public void profileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("peso", pessoa.getWeights().get(pessoa.getWeights().size() - 1).getWeight().toString());
        startActivity(intent);
    }

    public void monitoringActivity(View view) {
        Intent intent = new Intent(this, MonitoryActivity.class);
        // enviar pessoa
        intent.putExtra("pessoa", pessoa);
        startActivity(intent);
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
            });
        });

    }
}