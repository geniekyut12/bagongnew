package com.example.firstpage;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView titleName, logoutButton;
    private Button editButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public ProfileFragment() {
        // Default constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initializeViews(view);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check if the user is logged in and fetch user data
        fetchUserData();

        // Set up listeners for buttons
        setupListeners();
    }

    private void initializeViews(View view) {
        titleName = view.findViewById(R.id.titleName);
        editButton = view.findViewById(R.id.editProfile);
        logoutButton = view.findViewById(R.id.logout);
    }

    private void setupListeners() {
        logoutButton.setOnClickListener(v -> handleLogout());
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            startActivity(intent);
        });
    }

    private void fetchUserData() {
        if (mAuth.getCurrentUser() != null) {
            String username = mAuth.getCurrentUser().getDisplayName(); // Use display name as username

            DocumentReference userDocRef = db.collection("users").document(username); // Query using username
            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String firstName = task.getResult().getString("firstName");

                    if (firstName != null) {
                        titleName.setText(firstName);
                    } else {
                        Log.d("ProfileFragment", "User data is null or empty.");
                        Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("ProfileFragment", "Failed to retrieve user data: " + task.getException());
                    Toast.makeText(getContext(), "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d("ProfileFragment", "No user logged in.");
            Toast.makeText(getContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLogout() {
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();

            // Clear login preferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            Toast.makeText(getContext(), "Successfully logged out", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User logged out successfully");

            // Redirect to login screen
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        } else {
            Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Attempted to log out but no user was logged in");
        }
    }
}
