package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Question1 extends AppCompatActivity {

    private Button nextBtn, bmicalculate;
    private RadioGroup radioGroup;
    private TextInputEditText txtHeight, txtWeight;
    private TextView bmiResult, bmiStatus, txtFirstName, txtUsername;
    private ImageView bmiStatusImage;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question1);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        fetchUserData(); // Fetch first name and username from Firestore
        setupListeners();
    }

    private void initializeViews() {
        radioGroup = findViewById(R.id.radioGroupQ1);
        nextBtn = findViewById(R.id.btn1);
        bmicalculate = findViewById(R.id.btn_bmicalculate);
        txtHeight = findViewById(R.id.txtHeight);
        txtWeight = findViewById(R.id.txtWeight);
        bmiResult = findViewById(R.id.BMI);
        bmiStatus = findViewById(R.id.bmistatus);
        bmiStatusImage = findViewById(R.id.BMIimage);
        txtFirstName = findViewById(R.id.FetchFName);
        txtUsername = findViewById(R.id.FetchUName);

        // Add validation for weight input (max 150)
        txtWeight.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    try {
                        float weight = Float.parseFloat(s.toString());
                        if (weight > 150) {
                            txtWeight.setText("150"); // Set max value
                            txtWeight.setSelection(txtWeight.getText().length()); // Move cursor to the end
                        }
                    } catch (NumberFormatException e) {
                        txtWeight.setText(""); // Clear invalid input
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        txtHeight.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    try {
                        float weight = Float.parseFloat(s.toString());
                        if (weight > 250) {
                            txtHeight.setText("250"); // Set max value
                            txtHeight.setSelection(txtHeight.getText().length()); // Move cursor to the end
                        }
                    } catch (NumberFormatException e) {
                        txtHeight.setText(""); // Clear invalid input
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupListeners() {
        nextBtn.setOnClickListener(v -> navigateToNext());
        bmicalculate.setOnClickListener(v -> calculateAndSaveBMI());
    }

    private void fetchUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = user.getEmail();
        if (userEmail == null) {
            Toast.makeText(this, "Failed to get email", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String firstName = document.getString("firstName");
                            String username = document.getString("username");

                            txtFirstName.setText(firstName != null ? "Firstname: " + firstName : "Firstname: N/A");
                            txtUsername.setText(username != null ? "Username: " + username : "Username: N/A");
                        }
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching user data", e);
                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                });
    }

    private void calculateAndSaveBMI() {
        String heightStr = txtHeight.getText().toString().trim();
        String weightStr = txtWeight.getText().toString().trim();

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please enter both height and weight", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double height = Double.parseDouble(heightStr) / 100; // Convert cm to meters
            double weight = Double.parseDouble(weightStr);

            if (height <= 0 || weight <= 0) {
                Toast.makeText(this, "Invalid input values", Toast.LENGTH_SHORT).show();
                return;
            }

            double bmi = weight / (height * height);
            bmiResult.setText(String.format(Locale.US, "BMI: %.2f", bmi));

            String status;
            int imageResId;
            if (bmi < 18.5) {
                imageResId = R.drawable.underw;
                status = "Underweight";
            } else if (bmi < 24.9) {
                imageResId = R.drawable.normal_weight;
                status = "Normal weight";
            } else if (bmi < 29.9) {
                imageResId = R.drawable.overweight;
                status = "Overweight";
            } else {
                imageResId = R.drawable.obese;
                status = "Obese";
            }

            bmiStatusImage.setImageResource(imageResId);
            bmiStatus.setText(status);

            saveBMIData(bmi, status);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBMIData(double bmi, String status) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = user.getEmail();
        if (userEmail == null) {
            Toast.makeText(this, "Failed to get email", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String username = queryDocumentSnapshots.getDocuments().get(0).getString("username");

                        if (username == null || username.isEmpty()) {
                            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Map<String, Object> bmiData = new HashMap<>();
                        bmiData.put("bmiValue", bmi);
                        bmiData.put("bmiStatus", status);
                        bmiData.put("timestamp", System.currentTimeMillis());

                        db.collection("bmi_records").document(username)
                                .set(bmiData)
                                .addOnSuccessListener(aVoid -> Toast.makeText(Question1.this, "BMI data saved!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> {
                                    Log.e("FirestoreError", "Error saving BMI data", e);
                                    Toast.makeText(Question1.this, "Failed to save BMI", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching username", e);
                    Toast.makeText(this, "Failed to fetch username", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToNext() {
        startActivity(new Intent(Question1.this, Question2.class));
    }
}
