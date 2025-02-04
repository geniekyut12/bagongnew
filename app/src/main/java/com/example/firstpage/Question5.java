package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class Question5 extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView SBtext5;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question5);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize the SeekBar and TextView
        seekBar = findViewById(R.id.seekBar5);
        SBtext5 = findViewById(R.id.SBtext5);

        // Retrieve transportation carbon footprint from intent
        double transportationCarbon = getIntent().getDoubleExtra("transportation_carbon", 0.0);

        // Set up SeekBar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the TextView with current progress
                SBtext5.setText(progress + " kg");
                SBtext5.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Handle when the user starts sliding
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Capture the final weight value when the user stops sliding
                int weight = seekBar.getProgress();

                // Perform calculation for food carbon footprint
                double foodCarbon = calculateCarbonFootprint(weight);

                // Show a Toast with the calculated food carbon footprint
                Toast.makeText(Question5.this, "Food Carbon Footprint: " + foodCarbon + " kg CO₂", Toast.LENGTH_SHORT).show();

                // Save the combined results of Question1 and Question2 to Firestore
                saveToFirestore(weight, foodCarbon, transportationCarbon);

                // Redirect to the next activity
                Intent intent = new Intent(Question5.this, Question6.class);
                intent.putExtra("total_carbon", transportationCarbon + foodCarbon); // Pass combined carbon footprint
                startActivity(intent);

                // Optionally finish this activity to remove it from the back stack
                finish();
            }
        });
    }

    // Example method to calculate carbon footprint based on weight (kg)
    private double calculateCarbonFootprint(int weight) {
        // Example logic: 2.5 kg CO₂ per kg of food
        return weight * 2.5;
    }

    private void saveToFirestore(int weight, double foodCarbon, double transportationCarbon) {
        String userId = mAuth.getCurrentUser().getUid();
        String username = mAuth.getCurrentUser().getDisplayName(); // Assuming username is available as display name

        CollectionReference carbonFootprintsRef = db.collection("carbon_footprints");

        // Fetch and update the existing document
        carbonFootprintsRef.whereEqualTo("username", username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                String docId = document.getId();

                // Prepare the combined data for Question1 and Question2
                Map<String, Object> updatedData = new HashMap<>();

                // Question1: Transportation
                updatedData.put("Question1", document.get("Question1")); // Retain existing transportation data

                // Question2: Food consumption
                Map<String, Object> question2Data = new HashMap<>();
                question2Data.put("mealConsumption", weight + " kg");
                question2Data.put("foodCarbonFootprint", foodCarbon + " kg CO₂");
                updatedData.put("Question2", question2Data);

                // Update the document with the combined data
                carbonFootprintsRef.document(docId).update(updatedData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Question5.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Question5.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });

            } else {
                Toast.makeText(Question5.this, "No document found for user: " + username, Toast.LENGTH_SHORT).show();
            }
        });
    }

}