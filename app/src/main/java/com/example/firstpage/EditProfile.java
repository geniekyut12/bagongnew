package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private EditText editName, editEmail, editUname, editPass;
    private Button saveButton;
    private String nameUser, emailUser, Username, Pass;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        editName = findViewById(R.id.firstNameInput);
        editEmail = findViewById(R.id.emailInput);
        editUname = findViewById(R.id.usernameEdit);
        editPass = findViewById(R.id.inputpass);
        saveButton = findViewById(R.id.saveButton);

        // Retrieve the data passed from ProfileFragment
        Intent intent = getIntent();
        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        Username = intent.getStringExtra("Username");
        Pass = intent.getStringExtra("Password");

        // Set the data into EditText fields
        editName.setText(nameUser);
        editEmail.setText(emailUser);
        editUname.setText(Username);
        editPass.setText(Pass);

        // Save Button Click Listener
        saveButton.setOnClickListener(v -> {
            // Get the updated data
            String updatedName = editName.getText().toString();
            String updatedEmail = editEmail.getText().toString();
            String updatedUName = editUname.getText().toString();
            String updatedPass = editPass.getText().toString();

            // Check if any field was changed
            if (!nameUser.equals(updatedName) || !emailUser.equals(updatedEmail) || !Username.equals(updatedUName) || !Pass.equals(updatedPass)) {

                // Create a map of updated values
                Map<String, Object> userUpdates = new HashMap<>();
                userUpdates.put("name", updatedName);
                userUpdates.put("email", updatedEmail);
                userUpdates.put("username", updatedUName);
                userUpdates.put("password", updatedPass);

                // If username is changed, delete old document and create a new one
                if (!Username.equals(updatedUName)) {
                    db.collection("users").document(Username).delete()
                            .addOnSuccessListener(aVoid -> {
                                // Create new document with updated username
                                db.collection("users").document(updatedUName)
                                        .set(userUpdates, SetOptions.merge())
                                        .addOnSuccessListener(aVoid2 -> {
                                            showToastAndFinish(updatedName, updatedEmail, updatedUName, updatedPass);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> Toast.makeText(EditProfile.this, "Error updating username", Toast.LENGTH_SHORT).show());
                } else {
                    // Just update existing document if username is unchanged
                    db.collection("users").document(Username)
                            .set(userUpdates, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                showToastAndFinish(updatedName, updatedEmail, updatedUName, updatedPass);
                            })
                            .addOnFailureListener(e -> Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show());
                }
            } else {
                Toast.makeText(EditProfile.this, "No changes to save", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToastAndFinish(String updatedName, String updatedEmail, String updatedUName, String updatedPass) {
        Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedName", updatedName);
        resultIntent.putExtra("updatedEmail", updatedEmail);
        resultIntent.putExtra("updatedUName", updatedUName);
        resultIntent.putExtra("updatedPass", updatedPass);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
