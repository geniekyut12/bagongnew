package com.example.firstpage;

// Android imports
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

// AndroidX
import androidx.appcompat.app.AppCompatActivity;

// Google Sign-In
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;

// Firebase
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

// Java utilities
import java.util.HashMap;

public class Signin extends AppCompatActivity {

    private EditText txtEmail, LastPass;
    private Button Loginbtn, google_sign_in_btn,forgotPasswordButton;
    private ProgressBar progressBar;
    private GoogleSignInOptions gso;
    private FirebaseAuth firebase;
    private FirebaseFirestore db;
    private GoogleSignInClient gsc;
    private static final int RC_SIGN_IN = 20;
    private VideoView videoView;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "loginPrefs";
    private static final String PREF_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(PREF_IS_LOGGED_IN, false);

        if (isLoggedIn && !getIntent().hasExtra("skipAutoLogin")) {
            startActivity(new Intent(Signin.this, Loadingpage.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_signin);

        TextView registerTextView = findViewById(R.id.Register);
        registerTextView.setOnClickListener(v -> {
            Intent registerIntent = new Intent(Signin.this, Register.class);
            registerIntent.putExtra("skipAutoLogin", true);
            startActivity(registerIntent);
        });

        txtEmail = findViewById(R.id.txtEmail);
        LastPass = findViewById(R.id.LastPass);
        Loginbtn = findViewById(R.id.btnLogIn);
        progressBar = findViewById(R.id.progressBar);
        google_sign_in_btn = findViewById(R.id.google_sign_in_btn);
        videoView = findViewById(R.id.videoViewBackground);
        forgotPasswordButton = findViewById(R.id.btn_forgot_password);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.mbgblur);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        firebase = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(Signin.this, gso);

        google_sign_in_btn.setOnClickListener(v -> signIn());
        Loginbtn.setOnClickListener(v -> signUpUser());
        forgotPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && account.getIdToken() != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    Log.e("Google Sign-In", "ID Token is null or account is null");
                    Toast.makeText(this, "Google Sign-In failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Log.e("Google Sign-In", "Sign-in failed. Status Code: " + e.getStatusCode(), e);
                Toast.makeText(this, "Google Sign-In failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebase.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebase.getCurrentUser();
                        if (user != null) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", user.getUid());
                            map.put("name", user.getDisplayName());
                            map.put("profile", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
                            db.collection("users").document(user.getUid()).set(map)
                                    .addOnSuccessListener(aVoid -> {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean(PREF_IS_LOGGED_IN, true);
                                        editor.apply();

                                        startActivity(new Intent(Signin.this, Loadingpage.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error saving user data: " + e.getMessage());
                                        Toast.makeText(Signin.this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Log.e("Google Sign-In", "Authentication failed: " + task.getException());
                        Toast.makeText(Signin.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("Google Sign-In", "Sign-In failed: " + e.getMessage());
                    Toast.makeText(Signin.this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void signUpUser() {
        String input = txtEmail.getText().toString().trim();
        String password = LastPass.getText().toString().trim();

        if (TextUtils.isEmpty(input)) {
            txtEmail.setError("Email or username is required.");
            txtEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            LastPass.setError("Password is required.");
            LastPass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            loginWithEmail(input, password);
        } else {
            db.collection("users")
                    .whereEqualTo("username", input)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            String email = task.getResult().getDocuments().get(0).getString("email");
                            loginWithEmail(email, password);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            txtEmail.setError("Username not found.");
                            txtEmail.requestFocus();
                        }
                    });
        }
    }

    private void loginWithEmail(String email, String password) {
        firebase.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebase.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(PREF_IS_LOGGED_IN, true);
                            editor.apply();

                            startActivity(new Intent(Signin.this, Loadingpage.class));
                            finish();
                        } else if (user != null && !user.isEmailVerified()) {
                            Toast.makeText(Signin.this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                            firebase.signOut();
                        } else {
                            Toast.makeText(Signin.this, "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Signin.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resetPassword() {
        String email = txtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Enter your registered email.");
            txtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError("Enter a valid email.");
            txtEmail.requestFocus();
            return;
        }

        sendPasswordResetEmail(email);
    }

    private void sendPasswordResetEmail(String email) {
        progressBar.setVisibility(View.VISIBLE);
        firebase.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(Signin.this, "Reset email sent successfully. Check your inbox.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Signin.this, "Error sending reset email. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}