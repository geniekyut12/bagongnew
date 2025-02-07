package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class Game1Tut extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1_tut);

        // Start GameActivity when "g1start" button is clicked
        Button startButton = findViewById(R.id.g1start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGameActivity();
            }
        });

        // Go back to Game.class when "g1back1" ImageButton is clicked
        ImageButton backButton = findViewById(R.id.g1back1);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGameScreen();
            }
        });
    }

    private void openGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void openGameScreen() {
        Intent intent = new Intent(this, Game.class); // Ensure Game.class exists
        startActivity(intent);
    }
}
