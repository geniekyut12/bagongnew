package com.example.firstpage;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class Quiz1After extends AppCompatActivity {

    private Button btnq1done;
    private TextView scoreTextView; // Declare TextView for displaying score

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz1_after); // Set your XML file here

        // Initialize button and TextView
        btnq1done = findViewById(R.id.btnq1done);
        scoreTextView = findViewById(R.id.q1score); // Initialize the TextView for the score

        // Retrieve the score passed from the previous activity
        int score = getIntent().getIntExtra("score", 0); // Default score is 0 if not passed

        // Display the score in the TextView
        scoreTextView.setText("Your final score: " + score);

        // Set click listener for the button
        btnq1done.setOnClickListener(v -> {
            if (validateBeforeRedirect()) {
                navigateToQuizFragment();
            } else {
                Toast.makeText(Quiz1After.this, "Validation failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validation logic before navigating
    private boolean validateBeforeRedirect() {
        // Example validation: Check if a certain condition is met
        // Replace this with your actual validation logic
        return true; // Return false to prevent navigation if validation fails
    }

    // Replace current fragment with QuizFrag
    private void navigateToQuizFragment() {
        QuizFrag quizFrag = new QuizFrag(); // Create an instance of the target fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, quizFrag); // Replace the current fragment with QuizFrag
        transaction.addToBackStack(null); // Optional: Add to back stack to allow back navigation
        transaction.commit();
    }
}
