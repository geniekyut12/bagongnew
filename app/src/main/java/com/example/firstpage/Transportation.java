package com.example.firstpage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;

public class Transportation extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView distanceTextView, carbonTextView;
    private Spinner spinnerTravelMode;
    private Button resetButton;
    private static final int LOCATION_PERMISSION_REQUEST = 1000;
    private int totalDistance = 0;
    private int totalCarbon = 0;
    private String selectedMode = "Car";
    private ActivityRecognitionClient activityRecognitionClient;

    private final BroadcastReceiver trackingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            totalDistance = intent.getIntExtra("total_distance", 0);
            totalCarbon = intent.getIntExtra("total_carbon", 0);
            updateUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transportation);

        distanceTextView = findViewById(R.id.textViewDistance);
        carbonTextView = findViewById(R.id.textViewCarbon);
        resetButton = findViewById(R.id.resetButton);
        spinnerTravelMode = findViewById(R.id.spinnerTravelMode);

        sharedPreferences = getSharedPreferences("TrackerPrefs", Context.MODE_PRIVATE);
        totalDistance = sharedPreferences.getInt("total_distance", 0);
        totalCarbon = sharedPreferences.getInt("total_carbon", 0);
        selectedMode = sharedPreferences.getString("selected_mode", "Car");

        updateUI();
        checkPermissions();
        checkGpsEnabled();

        registerReceiver(trackingReceiver, new IntentFilter("TRACKING_UPDATE"), Context.RECEIVER_NOT_EXPORTED);
        startForegroundTracking();

        setupSpinner();

        resetButton.setOnClickListener(v -> resetTracking());
    }

    private void checkGpsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is disabled! Please enable it.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startForegroundTracking();
            } else {
                Toast.makeText(this, "Location permission required!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startForegroundTracking() {
        Intent serviceIntent = new Intent(this, TrackingService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.travel_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTravelMode.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(selectedMode);
        spinnerTravelMode.setSelection(spinnerPosition);

        spinnerTravelMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMode = parent.getItemAtPosition(position).toString();
                sharedPreferences.edit().putString("selected_mode", selectedMode).apply();

                Intent modeIntent = new Intent(Transportation.this, TrackingService.class);
                modeIntent.setAction("UPDATE_MODE");
                modeIntent.putExtra("selected_mode", selectedMode);
                startService(modeIntent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void resetTracking() {
        Intent serviceIntent = new Intent(Transportation.this, TrackingService.class);
        stopService(serviceIntent);

        SharedPreferences.Editor editor = getSharedPreferences("TrackingData", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        distanceTextView.setText("0 m");
        carbonTextView.setText("0 kg CO₂");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startService(serviceIntent);
            Intent forceUpdateIntent = new Intent(Transportation.this, TrackingService.class);
            forceUpdateIntent.setAction("FORCE_UPDATE");
            startService(forceUpdateIntent);
        }, 1000);
    }

    private void updateUI() {
        distanceTextView.setText("Distance Traveled: " + totalDistance + " meters");
        carbonTextView.setText("CO₂ Emission: " + totalCarbon + " kg");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(trackingReceiver);
    }
}
