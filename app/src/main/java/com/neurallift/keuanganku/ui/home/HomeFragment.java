package com.neurallift.keuanganku.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.ui.AccessibleLinearLayout;
import com.neurallift.keuanganku.ui.transaksi.TambahTransaksiBottomSheet;
import com.neurallift.keuanganku.ui.transaksi.TransaksiAdapter;
import com.neurallift.keuanganku.utils.FormatUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView tvSaldoTotal;
    private TextView tvPemasukan;
    private TextView tvPengeluaran;
    private TextView tvEmptyState;
    private RecyclerView rvTransaksi;
    private TransaksiAdapter transaksiAdapter;
    private PieChart pieChart;
    private TabLayout tabLayout;

    private int currentPeriod = 0; // 0: daily, 1: weekly, 2: monthly
    private static final String PREFS_NAME = "PERIOD_PREFS";
    private static final String SELECTED_TAB_KEY = "SelectedTab";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Initialize UI components
        tvSaldoTotal = view.findViewById(R.id.tvSaldoTotal);
        tvPemasukan = view.findViewById(R.id.tvPemasukan);
        tvPengeluaran = view.findViewById(R.id.tvPengeluaran);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        rvTransaksi = view.findViewById(R.id.rvTransaksi);
        pieChart = view.findViewById(R.id.pieChart);
        tabLayout = view.findViewById(R.id.tabLayout);

        // Setup RecyclerView
        transaksiAdapter = new TransaksiAdapter();
        rvTransaksi.setAdapter(transaksiAdapter);
        rvTransaksi.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Setup Floating Action Button
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            TambahTransaksiBottomSheet bottomSheet = new TambahTransaksiBottomSheet();
            bottomSheet.show(getChildFragmentManager(), "tambahTransaksi");
        });

        // Setup Period Tabs
        setupTabLayout();

        // Setup swipe gesture to navigate between periods
        setupSwipeGesture();

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
            loadDataByPeriod();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                SharedPreferences.Editor editor = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putInt(SELECTED_TAB_KEY, tab.getPosition());
                editor.apply();

                currentPeriod = tab.getPosition();
                loadDataByPeriod();
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
        // Observe transactions based on selected period
        switch (currentPeriod) {
            case 0: // Daily
                observeDailyData();
                break;
            case 1: // Weekly
                observeWeeklyData();
                break;
            case 2: // Monthly
                observeMonthlyData();
                break;
        }
    }

    private void observeDailyData() {
        homeViewModel.getTransaksiHarian().observe(getViewLifecycleOwner(), transaksi -> {
            transaksiAdapter.submitList(transaksi);

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
            updateChart();
        });

        homeViewModel.getTotalPengeluaranHarian().observe(getViewLifecycleOwner(), pengeluaran -> {
            tvPengeluaran.setText(FormatUtils.formatCurrency(pengeluaran != null ? pengeluaran : 0));
            updateChart();
        });

        updateSaldoTotal();
    }

    private void observeWeeklyData() {
        homeViewModel.getTransaksiMingguan().observe(getViewLifecycleOwner(), transaksi -> {
            transaksiAdapter.submitList(transaksi);

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
            updateChart();
        });

        homeViewModel.getTotalPengeluaranMingguan().observe(getViewLifecycleOwner(), pengeluaran -> {
            tvPengeluaran.setText(FormatUtils.formatCurrency(pengeluaran != null ? pengeluaran : 0));
            updateChart();
        });

        updateSaldoTotal();
    }

    private void observeMonthlyData() {
        homeViewModel.getTransaksiBulanan().observe(getViewLifecycleOwner(), transaksi -> {
            transaksiAdapter.submitList(transaksi);

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
            updateChart();
        });

        homeViewModel.getTotalPengeluaranBulanan().observe(getViewLifecycleOwner(), pengeluaran -> {
            tvPengeluaran.setText(FormatUtils.formatCurrency(pengeluaran != null ? pengeluaran : 0));
            updateChart();
        });

        updateSaldoTotal();
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

    // Formatter for percentage display in pie chart
    private static class PercentValueFormatter extends com.github.mikephil.charting.formatter.PercentFormatter {

        @Override
        public String getFormattedValue(float value) {
            return FormatUtils.formatPercentage(value);
        }
    }
}
