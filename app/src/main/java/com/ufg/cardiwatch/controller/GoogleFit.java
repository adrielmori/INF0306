package com.ufg.cardiwatch.controller;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.ufg.cardiwatch.model.Activity;
import com.ufg.cardiwatch.model.HeartRate;
import com.ufg.cardiwatch.model.Sleep;
import com.ufg.cardiwatch.model.Step;
import com.ufg.cardiwatch.model.Weight;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GoogleFit {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<HeartRate> getHeartRate(Context context) {
        List<HeartRate> heartRates = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusYears(1);
        long endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond();
        long startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_HEART_RATE_BPM)
                .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
                .bucketByTime(4, TimeUnit.HOURS)
                .build();

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {
                    for (Bucket bucket : dataReadResponse.getBuckets()) {
                        for (DataSet dataSet : bucket.getDataSets()) {
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                HeartRate heartRate = new HeartRate();
                                heartRate.setDay(dp.getStartTime(TimeUnit.DAYS));
                                heartRate.setBpm(dp.getValue(dp.getDataType().getFields().get(0)).asFloat());
                                heartRates.add(heartRate);
                            }
                        }
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure()", e);
                    latch.countDown();
                });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return heartRates;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Step> getSteps(Context context) {
        final List<Step> steps = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusYears(1);
        long endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond();
        long startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
                .build();

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {
                    for (Bucket bucket : dataReadResponse.getBuckets()) {
                        for (DataSet dataSet : bucket.getDataSets()) {
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                Step step = new Step();
                                step.setDay(dp.getStartTime(TimeUnit.DAYS));
                                step.setSteps(dp.getValue(dp.getDataType().getFields().get(0)).asInt());
                                steps.add(step);
                            }
                        }
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure()", e);
                    latch.countDown();
                });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return steps;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Activity> getActivities(Context context) {
        final List<Activity> activities = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusYears(1);
        long endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond();
        long startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
                .build();

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {
                    for (Bucket bucket : dataReadResponse.getBuckets()) {
                        for (DataSet dataSet : bucket.getDataSets()) {
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                Activity activity = new Activity();
                                activity.setDay(dp.getStartTime(TimeUnit.DAYS));
                                activity.setActivity(dp.getValue(dp.getDataType().getFields().get(0)).asInt());
                                activities.add(activity);
                            }
                        }
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure()", e);
                    latch.countDown();
                });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return activities;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Weight> getWeight(Context context) {
        final List<Weight> weights = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusYears(1);
        long endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond();
        long startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_WEIGHT, DataType.AGGREGATE_WEIGHT_SUMMARY)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
                .build();

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {
                    for (Bucket bucket : dataReadResponse.getBuckets()) {
                        for (DataSet dataSet : bucket.getDataSets()) {
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                Weight weight = new Weight();
                                weight.setDay(dp.getStartTime(TimeUnit.DAYS));
                                weight.setWeight(dp.getValue(dp.getDataType().getFields().get(0)).asFloat());
                                weights.add(weight);
                            }
                        }
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure()", e);
                    latch.countDown();
                });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return weights;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Sleep> getSleep(Context context) {
        final List<Sleep> sleeps = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusYears(1);
        long endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond();
        long startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_SLEEP_SEGMENT)
                .bucketByTime(2, TimeUnit.DAYS)
                .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
                .build();

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readData(readRequest)
                .addOnSuccessListener(dataReadResponse -> {
                    for (Bucket bucket : dataReadResponse.getBuckets()) {
                        for (DataSet dataSet : bucket.getDataSets()) {
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                Sleep sleep = new Sleep();
                                sleep.setDay(dp.getStartTime(TimeUnit.DAYS));
                                for (Field field : dp.getDataType().getFields()) {
                                    sleep.setSleep(dp.getValue(field).asInt());
                                }
                                sleeps.add(sleep);
                            }
                        }
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure()", e);
                    latch.countDown();
                });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return sleeps;
    }
}
