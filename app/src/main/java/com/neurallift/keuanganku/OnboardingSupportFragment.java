package com.neurallift.keuanganku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingSupportFragment extends Fragment {

    private ViewPager2 viewPager;
    private Button skipButton;
    private Button nextButton;
    private TabLayout tabLayout;

    // Content for onboarding screens
    private static final int[] TITLES = {
        R.string.onboarding_title_1,
        R.string.onboarding_title_2,
        //R.string.onboarding_title_3,
        R.string.onboarding_title_4
    };

    private static final int[] DESCRIPTIONS = {
        R.string.onboarding_desc_1,
        R.string.onboarding_desc_2,
        //R.string.onboarding_desc_3,
        R.string.onboarding_desc_4
    };

    private static final int[] IMAGES = {
        R.drawable.ic_onboarding_5,
        R.drawable.ic_onboarding_2,
        //R.drawable.ic_onboarding_3,
        R.drawable.ic_onboarding_4
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);
        skipButton = view.findViewById(R.id.skipButton);
        nextButton = view.findViewById(R.id.nextButton);
        tabLayout = view.findViewById(R.id.tabLayout);

        // Set up the adapter for ViewPager
        OnboardingAdapter adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        // Set up tab layout with viewpager
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    // No text for tabs, just indicators
                    tab.setIcon(R.drawable.tab_selector);
                }).attach();

        // Skip button click listener
        skipButton.setOnClickListener(v -> goToMainActivity());

        // Next button click listener
        nextButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < TITLES.length - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                goToMainActivity();
            }
        });

        // Page change listener to update button text
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == TITLES.length - 1) {
                    nextButton.setText(R.string.selesai);
                } else {
                    nextButton.setText(R.string.lanjut);
                }

                // Show skip button only if not on the last page
                skipButton.setVisibility(position == TITLES.length - 1 ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void goToMainActivity() {
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit();

        sharedPreferencesEditor.putBoolean("first_launch", true);
        sharedPreferencesEditor.apply();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    // ViewPager adapter
    private class OnboardingAdapter extends FragmentStateAdapter {

        public OnboardingAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return OnboardingPageFragment.newInstance(
                    TITLES[position],
                    DESCRIPTIONS[position],
                    IMAGES[position]
            );
        }

        @Override
        public int getItemCount() {
            return TITLES.length;
        }
    }

    // Fragment for each onboarding page
    public static class OnboardingPageFragment extends Fragment {

        private static final String ARG_TITLE = "title";
        private static final String ARG_DESCRIPTION = "description";
        private static final String ARG_IMAGE = "image";

        public static OnboardingPageFragment newInstance(int titleResId, int descriptionResId, int imageResId) {
            OnboardingPageFragment fragment = new OnboardingPageFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_TITLE, titleResId);
            args.putInt(ARG_DESCRIPTION, descriptionResId);
            args.putInt(ARG_IMAGE, imageResId);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.item_onboarding, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            Bundle args = getArguments();
            if (args != null) {
                TextView titleTextView = view.findViewById(R.id.titleTextView);
                TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);
                ImageView imageView = view.findViewById(R.id.imageView);

                titleTextView.setText(args.getInt(ARG_TITLE));
                descriptionTextView.setText(args.getInt(ARG_DESCRIPTION));
                imageView.setImageResource(args.getInt(ARG_IMAGE));
            }
        }
    }
}
