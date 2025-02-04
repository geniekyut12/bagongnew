package com.example.firstpage;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Stack;

public class navbar extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private final Stack<Integer> fragmentStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // Load the default fragment (HomeFragment)
        if (savedInstanceState == null) {
            loadFragment(new homepage(), R.id.nav_home);
        }

        // Handle bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Avoid reloading the current fragment
            if (!fragmentStack.isEmpty() && fragmentStack.peek() == itemId) {
                return true;
            }

            Fragment selectedFragment = null;
            if (itemId == R.id.nav_home) {
                selectedFragment = new homepage();
            } else if (itemId == R.id.nav_footprint) {
                selectedFragment = new FootPrintFragment();
            } else if (itemId == R.id.nav_games) {
                selectedFragment = new GameFragment();
            } else if (itemId == R.id.nav_community) {
                selectedFragment = new CommunityFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, itemId);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment, int itemId) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        // Add the selected fragment to the stack
        if (!fragmentStack.isEmpty() && fragmentStack.peek() == itemId) {
            return; // Prevent duplicate entries
        }
        fragmentStack.push(itemId);
    }

    @Override
    public void onBackPressed() {
        if (fragmentStack.size() > 1) {
            fragmentStack.pop(); // Remove the current fragment
            int previousItemId = fragmentStack.peek();

            Fragment previousFragment = null;
            if (previousItemId == R.id.nav_home) {
                previousFragment = new homepage();
            } else if (previousItemId == R.id.nav_footprint) {
                previousFragment = new FootPrintFragment();
            } else if (previousItemId == R.id.nav_games) {
                previousFragment = new GameFragment();
            } else if (previousItemId == R.id.nav_community) {
                previousFragment = new CommunityFragment();
            } else if (previousItemId == R.id.nav_profile) {
                previousFragment = new ProfileFragment();
            }

            if (previousFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, previousFragment)
                        .commit();
                bottomNavigationView.setSelectedItemId(previousItemId);
            }
        } else {
            super.onBackPressed();
        }
    }
}
