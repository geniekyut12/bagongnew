package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfile extends AppCompatActivity {
    private EditText editFirstName, editLastName, editEmail, editUsername;
    private Button saveButton, resetPasswordButton;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String username, firstName, lastName, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        // Initialize UI elements
        editFirstName = findViewById(R.id.firstNameInput);
        editLastName = findViewById(R.id.lastNameInput);
        editEmail = findViewById(R.id.emailInput);
        editUsername = findViewById(R.id.usernameEdit);
        saveButton = findViewById(R.id.saveButton);
        resetPasswordButton = findViewById(R.id.inputpass);
        progressBar = findViewById(R.id.progressBar); // Ensure this exists in XML

        // Check if user is logged in
        if (currentUser == null) {
            showToast("User not logged in");
            finish();
            return;
        }

        username = currentUser.getDisplayName(); // Get username from Firebase Auth
        fetchUserData(username);

        saveButton.setOnClickListener(v -> saveProfileChanges());
        resetPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void fetchUserData(String username) {
        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                firstName = documentSnapshot.getString("firstName");
                lastName = documentSnapshot.getString("lastName");
                email = documentSnapshot.getString("email");

                // Populate EditTexts
                if (firstName != null) editFirstName.setText(firstName);
                if (lastName != null) editLastName.setText(lastName);
                if (email != null) editEmail.setText(email);
                if (username != null) editUsername.setText(username);
            } else {
                showToast("User data not found");
                finish();
            }
        }).addOnFailureListener(e -> {
            showToast("Failed to fetch user data: " + e.getMessage());
            finish();
        });
    }

    private void saveProfileChanges() {
        String updatedFirstName = editFirstName.getText().toString().trim();
        String updatedLastName = editLastName.getText().toString().trim();
        String updatedEmail = editEmail.getText().toString().trim();
        String updatedUsername = editUsername.getText().toString().trim();

        if (isDataUnchanged(updatedFirstName, updatedLastName, updatedEmail, updatedUsername)) {
            showToast("No changes to save");
            return;
        }

        DocumentReference docRef = db.collection("users").document(username);
        docRef.update(
                        "firstName", updatedFirstName,
                        "lastName", updatedLastName,
                        "email", updatedEmail,
                        "username", updatedUsername
                ).addOnSuccessListener(aVoid -> finishWithSuccess(updatedFirstName, updatedLastName, updatedEmail, updatedUsername))
                .addOnFailureListener(e -> showToast("Failed to update profile"));
    }

    private boolean isDataUnchanged(String fName, String lName, String mail, String uName) {
        return fName.equals(firstName) && lName.equals(lastName) && mail.equals(email) && uName.equals(username);
    }

    private void finishWithSuccess(String fName, String lName, String mail, String uName) {
        showToast("Profile Updated");
        Intent resultIntent = new Intent();
        resultIntent.putExtra("firstName", fName);
        resultIntent.putExtra("lastName", lName);
        resultIntent.putExtra("email", mail);
        resultIntent.putExtra("username", uName);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void resetPassword() {
        String email = editEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Enter your registered email.");
            editEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Enter a valid email.");
            editEmail.requestFocus();
            return;
        }

        sendPasswordResetEmail(email);
    }

    private void sendPasswordResetEmail(String email) {
        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        showToast("Reset email sent successfully. Check your inbox.");
                        redirectToProfileFragment();
                    } else {
                        showToast("Error sending reset email. Try again.");
                    }
                });
    }

    private void redirectToProfileFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new ProfileFragment()) // Ensure you have a valid container ID
                .addToBackStack(null) // Optional: Allows back navigation
                .commit();
    }



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
