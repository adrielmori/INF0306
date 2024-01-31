package com.ufg.cardiwatch.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.ufg.cardiwatch.MainActivity;
import com.ufg.cardiwatch.MonitoryActivity;
import com.ufg.cardiwatch.R;
import com.ufg.cardiwatch.model.Weight;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Mqtt {
    public static final String brokerURI = "18.211.191.131";
//    public static final String brokerURI = "34.198.232.62";

    public static void publishMessage(String topicName, String value) {
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();
        client.publishWith().topic(topicName).qos(MqttQos.AT_LEAST_ONCE).payload(value.getBytes()).send();
        client.disconnect();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void sendSubscriptionSendNotification(String topicName, AppCompatActivity activity, NotificationManager manager) {
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();

        // Use a callback to show the message on the screen
        client.toAsync().subscribeWith()
                .topicFilter(topicName)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            String message = new String(msg.getPayloadAsBytes(), StandardCharsets.UTF_8);
                            message = message.substring(11, message.length() - 1);
                            Weight weight = new Weight();
                            try {
                                JSONArray jsonArray = new JSONArray(message);

                                Gson gson = new Gson();
                                weight = gson.fromJson(jsonArray.getJSONObject(jsonArray.length() - 1).toString(), Weight.class);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            String messagemNotificacao = "";
                            DecimalFormat df = new DecimalFormat("#.##");
                            String peso = df.format(weight.getWeight());

                            if (weight.getWeight() > 110) {
                                messagemNotificacao = "Warning! Your expected weight is " + peso + "kg\n" +
                                        "This value is not satisfactory for the nutritionist.\n" +
                                        "You need to lose weight!" +
                                        "TIP: Try exercise more and sleep better.";
                            } else {
                                messagemNotificacao = "Your expected weight is " + peso + "kg\n" +
                                        "This value is satisfactory for the nutritionist.\n" +
                                        "Keep it up!";
                            }

                            Notification notification = new Notification.Builder(activity, "channel1")
                                    .setContentTitle("CardiWatch")
                                    .setContentText(messagemNotificacao)
                                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                                    .build();
                            manager.notify(1, notification);
                        }
                    });
                })
                .send();
    }
}
