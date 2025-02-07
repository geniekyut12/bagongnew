package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Quiz1of1 extends AppCompatActivity {

    // Variable to track the score
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz1of1);

        // Find the RadioGroup
        RadioGroup radioGroup = findViewById(R.id.qz1q1chcs);

        // Set listener for RadioGroup selection
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) { // Ensure a valid selection
                // Check if the correct answer is selected (btnq1A)
                if (checkedId == R.id.btnq1A) { // Replace btnq1A with the actual button ID
                    score++; // Increment the score for the correct answer
                }

                // Proceed to the next question or finish quiz
                Intent intent = new Intent(Quiz1of1.this, Quiz2of1.class);
                // Pass the score to the next activity
                intent.putExtra("score", score);
                startActivity(intent);
            }
        });
    }

    // In the next activity (Quiz2of1), you can retrieve and use the score to display the total score.
}
