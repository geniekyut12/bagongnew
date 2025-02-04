package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Question1 extends AppCompatActivity {

    private Button nextBtn;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question1);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        radioGroup = findViewById(R.id.radioGroupQ1);
        nextBtn = findViewById(R.id.btn1);
    }

    private void setupListeners() {
        // Disable further selection after the first selection
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                // Disable all RadioButtons after the first selection
                for (int i = 0; i < group.getChildCount(); i++) {
                    group.getChildAt(i).setEnabled(true);
                }
            }
        });

        // Navigate to next activity on button click
        nextBtn.setOnClickListener(v -> navigateToNext());
    }

    private void navigateToNext() {
        Intent intent = new Intent(Question1.this, Question2.class);
        startActivity(intent);
    }
}
