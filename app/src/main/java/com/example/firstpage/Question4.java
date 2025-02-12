package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Question4 extends AppCompatActivity {

    private double carbonQ2, fuelCarbonQ3, carbonQ4; // Carbon footprints from previous questions
    private String username; // Username for Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question4);

        // Retrieve data from previous activities
        Intent intent = getIntent();
        carbonQ2 = intent.getDoubleExtra("carbonQ2", 0.0); // Vehicle carbon footprint
        fuelCarbonQ3 = intent.getDoubleExtra("fuelCarbonQ3", 0.0); // Fuel type carbon footprint
        username = intent.getStringExtra("username"); // User's username

        // Find RadioGroup
        RadioGroup fuelConsumptionGroup = findViewById(R.id.radioGroupQ1);

        // Set listener for selection
        fuelConsumptionGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                if (checkedId == R.id.btnperkm) {
                    carbonQ4 = calculateCarbonPerKm();
                } else if (checkedId == R.id.btnpergallon) {
                    carbonQ4 = calculateCarbonPerGallon();
                }

                // Debugging log
                Toast.makeText(this, "Carbon Q4: " + carbonQ4 + " kg CO₂", Toast.LENGTH_SHORT).show();

                // Pass data to Question5
                Intent nextIntent = new Intent(Question4.this, Question5.class);
                nextIntent.putExtra("carbonQ2", carbonQ2);
                nextIntent.putExtra("fuelCarbonQ3", fuelCarbonQ3);
                nextIntent.putExtra("carbonQ4", carbonQ4);
                nextIntent.putExtra("username", username);
                startActivity(nextIntent);
            }
        });
    }

    // Example calculations
    private double calculateCarbonPerKm() {
        return 0.21; // Example: 0.21 kg CO₂ per km
    }

    private double calculateCarbonPerGallon() {
        return 2.3; // Example: 2.3 kg CO₂ per gallon
    }
}
