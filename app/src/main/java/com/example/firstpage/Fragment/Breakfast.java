package com.example.firstpage.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.firstpage.Chall1;
import com.example.firstpage.R;

public class Breakfast extends Fragment {

    private Button bfast1;
    private Button bfast2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_in_progress, container, false);

        // Initialize the button
        bfast1 = view.findViewById(R.id.btnStartChallenge1);

        // Set the click listener
        bfast1.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), Chall1.class);
            startActivity(intent);
        });


        bfast2 = view.findViewById(R.id.btnStartChallenge2);

        // Set the click listener
        bfast2.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), Chall1.class);
            startActivity(intent);
        });

        return view;
    }
}
