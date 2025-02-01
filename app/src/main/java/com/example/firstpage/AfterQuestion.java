package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AfterQuestion extends AppCompatActivity {

    private Button nextbtn;
    private static final String TAG = "TotalCarbonActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_question);

        // Initialize UI elements
        TextView tvTotalCarbon = findViewById(R.id.tv_total_carbon);
        Button btnBack = findViewById(R.id.nextbtn);

        // Retrieve the total carbon footprint passed from FoodWasteActivity
        double totalCarbon = getIntent().getDoubleExtra("total_carbon", 0.0);

        // Log the received total carbon footprint for debugging
        Log.d(TAG, "Received total carbon footprint: " + totalCarbon);

        // Display the total carbon footprint in the TextView
        tvTotalCarbon.setText(String.format("%.2f g CO₂", totalCarbon));

        // Back button click listener
        btnBack.setOnClickListener(v -> {
            // Navigate back to the MainActivity
            Intent backIntent = new Intent(AfterQuestion.this, navbar.class);
            startActivity(backIntent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Optionally display a message or log the back press
        Toast.makeText(this, "You cannot go back from this page.", Toast.LENGTH_SHORT).show();

        // Prevent back navigation by not calling super.onBackPressed()
        // Uncomment the next line if you want to allow back navigation
        // super.onBackPressed();
    }
}
