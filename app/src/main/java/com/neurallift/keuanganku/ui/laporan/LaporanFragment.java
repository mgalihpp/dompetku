package com.neurallift.keuanganku.ui.laporan;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.utils.DateTimeUtils;
import com.neurallift.keuanganku.utils.FormatUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LaporanFragment extends Fragment {

    private LaporanViewModel laporanViewModel;

    private TextView tvPeriode;
    private TextView tvTotalPemasukan;
    private TextView tvTotalPengeluaran;
    private TextView tvLaba;
    private LineChart lineChart;
    private TabLayout tabLayout;
    private Button btnCustomPeriode;

    private static final String PREFS_NAME = "REPORT_PERIOD_PREFS";
    private static final String SELECTED_TAB_KEY = "SelectedTab";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laporan, container, false);

        tvPeriode = view.findViewById(R.id.tvPeriode);
        tvTotalPemasukan = view.findViewById(R.id.tvTotalPemasukan);
        tvTotalPengeluaran = view.findViewById(R.id.tvTotalPengeluaran);
        tvLaba = view.findViewById(R.id.tvLaba);
        lineChart = view.findViewById(R.id.lineChart);
        tabLayout = view.findViewById(R.id.tabLayout);
        btnCustomPeriode = view.findViewById(R.id.btnCustomPeriode);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        laporanViewModel = new ViewModelProvider(this).get(LaporanViewModel.class);

        // Setup period tabs
        setupTabLayout();

        // Setup custom period button
        btnCustomPeriode.setOnClickListener(v -> showCustomPeriodPickers());

        // Observe selected period
        laporanViewModel.getSelectedPeriod().observe(getViewLifecycleOwner(), period -> {
            if (period != null && period.length == 2) {
                String formattedPeriod = formatPeriodText(period[0], period[1]);
                tvPeriode.setText(formattedPeriod);
            }
        });

        // Observe financial data
        observeFinancialData();
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.harian));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.mingguan));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.bulanan));
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedIndex = prefs.getInt(SELECTED_TAB_KEY, 2);

        // Default selection
        TabLayout.Tab tab = tabLayout.getTabAt(savedIndex); // Monthly by default
        if (tab != null) {
            tab.select();

            loadDataByPeriod(tab);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                SharedPreferences.Editor editor = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putInt(SELECTED_TAB_KEY, tab.getPosition());
                editor.apply();

                loadDataByPeriod(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void observeFinancialData() {
        // Observe total income
        laporanViewModel.getTotalPemasukanByPeriod().observe(getViewLifecycleOwner(), pemasukan -> {
            Double value = pemasukan != null ? pemasukan : 0;
            tvTotalPemasukan.setText(FormatUtils.formatCurrency(value));
        });

        // Observe total expenses
        laporanViewModel.getTotalPengeluaranByPeriod().observe(getViewLifecycleOwner(), pengeluaran -> {
            Double value = pengeluaran != null ? pengeluaran : 0;
            tvTotalPengeluaran.setText(FormatUtils.formatCurrency(value));
        });

        // Observe profit/loss
        laporanViewModel.getLaba().observe(getViewLifecycleOwner(), laba -> {
            Double value = laba != null ? laba : 0;
            tvLaba.setText(FormatUtils.formatCurrency(value));

            // Change color based on profit or loss
            int colorResId = value >= 0 ? R.color.colorIncome : R.color.colorExpense;
            tvLaba.setTextColor(getResources().getColor(colorResId));
        });

        // Observe transactions for charts
        laporanViewModel.getTransaksiByPeriod().observe(getViewLifecycleOwner(), transaksiList -> {
            updateLineChart(transaksiList);
        });
    }

    private void updateLineChart(List<Transaksi> transaksiList) {
        if (transaksiList == null || transaksiList.isEmpty()) {
            lineChart.setNoDataText(getString(R.string.tidak_ada_data));
            lineChart.invalidate();
            return;
        }

        // Prepare data by date
        Map<String, Double> pemasukanByDate = new HashMap<>();
        Map<String, Double> pengeluaranByDate = new HashMap<>();
        List<String> dateLabels = new ArrayList<>();

        // Sort transactions by date
        for (Transaksi transaksi : transaksiList) {
            String date = transaksi.getTanggal();

            if (!dateLabels.contains(date)) {
                dateLabels.add(date);
            }

            if (transaksi.getJenis().equals("pemasukan")) {
                if (pemasukanByDate.containsKey(date)) {
                    double current = pemasukanByDate.get(date);
                    pemasukanByDate.put(date, current + transaksi.getNominal());
                } else {
                    pemasukanByDate.put(date, transaksi.getNominal());
                }
            } else { // pengeluaran
                if (pengeluaranByDate.containsKey(date)) {
                    double current = pengeluaranByDate.get(date);
                    pengeluaranByDate.put(date, current + transaksi.getNominal());
                } else {
                    pengeluaranByDate.put(date, transaksi.getNominal());
                }
            }
        }

        // Sort dates
        dateLabels.sort((date1, date2) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date d1 = sdf.parse(date1);
                Date d2 = sdf.parse(date2);
                return d1.compareTo(d2);
            } catch (ParseException e) {
                return date1.compareTo(date2);
            }
        });

        // Create entries for line chart
        List<Entry> pemasukanEntries = new ArrayList<>();
        List<Entry> pengeluaranEntries = new ArrayList<>();

        for (int i = 0; i < dateLabels.size(); i++) {
            String date = dateLabels.get(i);

            // Pemasukan entry
            double pemasukan = pemasukanByDate.containsKey(date) ? pemasukanByDate.get(date) : 0;
            pemasukanEntries.add(new Entry(i, (float) pemasukan));

            // Pengeluaran entry
            double pengeluaran = pengeluaranByDate.containsKey(date) ? pengeluaranByDate.get(date) : 0;
            pengeluaranEntries.add(new Entry(i, (float) pengeluaran));
        }

        // Format date labels for display
        List<String> formattedDateLabels = new ArrayList<>();
        for (String date : dateLabels) {
            formattedDateLabels.add(DateTimeUtils.formatDateShort(date));
        }

        // Create dataset for income
        LineDataSet pemasukanDataSet = new LineDataSet(pemasukanEntries, getString(R.string.pemasukan));
        pemasukanDataSet.setColor(getResources().getColor(R.color.colorIncome));
        pemasukanDataSet.setCircleColor(getResources().getColor(R.color.colorIncome));
        pemasukanDataSet.setLineWidth(2f);
        pemasukanDataSet.setCircleRadius(4f);
        pemasukanDataSet.setDrawValues(false);

        // Create dataset for expenses
        LineDataSet pengeluaranDataSet = new LineDataSet(pengeluaranEntries, getString(R.string.pengeluaran));
        pengeluaranDataSet.setColor(getResources().getColor(R.color.colorExpense));
        pengeluaranDataSet.setCircleColor(getResources().getColor(R.color.colorExpense));
        pengeluaranDataSet.setLineWidth(2f);
        pengeluaranDataSet.setCircleRadius(4f);
        pengeluaranDataSet.setDrawValues(false);

        // Combine datasets
        LineData lineData = new LineData(pemasukanDataSet, pengeluaranDataSet);

        // Configure X axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(formattedDateLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(45f); // Angle for better readability

        // Set data to chart
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(true);
        lineChart.animateY(1000);
        lineChart.invalidate();
    }

    private void loadDataByPeriod(TabLayout.Tab tab){
        switch (tab.getPosition()) {
            case 0:
                laporanViewModel.setDailyPeriod();
                break;
            case 1:
                laporanViewModel.setWeeklyPeriod();
                break;
            case 2:
                laporanViewModel.setMonthlyPeriod();
                break;
        }
    }

    private String formatPeriodText(String startDate, String endDate) {
        if (startDate.equals(endDate)) {
            return DateTimeUtils.formatDateFull(startDate);
        } else {
            return DateTimeUtils.formatDateFull(startDate) + " - " + DateTimeUtils.formatDateFull(endDate);
        }
    }

    private void showCustomPeriodPickers() {
        // First show start date picker
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog startDatePicker = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth1) -> {
                    Calendar startDate = Calendar.getInstance();
                    startDate.set(year1, month1, dayOfMonth1);

                    // Then show end date picker
                    DatePickerDialog endDatePicker = new DatePickerDialog(getContext(),
                            (view2, year2, month2, dayOfMonth2) -> {
                                Calendar endDate = Calendar.getInstance();
                                endDate.set(year2, month2, dayOfMonth2);

                                // Set custom period
                                String startDateStr = DateTimeUtils.formatDate(startDate.getTime());
                                String endDateStr = DateTimeUtils.formatDate(endDate.getTime());
                                laporanViewModel.setCustomPeriod(startDateStr, endDateStr);

                                // Deselect all tabs
                                TabLayout.Tab selectedTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());
                                if (selectedTab != null) {
                                    selectedTab.select();
                                    tabLayout.selectTab(null);
                                }
                            }, year, month, dayOfMonth);

                    endDatePicker.getDatePicker().setMinDate(startDate.getTimeInMillis());
                    endDatePicker.show();
                }, year, month, dayOfMonth);

        startDatePicker.show();
    }
}
