package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Question2 extends AppCompatActivity {

    // Carbon footprint values in kg CO2 per km (Africa's data)
    private final double BUS_FOOTPRINT = 0.089;
    private final double JEEP_FOOTPRINT = 0.15;
    private final double TRICYCLE_FOOTPRINT = 0.25;
    private final double CAR_FOOTPRINT = 0.271;
    private final double MOTORCYCLE_FOOTPRINT = 0.103;
    private final double BIKE_FOOTPRINT = 0.0;
    private final double WALK_FOOTPRINT = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question2);

        // Find the RadioGroup
        RadioGroup radioGroup = findViewById(R.id.radioGroupQ2);

        // Set listener for RadioGroup selection
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) { // Ensure a valid selection
                RadioButton selectedButton = findViewById(checkedId);
                String vehicleType = selectedButton.getText().toString();

                double carbonFootprint = calculateFootprint(vehicleType, 10); // Example: 10 km

                // Pass data to Question3
                Intent intent = new Intent(Question2.this, Question3.class);
                intent.putExtra("vehicleType", vehicleType);
                intent.putExtra("carbonFootprint", carbonFootprint);
                startActivity(intent);
            }
        });
    }

    private double calculateFootprint(String vehicleType, double distance) {
        switch (vehicleType) {
            case "Bus":
                return BUS_FOOTPRINT * distance;
            case "Jeep":
                return JEEP_FOOTPRINT * distance;
            case "Tricycle":
                return TRICYCLE_FOOTPRINT * distance;
            case "Car":
                return CAR_FOOTPRINT * distance;
            case "Motor":
                return MOTORCYCLE_FOOTPRINT * distance;
            case "Bike":
            case "Walk":
                return 0.0;  // No emissions for walking or biking
            default:
                return 0.0;
        }
    }
}
