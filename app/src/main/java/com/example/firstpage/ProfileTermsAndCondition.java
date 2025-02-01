package com.example.firstpage;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileTermsAndCondition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_terms_and_condition); // Replace with your actual layout name

        // Find the back button and set the click listener
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the previous screen when the back button is clicked
                onBackPressed(); // Alternatively, use finish() to close the current activity
            }
        });

        // Set the toolbar title
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(getString(R.string.termsandcondition)); // Set the title text (using string resource)

        // Find the scroll view to make sure it is scrollable
        ScrollView scrollView = findViewById(R.id.scrollView);
        // Optionally, you can set any properties or listeners on the scroll view if needed
    }
}
