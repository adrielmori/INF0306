package com.ufg.cardiwatch.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.ufg.cardiwatch.R;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Mqtt {
//    public static final String brokerURI = "18.211.191.131";
    public static final String brokerURI = "34.198.232.62";

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
                            Notification notification = new Notification.Builder(activity, "channel1")
                                    .setContentTitle("CardiWatch")
                                    .setContentText(message)
                                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                                    .build();
                            manager.notify(1, notification);
                        }
                    });
                })
                .send();
    }
}
