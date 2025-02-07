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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GameFragment extends Fragment {

    private TextView tvDate, tvMonthYear, tvStreakCount, tvAttendanceStatus;
    private ImageView ivCheckmark;
    private int streakCount = 0;  // Variable to store the streak count
    private SharedPreferences sharedPreferences;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI Elements
        tvDate = view.findViewById(R.id.tvDate);
        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        tvStreakCount = view.findViewById(R.id.tvStreakCount);
        tvAttendanceStatus = view.findViewById(R.id.tvAttendanceStatus);
        ivCheckmark = view.findViewById(R.id.ivCheckmark);

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);

        // Retrieve saved streak count and last marked date
        streakCount = sharedPreferences.getInt("streakCount", 0);
        String lastMarkedDate = sharedPreferences.getString("lastMarkedDate", "");

        // Update UI with saved data
        tvStreakCount.setText(String.format(Locale.getDefault(), "%d - Day Streak", streakCount));

        // Update Date
        updateDate();

        // Set Click Listeners for Buttons
        view.findViewById(R.id.gameB).setOnClickListener(v -> {
            if (getActivity() != null) {
                openActivity(Game.class);  // Start Game activity
            }
        });
        view.findViewById(R.id.quizB).setOnClickListener(v -> replaceFragment(new QuizFrag()));
        view.findViewById(R.id.feature1B).setOnClickListener(v -> replaceFragment(new VideoFrag()));
        view.findViewById(R.id.feature2B).setOnClickListener(v -> replaceFragment(new LeaderBordsFrag()));

        // Mark attendance button
        view.findViewById(R.id.btnMarkAttendance).setOnClickListener(this::markAttendance);

        // Check if it's a new day and reset attendance button if necessary
        checkAndResetAttendance(lastMarkedDate);
    }

    private void markAttendance(View view) {
        // Show the checkmark icon to indicate attendance has been marked
        ivCheckmark.setVisibility(View.VISIBLE); // Make the checkmark visible

        // Update the status text
        tvAttendanceStatus.setText("Attendance Marked");

        // Increment the streak count
        streakCount++;

        // Update the streak count in the UI
        tvStreakCount.setText(String.format(Locale.getDefault(), "%d - Day Streak", streakCount));

        // Save the updated streak count and today's date in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("streakCount", streakCount);
        editor.putString("lastMarkedDate", getCurrentDate());
        editor.apply();

        // Disable the button or set low opacity
        Button btnMarkAttendance = getView().findViewById(R.id.btnMarkAttendance);
        btnMarkAttendance.setEnabled(false);  // Disable the button
        btnMarkAttendance.setAlpha(0.5f);    // Set low opacity
    }

    private void checkAndResetAttendance(String lastMarkedDate) {
        // If the last marked date is not today's date, reset the attendance button
        if (!getCurrentDate().equals(lastMarkedDate)) {
            Button btnMarkAttendance = getView().findViewById(R.id.btnMarkAttendance);
            btnMarkAttendance.setEnabled(true);  // Enable the button
            btnMarkAttendance.setAlpha(1.0f);   // Restore full opacity
            ivCheckmark.setVisibility(View.GONE);  // Hide the checkmark
            tvAttendanceStatus.setText("");  // Clear attendance status
        }
    }

    private String getCurrentDate() {
        // Get the current date in a format (e.g., "dd/MM/yyyy")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void updateDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());

        String todayDate = new SimpleDateFormat("dd", Locale.getDefault()).format(calendar.getTime());
        String monthYear = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.getTime());

        tvDate.setText(todayDate);
        tvMonthYear.setText(monthYear);
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(getActivity(), activityClass);
        startActivity(intent);
    }
}
