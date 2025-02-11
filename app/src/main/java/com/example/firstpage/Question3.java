package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Question3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question3);

        // Get data from the previous activity
        Intent intent = getIntent();
        String fuelType = intent.getStringExtra("fuelType");
        double carbonFootprint = intent.getDoubleExtra("carbonFootprint", 0.0);

        // Find the RadioGroup
        RadioGroup radioGroup = findViewById(R.id.radioGroupQ1);

        // Set listener for RadioGroup selection
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) { // Ensure a valid selection
                Intent nextIntent = new Intent(Question3.this, Question4.class);
                nextIntent.putExtra("fuelType", fuelType);
                nextIntent.putExtra("carbonFootprint", carbonFootprint);
                startActivity(nextIntent);
            }
        });
    }
}
