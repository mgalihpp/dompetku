package com.neurallift.keuanganku.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.MainActivity;
import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.ui.AccessibleLinearLayout;
import com.neurallift.keuanganku.ui.transaksi.dialog.TambahTransaksiBottomSheet;
import com.neurallift.keuanganku.ui.transaksi.adapter.TransaksiAdapter;
import com.neurallift.keuanganku.utils.FormatUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView tvSaldoTotal;
    private TextView tvPemasukan;
    private TextView tvPengeluaran;
    private LinearLayout tvEmptyState;
    private RecyclerView rvTransaksi;
    private TransaksiAdapter transaksiAdapter;
    private PieChart pieChart;
    private TabLayout tabLayout;
    private TextView tvDetail;
    private TextView tvLihatSemua;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private int currentPeriod = 0; // 0: daily, 1: weekly, 2: monthly
    private static final String PREFS_NAME = "PERIOD_PREFS";
    private static final String SELECTED_TAB_KEY = "SelectedTab";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        tvSaldoTotal = view.findViewById(R.id.tvSaldoTotal);
        tvPemasukan = view.findViewById(R.id.tvPemasukan);
        tvPengeluaran = view.findViewById(R.id.tvPengeluaran);
        tvEmptyState = view.findViewById(R.id.layout_empty_state);
        rvTransaksi = view.findViewById(R.id.rvTransaksi);
        pieChart = view.findViewById(R.id.pieChart);
        tabLayout = view.findViewById(R.id.tabLayout);
        tvDetail = view.findViewById(R.id.tvDetail);
        tvLihatSemua = view.findViewById(R.id.tvLihatSemua);

        // Setup RecyclerView
        transaksiAdapter = new TransaksiAdapter();
        rvTransaksi.setAdapter(transaksiAdapter);
        rvTransaksi.setItemViewCacheSize(20);
        rvTransaksi.setDrawingCacheEnabled(true);
        rvTransaksi.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rvTransaksi.setLayoutManager(new LinearLayoutManager(requireContext()));

        transaksiAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (positionStart == 0) {
                    rvTransaksi.scrollToPosition(0); // Smooth scroll for new items
                }
            }
        });

        // Setup Floating Action Button
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            TambahTransaksiBottomSheet bottomSheet = new TambahTransaksiBottomSheet();
            bottomSheet.show(getChildFragmentManager(), "tambahTransaksi");
        });

        tvDetail.setOnClickListener(v -> {
            navigateToAkun();
        });

        tvLihatSemua.setOnClickListener(v -> {
            navigateToTransaksi();
        });

        // Setup Period Tabs
        setupTabLayout();

        // Setup swipe gesture to navigate between periods
        setupSwipeGesture();

    }

    @Override
    public void onStart() {
        super.onStart();

        // Load chart
        updateChart();

        // Load saldo total
        updateSaldoTotal();

        // Load data based on current period
        loadDataByPeriod();
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.harian));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.mingguan));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.bulanan));
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedIndex = prefs.getInt(SELECTED_TAB_KEY, 0);
        TabLayout.Tab tab = tabLayout.getTabAt(savedIndex);
        if (tab != null) {
            currentPeriod = savedIndex;
            tab.select();

            executor.execute(() -> loadDataByPeriod());
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                SharedPreferences.Editor editor = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putInt(SELECTED_TAB_KEY, tab.getPosition());
                editor.apply();

                currentPeriod = tab.getPosition();

                // Load data asynchronously
                executor.execute(() -> loadDataByPeriod());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupSwipeGesture() {
        AccessibleLinearLayout mainLayout = getView().findViewById(R.id.home_main_layout);
        mainLayout.setOnSwipeListener(new AccessibleLinearLayout.OnSwipeListener() {
            @Override
            public void onSwipeLeft() {
                if (currentPeriod < 2) {
                    tabLayout.selectTab(tabLayout.getTabAt(currentPeriod + 1));
                }
            }

            @Override
            public void onSwipeRight() {
                if (currentPeriod > 0) {
                    tabLayout.selectTab(tabLayout.getTabAt(currentPeriod - 1));
                }
            }

            @Override
            public void onClick() {
                // Handle click if needed, e.g., show a toast or trigger an action
            }
        });
    }

    private void loadDataByPeriod() {
        // Post to main thread for UI updates
        requireActivity().runOnUiThread(() -> {
            switch (currentPeriod) {
                case 0: observeDailyData(); break;
                case 1: observeWeeklyData(); break;
                case 2: observeMonthlyData();break;
            }
        });
    }

    private void observeDailyData() {
        homeViewModel.getTransaksiHarian().observe(getViewLifecycleOwner(), transaksi -> {
            transaksiAdapter.submitList(transaksi.size() > 30 ? transaksi.subList(0, 30) : transaksi);

            if (transaksi == null || transaksi.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                rvTransaksi.setVisibility(View.GONE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                rvTransaksi.setVisibility(View.VISIBLE);
            }
        });

        homeViewModel.getTotalPemasukanHarian().observe(getViewLifecycleOwner(), pemasukan -> {
            tvPemasukan.setText(FormatUtils.formatCurrency(pemasukan != null ? pemasukan : 0));
        });

        homeViewModel.getTotalPengeluaranHarian().observe(getViewLifecycleOwner(), pengeluaran -> {
            tvPengeluaran.setText(FormatUtils.formatCurrency(pengeluaran != null ? pengeluaran : 0));
        });
    }

    private void observeWeeklyData() {
        homeViewModel.getTransaksiMingguan().observe(getViewLifecycleOwner(), transaksi -> {
            transaksiAdapter.submitList(transaksi.size() > 30 ? transaksi.subList(0, 30) : transaksi);

            if (transaksi == null || transaksi.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                rvTransaksi.setVisibility(View.GONE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                rvTransaksi.setVisibility(View.VISIBLE);
            }
        });

        homeViewModel.getTotalPemasukanMingguan().observe(getViewLifecycleOwner(), pemasukan -> {
            tvPemasukan.setText(FormatUtils.formatCurrency(pemasukan != null ? pemasukan : 0));
        });

        homeViewModel.getTotalPengeluaranMingguan().observe(getViewLifecycleOwner(), pengeluaran -> {
            tvPengeluaran.setText(FormatUtils.formatCurrency(pengeluaran != null ? pengeluaran : 0));
        });
    }

    private void observeMonthlyData() {
        homeViewModel.getTransaksiBulanan().observe(getViewLifecycleOwner(), transaksi -> {
            transaksiAdapter.submitList(transaksi.size() > 30 ? transaksi.subList(0, 30) : transaksi);

            if (transaksi == null || transaksi.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                rvTransaksi.setVisibility(View.GONE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                rvTransaksi.setVisibility(View.VISIBLE);
            }
        });

        homeViewModel.getTotalPemasukanBulanan().observe(getViewLifecycleOwner(), pemasukan -> {
            tvPemasukan.setText(FormatUtils.formatCurrency(pemasukan != null ? pemasukan : 0));
        });

        homeViewModel.getTotalPengeluaranBulanan().observe(getViewLifecycleOwner(), pengeluaran -> {
            tvPengeluaran.setText(FormatUtils.formatCurrency(pengeluaran != null ? pengeluaran : 0));
        });
    }

    private void updateSaldoTotal() {
        homeViewModel.getTotalPemasukan().observe(getViewLifecycleOwner(), pemasukan -> {
            double totalPemasukan = pemasukan != null ? pemasukan : 0;

            homeViewModel.getTotalPengeluaran().observe(getViewLifecycleOwner(), pengeluaran -> {
                double totalPengeluaran = pengeluaran != null ? pengeluaran : 0;
                double saldo = totalPemasukan - totalPengeluaran;

                tvSaldoTotal.setText(FormatUtils.formatCurrency(saldo));
            });
        });
    }

    private void updateChart() {
        homeViewModel.getTotalPemasukan().observe(getViewLifecycleOwner(), pemasukan -> {
            double totalPemasukan = pemasukan != null ? pemasukan : 0;

            homeViewModel.getTotalPengeluaran().observe(getViewLifecycleOwner(), pengeluaran -> {
                double totalPengeluaran = pengeluaran != null ? pengeluaran : 0;

                setupPieChart(totalPemasukan, totalPengeluaran);
            });
        });
    }

    private void setupPieChart(double pemasukan, double pengeluaran) {
        List<PieEntry> entries = new ArrayList<>();

        if (pemasukan > 0) {
            entries.add(new PieEntry((float) pemasukan, getString(R.string.pemasukan)));
        }

        if (pengeluaran > 0) {
            entries.add(new PieEntry((float) pengeluaran, getString(R.string.pengeluaran)));
        }

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, ""));
            pieChart.setNoDataText(getString(R.string.tidak_ada_data));
            pieChart.invalidate();
            return;
        } else {
            pieChart.setVisibility(View.VISIBLE);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{
            getResources().getColor(R.color.colorPieIncome),
            getResources().getColor(R.color.colorPieExpense)
        });

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(getResources().getColor(R.color.colorOnBackground));
        data.setValueFormatter(new PercentValueFormatter());

        pieChart.setData(data);
        pieChart.setDescription(null);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(getResources().getColor(R.color.colorOnBackground));
        pieChart.setCenterText(getString(R.string.keuangan));
        pieChart.setCenterTextSize(16f);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void navigateToAkun(){
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.navigation_akun, null, getNavOptions());

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectBottomNav(R.id.navigation_akun);
        }
    }

    private void navigateToTransaksi(){
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.navigation_transaksi, null, getNavOptions());

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectBottomNav(R.id.navigation_transaksi);
        }
    }

    private NavOptions getNavOptions() {
        return new NavOptions.Builder()
                .setEnterAnim(R.anim.enter_from_right)
                .setExitAnim(R.anim.exit_to_left)
                .setPopEnterAnim(R.anim.enter_from_left)
                .setPopExitAnim(R.anim.exit_to_right)
                .build();
    }

    // Formatter for percentage display in pie chart
    private static class PercentValueFormatter extends com.github.mikephil.charting.formatter.PercentFormatter {

        @Override
        public String getFormattedValue(float value) {
            return FormatUtils.formatPercentage(value);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
