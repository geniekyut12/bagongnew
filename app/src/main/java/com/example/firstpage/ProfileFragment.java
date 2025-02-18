package com.example.firstpage;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String PREFS_NAME = "loginPrefs";
    private static final String PREF_IS_LOGGED_IN = "isLoggedIn";
    private TextView titleName;
    private LinearLayout logoutButton, editButton;
    private ImageView profileImg;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Uri imageUri;
    private String username;
    private GoogleSignInOptions gso; // Google SignIn options

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

        initializeViews(view);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Google Sign-In options
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        fetchUserData();
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

        profileImg.setOnClickListener(v -> selectImageSource());
    }

    private void fetchUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d("ProfileFragment", "No user logged in.");
            Toast.makeText(getContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        username = currentUser.getDisplayName();
        if (username == null || username.isEmpty()) {
            username = currentUser.getUid(); // Fallback to UID if display name is not set
        }

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
    }

    private void selectImageSource() {
        if (getContext() == null) return;

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
        if (getActivity() == null) return;

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
                if (getActivity() != null && result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    uploadImageToFirebase(imageUri);
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (getActivity() != null && result.getResultCode() == getActivity().RESULT_OK) {
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
        if (username == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("profileImageUrl", imageUrl);

        db.collection("profile_pictures").document(username)
                .set(data)
                .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update!", Toast.LENGTH_SHORT).show());
    }

    private void handleLogout() {
        // Step 1: Sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // Step 2: Sign out from Google Sign-In (if applicable)
        GoogleSignIn.getClient(getContext(), gso).signOut()
                .addOnCompleteListener(task -> {
                    // Step 3: Clear SharedPreferences
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(PREF_IS_LOGGED_IN, false);
                    editor.apply();

                    // Step 4: Redirect to SignIn activity
                    Intent intent = new Intent(getActivity(), Signin.class);
                    startActivity(intent);

                    // Step 5: Finish the ProfileFragment to prevent going back to it
                    getActivity().finish();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors here
                    Log.e("Google Sign-Out", "Failed to sign out: " + e.getMessage());
                });
    }
}
