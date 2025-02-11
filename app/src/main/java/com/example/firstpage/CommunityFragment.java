package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CommunityFragment extends Fragment {

    private ImageView profileImage;
    private EditText searchEditText;
    private LinearLayout masEnt, waniEnt, playEnt;

    public CommunityFragment() {
        super(R.layout.fragment_community); // Ensure this matches your XML layout filename
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        profileImage = view.findViewById(R.id.profileImage);
        searchEditText = view.findViewById(R.id.searchEditText);
        masEnt = view.findViewById(R.id.MasEnt);
        waniEnt = view.findViewById(R.id.WaniEnt);
        playEnt = view.findViewById(R.id.PlayEnt);

        // Click listener for profile image
        profileImage.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Profile clicked!", Toast.LENGTH_SHORT).show();
            // Add navigation logic if needed (e.g., go to Profile Activity)
        });

        // Click listeners for enterprise items
        masEnt.setOnClickListener(v -> startNewActivity(Masugid_Ent.class));
        waniEnt.setOnClickListener(v -> startNewActivity(Wani_Ent.class));
        playEnt.setOnClickListener(v -> startNewActivity(PlayMaker_Ent.class));
    }

    // Generic method to navigate to a new activity
    private void startNewActivity(Class<?> destinationClass) {
        Intent intent = new Intent(getActivity(), destinationClass);
        startActivity(intent);
    }
}
