package com.example.firstpage; // Replace with your actual package name

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class RewardFive extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_five); // Replace with your actual layout name

        // Find the back button
        ImageView backButton = findViewById(R.id.backButton);

        // Set an onClickListener to the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and return to the previous one
                onBackPressed(); // Alternatively, you can use finish();
            }
        });
    }
}
