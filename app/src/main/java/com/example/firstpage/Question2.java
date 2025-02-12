package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Question2 extends AppCompatActivity {
    private double carbonFootprintQ2 = 0.0; // Carbon footprint per km based on selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question2);

        RadioGroup radioGroup = findViewById(R.id.radioGroupQ2);

        // Get username from intent
        String username = getIntent().getStringExtra("username");

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                if (checkedId == R.id.btnBus) {
                    carbonFootprintQ2 = 1.2; // Example CO₂ per km for a bus
                } else if (checkedId == R.id.btnJeep) {
                    carbonFootprintQ2 = 1.8; // Example CO₂ per km for a jeepney
                } else if (checkedId == R.id.btnTric) {
                    carbonFootprintQ2 = 2.5; // Example CO₂ per km for a tricycle
                } else if (checkedId == R.id.btnCar) {
                    carbonFootprintQ2 = 2.9; // Example CO₂ per km for a car
                } else if (checkedId == R.id.btnMotor) {
                    carbonFootprintQ2 = 1.5; // Example CO₂ per km for a motorcycle
                } else if (checkedId == R.id.btnBike || checkedId == R.id.btnWalk) {
                    carbonFootprintQ2 = 0.0; // Zero carbon footprint for biking and walking
                }

                // Show a confirmation toast
                Toast.makeText(Question2.this, "Selected Mode Carbon Footprint: " + carbonFootprintQ2 + " kg CO₂ per km", Toast.LENGTH_SHORT).show();

                // Proceed to Question 3
                Intent intent = new Intent(Question2.this, Question3.class);
                intent.putExtra("carbonQ2", carbonFootprintQ2);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}
