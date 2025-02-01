package com.example.firstpage;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;

public class Foodwaste extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodwaste);

        // Set up the back button functionality
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back
                onBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private OnBackPressedDispatcher onBackPressedDispatcher() {
        return null;
    }
}
