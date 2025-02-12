package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Question7 extends AppCompatActivity {

    private double dietCarbon;
    private double totalCarbon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question7);

        // Initialize UI
        RadioGroup radioGroup = findViewById(R.id.radioGroupQ7);

        // Retrieve total carbon footprint from previous activity
        totalCarbon = getIntent().getDoubleExtra("total_carbon", 0.0);

        // RadioGroup selection listener
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) { // Ensure a valid selection
                dietCarbon = computeDietCarbon(checkedId);
                totalCarbon += dietCarbon;

                // Show carbon footprint
                Toast.makeText(this, "Diet Carbon Footprint: " + dietCarbon + " kg CO₂", Toast.LENGTH_SHORT).show();

                // Pass updated total carbon footprint to next activity
                navigateToNext();
            }
        });
    }

    // Compute carbon footprint based on diet choice
    private double computeDietCarbon(int checkedId) {
        if (checkedId == R.id.btnveg) return 1.5;   // Vegan
        if (checkedId == R.id.btnvege) return 2.0;  // Vegetarian
        if (checkedId == R.id.btnmeat) return 3.5;  // Meat-based
        return 0.0;
    }

    // Navigate to the next activity
    private void navigateToNext() {
        Intent intent = new Intent(this, Question8.class);
        intent.putExtra("total_carbon", totalCarbon);
        startActivity(intent);
        finish();
    }
}
