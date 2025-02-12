package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

public class AfterQuestion extends AppCompatActivity {

    private static final String TAG = "TotalCarbonActivity";
    private TextView tvTotalCarbon;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private double totalCarbon = 0.0; // Store total carbon globally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_question);

        // Initialize UI elements
        tvTotalCarbon = findViewById(R.id.tv_total_carbon);
        Button btnBack = findViewById(R.id.nextbtn);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Fetch and compute total carbon footprint
        fetchAndComputeTotalCarbon();

        // Back button click listener
        btnBack.setOnClickListener(v -> {
            saveTotalCarbonToFirestore(totalCarbon);
        });
    }

    private void fetchAndComputeTotalCarbon() {
        // Get the logged-in username
        String username = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getDisplayName() : null;

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Error: Username not found!", Toast.LENGTH_LONG).show();
            return;
        }

        // Firestore references for both collections
        DocumentReference foodRef = db.collection("food_source").document(username);
        DocumentReference transpoRef = db.collection("transportation").document(username);

        // Use AtomicReference to hold mutable values inside lambdas
        AtomicReference<Double> foodCarbon = new AtomicReference<>(0.0);
        AtomicReference<Double> transpoCarbon = new AtomicReference<>(0.0);

        // Fetch carbon data from food_source
        foodRef.get().addOnSuccessListener(foodDoc -> {
            if (foodDoc.exists() && foodDoc.contains("total_carbon_footprint")) {
                String foodCarbonStr = foodDoc.getString("total_carbon_footprint");
                foodCarbon.set(parseCarbonValue(foodCarbonStr));
            }

            // Fetch carbon data from transportation
            transpoRef.get().addOnSuccessListener(transpoDoc -> {
                if (transpoDoc.exists() && transpoDoc.contains("total_carbon_footprint")) {
                    String transpoCarbonStr = transpoDoc.getString("total_carbon_footprint");
                    transpoCarbon.set(parseCarbonValue(transpoCarbonStr));
                }

                // Compute the total carbon footprint
                totalCarbon = foodCarbon.get() + transpoCarbon.get();

                // Log the total carbon footprint
                Log.d(TAG, "Total Carbon Footprint: " + totalCarbon + " kg CO₂");

                // Display the result in the TextView
                tvTotalCarbon.setText(String.format("%.2f kg CO₂", totalCarbon));
            }).addOnFailureListener(e -> Log.e(TAG, "Error fetching transportation data", e));
        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching food data", e));
    }

    private void saveTotalCarbonToFirestore(double totalCarbon) {
        String username = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getDisplayName() : null;

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Error: Username not found!", Toast.LENGTH_LONG).show();
            return;
        }

        // Store total carbon footprint in "carbon_footprint" collection using the username as the document ID
        DocumentReference carbonFootprintRef = db.collection("carbon_footprint").document(username);
        carbonFootprintRef.set(new CarbonData(totalCarbon))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Total carbon footprint saved successfully.");
                    // After saving, navigate to the next activity
                    Intent backIntent = new Intent(AfterQuestion.this, navbar.class);
                    startActivity(backIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving total carbon footprint", e);
                    Toast.makeText(this, "Failed to save data!", Toast.LENGTH_SHORT).show();
                });
    }

    // Helper method to extract numeric value from the stored carbon string
    private double parseCarbonValue(String carbonString) {
        if (carbonString != null && carbonString.contains(" kg CO₂")) {
            try {
                return Double.parseDouble(carbonString.replace(" kg CO₂", "").trim());
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing carbon value: " + carbonString, e);
            }
        }
        return 0.0;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "You cannot go back from this page.", Toast.LENGTH_SHORT).show();
    }

    // Simple POJO class for Firestore storage
    public static class CarbonData {
        private double total_carbon_footprint;

        public CarbonData() { } // Required empty constructor for Firestore

        public CarbonData(double totalCarbon) {
            this.total_carbon_footprint = totalCarbon;
        }

        public double getTotal_carbon_footprint() {
            return total_carbon_footprint;
        }

        public void setTotal_carbon_footprint(double total_carbon_footprint) {
            this.total_carbon_footprint = total_carbon_footprint;
        }
    }
}
