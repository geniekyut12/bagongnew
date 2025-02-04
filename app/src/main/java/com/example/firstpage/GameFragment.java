package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GameFragment extends Fragment {

    private TextView tvDate, tvMonthYear, tvStreakCount, tvAttendanceStatus;
    private ImageView ivCheckmark;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        // Update Date
        updateDate();

        // Simulate Streak Count
        tvStreakCount.setText("5-Day Streak"); // Change dynamically based on user data

        // Simulate Attendance Status
        tvAttendanceStatus.setText("Attendance Checked ✅");
        ivCheckmark.setImageResource(R.drawable.check);

        // Set Click Listeners for Buttons
        view.findViewById(R.id.gameB).setOnClickListener(v -> openActivity(Game.class));
        view.findViewById(R.id.quizB).setOnClickListener(v -> openActivity(Quiz.class));
        view.findViewById(R.id.feature1B).setOnClickListener(v -> openActivity(Video.class));
        view.findViewById(R.id.feature2B).setOnClickListener(v -> openActivity(LeaderBoards.class));
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
