package com.neurallift.keuanganku;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Akuntansi);
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

        setupNavListener();
    }

    private void setupNavListener() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            int currentId = navController.getCurrentDestination().getId();

            if (id == R.id.navigation_home && currentId != R.id.navigation_home) {
                navController.navigate(R.id.navigation_home, null, getNavOptions());
                return true;
            } else if (id == R.id.navigation_akun && currentId != R.id.navigation_akun) {
                navController.navigate(R.id.navigation_akun, null, getNavOptions());
                return true;
            } else if (id == R.id.navigation_transaksi && currentId != R.id.navigation_transaksi) {
                navController.navigate(R.id.navigation_transaksi, null, getNavOptions());
                return true;
            } else if (id == R.id.navigation_laporan && currentId != R.id.navigation_laporan) {
                navController.navigate(R.id.navigation_laporan, null, getNavOptions());
                return true;
            }
            return false;
        });
    }

    private NavOptions getNavOptions() {
        return new NavOptions.Builder()
                .setEnterAnim(R.anim.zoom_in)
                .setExitAnim(R.anim.zoom_out)
                .setPopEnterAnim(R.anim.zoom_in)
                .setPopExitAnim(R.anim.zoom_out)
                .build();
    }

    public void selectBottomNav(int id) {
        if (bottomNavigationView != null && bottomNavigationView.getSelectedItemId() != id) {
            bottomNavigationView.setOnItemSelectedListener(null); // temporarily disable listener
            bottomNavigationView.setSelectedItemId(id);
            setupNavListener(); // re-attach listener
        }
    }


    public void setSelectedBottomNavItem(int itemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(itemId);
        }
    }

}
