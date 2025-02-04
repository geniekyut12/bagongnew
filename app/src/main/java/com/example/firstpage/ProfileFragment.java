package com.example.firstpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private ImageView profileImg;
    private TextView titleName, titleEmail;
    private Button editButton, deleteAccountButton, termsConditionsButton, logoutButton;

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
        profileImg = view.findViewById(R.id.profileImg);
        titleName = view.findViewById(R.id.UserName);
        titleEmail = view.findViewById(R.id.titleEmail);
        editButton = view.findViewById(R.id.editButton);
        deleteAccountButton = view.findViewById(R.id.btndelacc);
        termsConditionsButton = view.findViewById(R.id.btnTAC);
        logoutButton = view.findViewById(R.id.btnLogout);
    }

    private void setupListeners() {
        editButton.setOnClickListener(v -> navigateToActivity(EditProfile.class));
        termsConditionsButton.setOnClickListener(v -> navigateToActivity(ProfileTermsAndCondition.class));
        deleteAccountButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        logoutButton.setOnClickListener(v -> handleLogout());
    }

    private void fetchUserData() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            String username = mAuth.getCurrentUser().getDisplayName(); // Use display name as username

            DocumentReference userDocRef = db.collection("users").document(username); // Query using username
            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String firstName = task.getResult().getString("firstName");
                    String email = task.getResult().getString("email");

                    if (firstName != null && email != null) {
                        titleName.setText(firstName);
                        titleEmail.setText(email);
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



    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(getActivity(), targetActivity);
        startActivity(intent);
    }

    private void showDeleteConfirmationDialog() {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? You have 30 days to restore it before it's permanently deleted.")
                .setPositiveButton("Delete", (dialog, which) -> performAccountDeletion())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performAccountDeletion() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(requireContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        // Add deletion logic here, possibly using Firebase functions to delete user data from Firestore and FirebaseAuth
        mAuth.getCurrentUser().delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                handleLogout(); // Logout after account deletion
            } else {
                Toast.makeText(requireContext(), "Failed to delete account.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLogout() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}