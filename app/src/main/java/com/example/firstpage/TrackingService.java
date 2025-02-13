package com.example.firstpage;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.*;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.CancellationTokenSource;

public class TrackingService extends Service {
    private static final String CHANNEL_ID = "TrackingServiceChannel";
    private static final float MIN_ACCURACY = 50.0f; //Stricter accuracy (Google Maps default)
    private static final float MIN_MOVEMENT = 1.5f;  //Lowered min movement for better detection

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location lastLocation = null;
    private float totalDistance = 0;
    private float totalCarbon = 0;
    private String selectedMode = "Car";
    private SharedPreferences sharedPreferences;
    private long lastUpdateTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sharedPreferences = getSharedPreferences("TrackingData", Context.MODE_PRIVATE);
        selectedMode = sharedPreferences.getString("selected_mode", "Car");

        createNotificationChannel();
        startForegroundServiceWithNotification();
        setupLocationUpdates();
        requestImmediateLocationUpdate();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Tracking Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void startForegroundServiceWithNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Tracking Active")
                .setContentText("Tracking movement using GPS & Network.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(1, notification);
    }

    private void setupLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(2000)
                .setMinUpdateDistanceMeters(1.5f)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    updateLocation(location);
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void startTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("TrackingService", "❌ Location permission not granted!");
            return;
        }

        fusedLocationClient.requestLocationUpdates(
                new LocationRequest.Builder(2000)
                        .setMinUpdateDistanceMeters(1.5f)
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .build(),
                locationCallback,
                Looper.getMainLooper());

        Log.d("TrackingService", "✅ GPS tracking started.");
    }

    private void updateLocation(Location location) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdateTime < 2000) {
            return;
        }
        lastUpdateTime = currentTime;

        Log.d("TrackingService", "📍 New GPS update: Lat=" + location.getLatitude() + ", Lng=" + location.getLongitude() + ", Accuracy=" + location.getAccuracy() + "m");

        if (location.getAccuracy() > MIN_ACCURACY) {
            Log.d("TrackingService", "⚠️ Poor accuracy (" + location.getAccuracy() + "m), forcing GPS refresh.");
            requestImmediateLocationUpdate();
            return;
        }

        if (lastLocation != null) {
            float distance = lastLocation.distanceTo(location);
            Log.d("TrackingService", "📏 Distance calculated: " + distance + "m");

            if (distance < MIN_MOVEMENT) {
                Log.d("TrackingService", "⚠️ Ignoring small movement (Less than " + MIN_MOVEMENT + "m)");
                return;
            }

            totalDistance += distance;
            totalCarbon = totalDistance * CarbonUtils.getCarbonEmissionRate(selectedMode);
            saveValues();
            sendUpdates();
        }
        lastLocation = location;
    }

    private void saveValues() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("totalDistance", totalDistance);
        editor.putFloat("totalCarbon", totalCarbon);
        editor.apply();
    }

    private void sendUpdates() {
        Intent intent = new Intent("TRACKING_UPDATE");
        intent.putExtra("total_distance", (int) totalDistance);
        intent.putExtra("total_carbon", (int) totalCarbon);
        sendBroadcast(intent);
    }

    private void requestImmediateLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("TrackingService", "❌ Location permission not granted!");
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        updateLocation(location);
                    }
                });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if ("FORCE_UPDATE".equals(intent.getAction())) {
                requestImmediateLocationUpdate();
            } else if ("UPDATE_MODE".equals(intent.getAction())) {
                selectedMode = intent.getStringExtra("selected_mode");
                sharedPreferences.edit().putString("selected_mode", selectedMode).apply();
            }
        }
        startTracking();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
