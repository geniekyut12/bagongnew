package com.example.firstpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button getstartbtn;
    private static final String PREFS_NAME = "loginPrefs";
    private static final String PREF_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(PREF_IS_LOGGED_IN, false);

        // If logged in, start navbar (or any other screen) and finish MainActivity
        if (isLoggedIn) {
            Intent intent = new Intent(MainActivity.this,navbar.class);
            startActivity(intent);
            finish();
            return;  // Stop here if the user is logged in
        }

        // If not logged in, show MainActivity layout
        setContentView(R.layout.activity_main);

        // Initialize and set up "Get Started" button
        getstartbtn = findViewById(R.id.getstartbtn);
        getstartbtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Signin.class);
            startActivity(intent);
        });

        // Initialize and set up VideoView
        VideoView videoView = findViewById(R.id.videoViewBackground);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.mbg);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });
    }
}