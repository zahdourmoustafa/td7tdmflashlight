package com.example.t7mobiletdm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.example.t7mobiletdm.MainActivity;
import com.example.t7mobiletdm.R;

public class FlashlightService extends Service {

    private static final int NOTIFICATION_ID = 123;

    private CameraManager cameraManager;
    private String cameraId;
    private boolean flashOn;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Flashlight Service Channel";
            String description = "Channel for flashlight service notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void turnOnFlashlight() {
        if (cameraManager == null) {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        }
        try {
            if (cameraId == null) {
                cameraId = cameraManager.getCameraIdList()[0];
            }
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_START_FOREGROUND_SERVICE)) {
                startForeground(NOTIFICATION_ID, createNotification());
                flashOn = true;
                // Turn on the flashlight
                turnOnFlashlight();
            } else if (intent.getAction().equals(ACTION_STOP_FOREGROUND_SERVICE)) {
                stopForeground(true);
                // Turn off the flashlight
                turnOffFlashlight();
                stopSelf();
                flashOn = false;
            }
        }
        return START_NOT_STICKY;
    }
    private void turnOffFlashlight() {
        if (cameraManager == null) {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        }
        try {
            if (cameraId == null) {
                cameraId = cameraManager.getCameraIdList()[0];
            }
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        Intent stopIntent = new Intent(this, FlashlightService.class);
        stopIntent.setAction(ACTION_STOP_FOREGROUND_SERVICE);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Flashlight Service")
                .setContentText("Flashlight is on")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setColor(Color.BLUE)
                .addAction(R.drawable.ic_launcher_foreground, "Stop", pendingStopIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Flashlight Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        return builder.build();
    }

    public static final String ACTION_START_FOREGROUND_SERVICE = "startForegroundService";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "stopForegroundService";

    private static final String CHANNEL_ID = "FlashlightServiceChannel";

}
