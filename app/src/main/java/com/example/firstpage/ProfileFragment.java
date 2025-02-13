package com.example.firstpage;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView titleName;
    private LinearLayout logoutButton,editButton;

    private ImageView profileImg;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Uri imageUri;
    private String username;

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

        // Fetch user data
        fetchUserData();

        // Set up listeners
        setupListeners();
    }

    private void initializeViews(View view) {
        titleName = view.findViewById(R.id.titleName);
        editButton = view.findViewById(R.id.editProfile);
        logoutButton = view.findViewById(R.id.btnlogout);
        profileImg = view.findViewById(R.id.profileImg);
    }

    private void setupListeners() {
        logoutButton.setOnClickListener(v -> handleLogout());
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            startActivity(intent);
        });

        // Profile image click listener for uploading
        profileImg.setOnClickListener(v -> selectImageSource());
    }

    private void fetchUserData() {
        if (mAuth.getCurrentUser() != null) {
            username = mAuth.getCurrentUser().getDisplayName(); // Use display name as username

            DocumentReference userDocRef = db.collection("users").document(username);
            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String firstName = task.getResult().getString("firstName");

                    if (firstName != null) {
                        titleName.setText(firstName);
                    }
                } else {
                    Log.d("ProfileFragment", "User data not found.");
                    Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d("ProfileFragment", "No user logged in.");
            Toast.makeText(getContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImageSource() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose an option")
                .setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void openCamera() {
        File photoFile = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile.jpg");
        imageUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".provider", photoFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraLauncher.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    uploadImageToFirebase(imageUri);
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    uploadImageToFirebase(imageUri);
                }
            }
    );

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri == null || username == null) return;

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images/" + username + ".jpg");
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveImageUrlToFirestore(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Upload failed!", Toast.LENGTH_SHORT).show());
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("profileImageUrl", imageUrl);

        db.collection("profile_pictures").document(username)
                .set(data)
                .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update!", Toast.LENGTH_SHORT).show());
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
