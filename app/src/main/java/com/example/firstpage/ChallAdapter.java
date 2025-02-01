package com.example.firstpage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.firstpage.Fragment.DoneFragment;
import com.example.firstpage.Fragment.InProgressFragment;

public class ChallAdapter extends FragmentStateAdapter {

    public ChallAdapter(@NonNull ChallengeFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new InProgressFragment();
            case 1:
                return new DoneFragment();
            default:
                return new InProgressFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}