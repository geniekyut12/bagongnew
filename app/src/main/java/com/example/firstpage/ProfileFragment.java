package com.example.firstpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

        // Set up listeners
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
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 30);
        Date deletionDate = calendar.getTime();

        Map<String, Object> updates = new HashMap<>();
        updates.put("isDeleted", true);
        updates.put("deletionDate", deletionDate);

        disableDeleteAction();

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    titleName.setText("Account Deletion Pending");
                    titleEmail.setText("Restore your account within 30 days.");
                    Toast.makeText(requireContext(), "Account marked for deletion.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    enableDeleteAction();
                    Toast.makeText(requireContext(), "Failed to delete account. Try again.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void disableDeleteAction() {
        deleteAccountButton.setEnabled(false);
    }

    private void enableDeleteAction() {
        deleteAccountButton.setEnabled(true);
    }

    // Log Out Button Handler
    private void handleLogout() {
        // Clear the user session (SharedPreferences)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);  // Set isLoggedIn to false
        editor.apply();  // Apply changes

        // Navigate back to MainActivity (or Signin) after logout
        Intent intent = new Intent(getActivity(), MainActivity.class);  // Navigate to MainActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);  // Clear back stack
        startActivity(intent);
        getActivity().finish();  // Finish ProfileFragment (or Activity)
    }
}
