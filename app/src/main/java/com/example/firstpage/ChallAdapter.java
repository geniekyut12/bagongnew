package com.example.firstpage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.firstpage.Fragment.Breakfast;
import com.example.firstpage.Fragment.DoneFragment;

public class ChallAdapter extends FragmentStateAdapter {

    public ChallAdapter(@NonNull ChallengeFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new Breakfast();
            case 1:
                return new DoneFragment();
            default:
                return new Breakfast();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}