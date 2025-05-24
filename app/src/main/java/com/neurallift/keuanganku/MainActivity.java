package com.neurallift.keuanganku;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Check if we need to display our onboarding
        if (!sharedPreferences.getBoolean("first_launch", false)) {
            setContentView(R.layout.activity_onboarding);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new OnboardingSupportFragment())
                    .commit();
        } else {
            setupMainScreen();
        }
    }

    private void setupMainScreen() {
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = NavHostFragment.findNavController(
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)
        );

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

}
