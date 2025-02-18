package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Question5 extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView SBtext5;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private double totalCarbon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question5);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        seekBar = findViewById(R.id.seekBar5);
        SBtext5 = findViewById(R.id.SBtext5);

        // Retrieve previous data
        totalCarbon = getIntent().getDoubleExtra("carbonQ2", 0.0)
                + getIntent().getDoubleExtra("fuelCarbonQ3", 0.0)
                + getIntent().getDoubleExtra("carbonQ4", 0.0);

        // SeekBar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SBtext5.setText(progress + " km");
                SBtext5.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                totalCarbon += calculateCarbonFootprint(seekBar.getProgress());
                saveToFirestore();
                navigateToNext();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
        });
    }

    private double calculateCarbonFootprint(int distance) {
        return distance * 0.25; // Example: 0.25 kg CO₂ per km
    }

    private void saveToFirestore() {
        String username = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getDisplayName() : null;
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Error: Username not found!", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("total_carbon_footprint", totalCarbon + " kg CO₂");

        db.collection("transportation").document(username)
                .set(data)
                .addOnSuccessListener(aVoid -> Toast.makeText(Question5.this, "Data saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(Question5.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void navigateToNext() {
        Intent intent = new Intent(Question5.this, Question6.class);
        intent.putExtra("total_carbon", totalCarbon);
        startActivity(intent);
        finish();
    }
}
