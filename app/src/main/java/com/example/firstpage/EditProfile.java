package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {

    private EditText editName, editEmail;
    private Button saveButton;
    private String nameUser, emailUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        editName = findViewById(R.id.firstNameInput);
        editEmail = findViewById(R.id.emailInput);
        saveButton = findViewById(R.id.saveButton);

        // Retrieve the data passed from ProfileFragment
        Intent intent = getIntent();
        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");

        // Set the data into EditText fields
        editName.setText(nameUser);
        editEmail.setText(emailUser);

        // Save Button Click Listener
        saveButton.setOnClickListener(v -> {
            // Get the updated data
            String updatedName = editName.getText().toString();
            String updatedEmail = editEmail.getText().toString();

            // Check if any field was changed
            if (!nameUser.equals(updatedName) || !emailUser.equals(updatedEmail)) {
                nameUser = updatedName;
                emailUser = updatedEmail;

                // Show Toast and return to ProfileFragment
                Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedName", nameUser);
                resultIntent.putExtra("updatedEmail", emailUser);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                // No changes detected
                Toast.makeText(EditProfile.this, "No changes to save", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
