package com.example.firstpage;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class RewardTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_two); // Replace with your actual layout name

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