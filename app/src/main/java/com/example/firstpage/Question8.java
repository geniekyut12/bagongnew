package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Question8 extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private double sourceCarbon;
    private double totalCarbon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question8);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI
        RadioGroup radioGroup = findViewById(R.id.radioGroupQ8);

        // Retrieve carbon data from previous activity
        totalCarbon = getIntent().getDoubleExtra("total_carbon", 0.0);

        // RadioGroup selection listener
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                sourceCarbon = computeSourceCarbon(checkedId);
                totalCarbon += sourceCarbon;  // Update total carbon footprint

                // Save food source data to Firestore and go to the next activity
                saveFoodSourceData(totalCarbon);
            }
        });
    }

    // Compute carbon footprint based on food source selection
    private double computeSourceCarbon(int checkedId) {
        if (checkedId == R.id.btnomni) return 1.0;  // Organic (lower footprint)
        if (checkedId == R.id.btnRare) return 3.0;  // Non-organic (higher footprint)
        return 0.0;
    }

    // Save food data to Firestore and navigate to next activity
    private void saveFoodSourceData(double finalCarbon) {
        String username = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getDisplayName() : null;

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Error: Username not found!", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> foodData = new HashMap<>();
        foodData.put("total_carbon_footprint", finalCarbon + " kg CO₂");

        // Save to Firestore
        db.collection("food_sources").document(username).set(foodData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Food data saved!", Toast.LENGTH_SHORT).show();
                    // Move to the next activity after saving data
                    Intent intent = new Intent(Question8.this, Question9.class);
                    intent.putExtra("total_carbon", finalCarbon);
                    startActivity(intent);
                    finish(); // Close current activity
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save data", Toast.LENGTH_LONG).show());
    }
}
