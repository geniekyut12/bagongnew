package com.example.firstpage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class Loadingpage extends AppCompatActivity {

    private VideoView videoView;
    private ProgressBar progressBar;
    private static final int LOADING_TIME = 3000; // Total loading time in milliseconds
    private static final String TAG = "LoadingPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadingpage);

        // Initialize the VideoView and ProgressBar
        videoView = findViewById(R.id.videoViewBackground);
        progressBar = findViewById(R.id.progressBar);

        if (videoView == null || progressBar == null) {
            Log.e(TAG, "VideoView or ProgressBar is not initialized.");
            return;
        }

        // Hide the ProgressBar as it will remain invisible
        progressBar.setVisibility(View.INVISIBLE);

        // Set up VideoView with background video
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.mbgblur);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        // Delay for 3 seconds before transitioning to the homepage
        new Handler().postDelayed(this::redirectToHomepage, LOADING_TIME);
    }

    private void redirectToHomepage() {
        if (!isFinishing()) {
            Intent intent = new Intent(Loadingpage.this, Question1.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}
