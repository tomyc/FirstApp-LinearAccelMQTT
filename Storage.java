package net.cieplak.firstapp_linearaccelmqtt;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tomasz on 07.04.2016.
 */
public class Storage implements SensorEventListener {

    public static final String BROKER_URL = "tcp://test.mosquitto.org:1883";
    public static final String TOPIC = "accel/data";
    private MqttClient client;


    public Storage() {
        connect();
    }

    public void write(long timestamp, float ax, float ay, float az) {

        String Json2Text="";
        JSONObject point;
        point = new JSONObject();
        try {
            point.put("Time", timestamp);
            point.put("XAxis", ax);
            point.put("YAxis", ay);
            point.put("ZAxis", az);
            Json2Text = point.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (client.isConnected()) {
            try {
                final MqttTopic mqttTopic = client.getTopic(TOPIC);

                MqttMessage message = new MqttMessage(Json2Text.getBytes());
                mqttTopic.publish(message);
            } catch (MqttException e) {
                System.out.println("Błąd publikowania wiadomości");
                e.printStackTrace();
            }
        }
    }


    public void connect() {
        try {
            client = new MqttClient(BROKER_URL, MqttClient.generateClientId(), new MemoryPersistence());
            client.connect();
        } catch (MqttPersistenceException e) {
            System.out.println("Błąd pamięci");
            e.printStackTrace();
        } catch (MqttException e) {
            System.out.println("Błąd połączenia");
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Long timestamp = System.currentTimeMillis();
        write(timestamp,event.values[0],event.values[1],event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
