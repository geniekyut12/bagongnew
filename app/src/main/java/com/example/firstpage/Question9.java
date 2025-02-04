package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Question9 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question9);

        // Find the RadioGroup
        RadioGroup radioGroup = findViewById(R.id.radioGroupQ9);

        // Set listener for RadioGroup selection
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) { // Ensure a valid selection
                Intent intent = new Intent(Question9.this, AfterQuestion.class);
                startActivity(intent);
            }
        });
    }
}