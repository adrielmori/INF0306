package com.ufg.cardiwatch;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.credentials.Credential;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
            .build();

    private GoogleSignInAccount account;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

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
        startActivity(intent);
    }

    public void monitoringActivity(View view) {
        Intent intent = new Intent(this, MonitoryActivity.class);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void accessGoogleFit() {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusYears(1);
        long endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond();
        long startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond();

        // pegar o aggregate de heart rate
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
                .build();

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {
                    for (Bucket bucket : dataReadResponse.getBuckets()) {
                        for (DataSet dataSet : bucket.getDataSets()) {
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                for (Field field : dp.getDataType().getFields()) {
                                    Log.i(TAG, "Heart Rate: " + dp.getValue(field));
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "onFailure()", e));

        // pegar o aggregate de steps
        readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
                .build();

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {
                    for (Bucket bucket : dataReadResponse.getBuckets()) {
                        for (DataSet dataSet : bucket.getDataSets()) {
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                for (Field field : dp.getDataType().getFields()) {
                                    Log.i(TAG, "Steps: " + dp.getValue(field));
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "onFailure()", e));

        // pegar o aggregate de activity
        readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
                .build();

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {
                    for (Bucket bucket : dataReadResponse.getBuckets()) {
                        for (DataSet dataSet : bucket.getDataSets()) {
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                for (Field field : dp.getDataType().getFields()) {
                                    Log.i(TAG, "Activity: " + dp.getValue(field));
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "onFailure()", e));
    }
}