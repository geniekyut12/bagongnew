package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class Question1 extends AppCompatActivity {

    private Button nextbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question1);


        // Initialize and set up "Get Started" button
        nextbtn = findViewById(R.id.button);
        nextbtn.setOnClickListener(v -> {
            Intent intent = new Intent(Question1.this, Question2.class);
            startActivity(intent);
        });

    }
}