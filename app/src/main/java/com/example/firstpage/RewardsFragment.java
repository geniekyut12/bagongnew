package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

public class RewardsFragment extends Fragment {

    public RewardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_rewards_fragment, container, false);

        // Find the ImageViews by ID
        ImageView rewardImageFive = view.findViewById(R.id.rewardImagefive);
        ImageView rewardImageTwo = view.findViewById(R.id.rewardImagetwo);
        ImageView rewardImageThree = view.findViewById(R.id.rewardImagethree);

        // Set click listeners for each ImageView
        rewardImageFive.setOnClickListener(v -> {
            // Navigate to RewardFiveActivity
            Intent intent = new Intent(getActivity(), RewardFive.class);
            startActivity(intent);
        });

        rewardImageTwo.setOnClickListener(v -> {
            // Navigate to RewardTwoActivity
            Intent intent = new Intent(getActivity(), RewardTwo.class);
            startActivity(intent);
        });

        rewardImageThree.setOnClickListener(v -> {
            // Navigate to RewardThreeActivity
            Intent intent = new Intent(getActivity(), RewardThree.class);
            startActivity(intent);
        });

        return view;
    }
}
