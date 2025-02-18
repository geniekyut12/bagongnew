package com.example.firstpage;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FootPrintFragment extends Fragment {

    private PieChart pieChart;
    private TextView headerText, co1, co2;

    public FootPrintFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_foot_print_fragment, container, false);

        headerText = view.findViewById(R.id.headerText);
        co1 = view.findViewById(R.id.co1);
        co2 = view.findViewById(R.id.co2);
        pieChart = view.findViewById(R.id.pieChart);

        ImageView arrowButton1 = view.findViewById(R.id.arrowButton1);
        ImageView arrowButton2 = view.findViewById(R.id.arrowButton2);

        fetchUserData();
        fetchCarbonFootprintData();

        arrowButton1.setOnClickListener(v -> {});
        arrowButton2.setOnClickListener(v -> {});

        return view;
    }

    private void fetchUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            headerText.setText("Hello, Guest");
            return;
        }

        String displayName = user.getDisplayName();
        if (displayName != null && !displayName.isEmpty()) {
            headerText.setText("Hello, " + displayName);
        } else {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String firstName = document.getString("firstName");
                                String lastName = document.getString("lastName");

                                if (firstName == null && lastName == null) {
                                    headerText.setText("Hello, User");
                                } else {
                                    String greeting = "Hello, " + (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                                    headerText.setText(greeting.trim());
                                }
                            } else {
                                headerText.setText("Hello, User");
                            }
                        } else {
                            headerText.setText("Failed to load user data");
                        }
                    })
                    .addOnFailureListener(e -> {
                        headerText.setText("Error retrieving data");
                    });
        }
    }



    private void fetchCarbonFootprintData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String username = user.getDisplayName(); // Match stored document ID
        if (username == null || username.isEmpty()) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final double[] transportEmission = {0};
        final double[] foodEmission = {0};

        // Fetch transportation data
        db.collection("transportation").document(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                String transportationDetails = document.getString("transportDetails");
                Double carbonFootprint = parseDouble(document.getString("total_carbon_footprint"));

                if (carbonFootprint != null) {
                    transportEmission[0] = carbonFootprint;
                    co1.setText("Transportation: " + transportationDetails + " - CO2: " + carbonFootprint + " kg");
                }
            }
            updatePieChart(transportEmission[0], foodEmission[0]);
        });

        // Fetch food source data
        db.collection("food_sources").document(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                String foodDetails = document.getString("foodDetails");
                Double foodFootprint = parseDouble(document.getString("total_carbon_footprint"));

                if (foodFootprint != null) {
                    foodEmission[0] = foodFootprint;
                    co2.setText("Food: " + foodDetails + " - CO2: " + foodFootprint + " kg");
                }
            }
            updatePieChart(transportEmission[0], foodEmission[0]);
        });
    }

    private void updatePieChart(double transportEmission, double foodEmission) {
        List<PieEntry> entries = new ArrayList<>();
        if (transportEmission > 0) {
            entries.add(new PieEntry((float) transportEmission, "Transport"));
        }
        if (foodEmission > 0) {
            entries.add(new PieEntry((float) foodEmission, "Food"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Carbon Footprint");
        dataSet.setColors(new int[]{Color.parseColor("#FFA726"), Color.parseColor("#66BB6A")});
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private Double parseDouble(String value) {
        if (value == null || !value.matches(".*\\d.*")) return 0.0;
        return Double.parseDouble(value.replaceAll("[^\\d.]", ""));
    }
}
