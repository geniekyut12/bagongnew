package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Question6 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question6);

        // Find the RadioGroup
        RadioGroup radioGroup = findViewById(R.id.radioGroupQ6);

        // Set listener for RadioGroup selection
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) { // Ensure a valid selection
                Intent intent = new Intent(Question6.this, Question7.class);
                startActivity(intent);
            }
        });
    }
}