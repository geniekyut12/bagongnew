package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {

    private Button btnTrackTrip;

    private LinearLayout linearLayoutBf, linearLayoutS, linearLayoutI, linearLayoutW, linearLayoutE,
            linearLayoutJ, linearLayoutB, linearLayoutEnvi;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_homepage, container, false);

        // Initialize views from the layout
        btnTrackTrip = view.findViewById(R.id.btnTrackTrip);

        // Initialize the LinearLayouts for food items


        linearLayoutS = view.findViewById(R.id.linearLayoutSphere);
        linearLayoutI = view.findViewById(R.id.linearLayoutIce);
        linearLayoutW = view.findViewById(R.id.linearLayoutWeather);
        linearLayoutE = view.findViewById(R.id.linearLayoutExp);
        linearLayoutJ = view.findViewById(R.id.linearLayoutJar);
        linearLayoutB = view.findViewById(R.id.linearLayoutBarrier);
        linearLayoutEnvi = view.findViewById(R.id.linearLayoutEnvironment);

        btnTrackTrip.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Transportation.class);
            startActivity(intent);
        });


        linearLayoutS.setOnClickListener(v -> startNewActivity(Sphere.class));
        linearLayoutI.setOnClickListener(v -> startNewActivity(Ice.class));
        linearLayoutW.setOnClickListener(v -> startNewActivity(Weather.class));
        linearLayoutE.setOnClickListener(v -> startNewActivity(Exp.class));
        linearLayoutJ.setOnClickListener(v -> startNewActivity(Jar.class));
        linearLayoutB.setOnClickListener(v -> startNewActivity(Barrier.class));
        linearLayoutEnvi.setOnClickListener(v -> startNewActivity(Environment.class));

        return view;
    }

    // Method to replace fragments
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment); // Ensure you have a container in your activity XML
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Helper method to start an activity from the fragment
    private void startNewActivity(Class<?> activityClass) {
        Intent intent = new Intent(requireActivity(), activityClass); // Use requireActivity to ensure valid context
        startActivity(intent);
    }
}