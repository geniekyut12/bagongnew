package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        DocumentReference foodRef = db.collection("food_sources").document(username);
        DocumentReference transpoRef = db.collection("transportation").document(username);

        // Fetch both documents asynchronously
        Task<DocumentSnapshot> foodTask = foodRef.get();
        Task<DocumentSnapshot> transpoTask = transpoRef.get();

        Tasks.whenAllSuccess(foodTask, transpoTask).addOnSuccessListener(results -> {
            double foodCarbon = 0.0;
            double transpoCarbon = 0.0;

            DocumentSnapshot foodDoc = (DocumentSnapshot) results.get(0);
            DocumentSnapshot transpoDoc = (DocumentSnapshot) results.get(1);

            if (foodDoc.exists() && foodDoc.contains("total_carbon_footprint")) {
                String foodCarbonStr = foodDoc.getString("total_carbon_footprint");
                foodCarbon = parseCarbonValue(foodCarbonStr);
            }

            if (transpoDoc.exists() && transpoDoc.contains("total_carbon_footprint")) {
                String transpoCarbonStr = transpoDoc.getString("total_carbon_footprint");
                transpoCarbon = parseCarbonValue(transpoCarbonStr);
            }

            // Compute the total carbon footprint
            totalCarbon = foodCarbon + transpoCarbon;

            // Log the total carbon footprint
            Log.d(TAG, "Total Carbon Footprint: " + totalCarbon + " kg CO₂");

            // Display the result in the TextView
            tvTotalCarbon.setText(String.format("%.2f kg CO₂", totalCarbon));

        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching data", e));
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
