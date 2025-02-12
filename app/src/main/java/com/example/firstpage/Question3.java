package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Question3 extends AppCompatActivity {
    private double fuelCarbonQ3 = 0.0; // Carbon footprint per liter based on fuel type
    private String selectedFuelType = ""; // Store selected fuel type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question3);

        RadioGroup radioGroup = findViewById(R.id.radioGroupQ3);

        // Retrieve previous data from Question2
        double carbonQ2 = getIntent().getDoubleExtra("carbonQ2", 0.0);
        String username = getIntent().getStringExtra("username");

        // Handle fuel type selection
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                if (checkedId == R.id.btnGas) { // Gasoline
                    selectedFuelType = "Gasoline";
                    fuelCarbonQ3 = 2.3; // Example kg CO₂ per liter
                } else if (checkedId == R.id.btndiesel) { // Diesel
                    selectedFuelType = "Diesel";
                    fuelCarbonQ3 = 2.7; // Example kg CO₂ per liter
                } else if (checkedId == R.id.btnhybrid) { // Hybrid
                    selectedFuelType = "Hybrid";
                    fuelCarbonQ3 = 1.5; // Example kg CO₂ per liter
                }

                // Display selection confirmation
                Toast.makeText(Question3.this, "Selected Fuel: " + selectedFuelType +
                        " | Carbon: " + fuelCarbonQ3 + " kg CO₂ per liter", Toast.LENGTH_SHORT).show();

                // Proceed to Question 4
                Intent intent = new Intent(Question3.this, Question4.class);
                intent.putExtra("carbonQ2", carbonQ2); // Pass previous carbon data
                intent.putExtra("fuelCarbonQ3", fuelCarbonQ3); // Pass fuel emission data
                intent.putExtra("fuelType", selectedFuelType); // Pass selected fuel type
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}
