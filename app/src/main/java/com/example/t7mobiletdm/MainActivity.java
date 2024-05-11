package com.example.t7mobiletdm;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean flashOn;
    private Button startFlashButton;
    private Button stopFlashButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        flashOn = false;

        // Start flash button
        startFlashButton = findViewById(R.id.startFlashButton);
        startFlashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFlashService();
                turnOnFlashlight();
            }
        });

        // Stop flash button
        stopFlashButton = findViewById(R.id.stopFlashButton);
        stopFlashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffFlashlight();
                stopFlashService();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] < proximitySensor.getMaximumRange()) {
                if (!flashOn) {
                    startFlashService();
                    turnOnFlashlight();
                } else {
                    turnOffFlashlight();
                    stopFlashService();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    private void turnOnFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, true);
            flashOn = true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void turnOffFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, false);
            flashOn = false;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startFlashService() {
        Intent serviceIntent = new Intent(this, FlashlightService.class);
        serviceIntent.setAction(FlashlightService.ACTION_START_FOREGROUND_SERVICE);
        startService(serviceIntent);
    }
    private void stopFlashService() {
        Intent serviceIntent = new Intent(this, FlashlightService.class);
        serviceIntent.setAction(FlashlightService.ACTION_STOP_FOREGROUND_SERVICE);
        startService(serviceIntent);
    }

}
