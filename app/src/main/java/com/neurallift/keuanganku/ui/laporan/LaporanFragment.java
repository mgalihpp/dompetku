package com.neurallift.keuanganku.ui.laporan;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.ui.AccessibleLinearLayout;
import com.neurallift.keuanganku.ui.laporan.adapter.ChartLegendAdapter;
import com.neurallift.keuanganku.ui.laporan.model.AkunWithTransaksi;
import com.neurallift.keuanganku.ui.laporan.model.ChartLegendItem;
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
    private PieChart pemasukanChart;
    private PieChart pengeluaranChart;
    private RecyclerView rvPemasukan;
    private RecyclerView rvPengeluaran;
    private TabLayout tabLayout;
    private Button btnCustomPeriode;
    private int currentPeriod = 0; // 0: daily, 1: weekly, 2: monthly

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
        pemasukanChart = view.findViewById(R.id.pemasukanChart);
        pengeluaranChart = view.findViewById(R.id.pengeluaranChart);
        rvPemasukan = view.findViewById(R.id.rvPemasukan);
        rvPengeluaran = view.findViewById(R.id.rvPengeluaran);
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

        // Setup swipe gesture to navigate between periods
        setupSwipeGesture();

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
        AccessibleLinearLayout mainLayout = getView().findViewById(R.id.laporan_main_layout);
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

            if(transaksiList.isEmpty()){
                lineChart.setNoDataText(getString(R.string.tidak_ada_data));
                lineChart.invalidate();
                return;
            }

            updateLineChart(transaksiList);
        });

        // Obeserve pie chart
        laporanViewModel.getAllIncomeCategories().observe(getViewLifecycleOwner(), transaksiList -> {

            if(transaksiList.isEmpty()){
                pemasukanChart.setNoDataText(getString(R.string.tidak_ada_data));
                pemasukanChart.setData(null);
                rvPemasukan.setAdapter(null);
                pemasukanChart.invalidate();
                return;
            }

            updateIncomePieChart(transaksiList);
        });

        laporanViewModel.getAllExpenseCategories().observe(getViewLifecycleOwner(), transaksiList -> {

            if(transaksiList.isEmpty()){
                pengeluaranChart.setNoDataText(getString(R.string.tidak_ada_data));
                pengeluaranChart.setData(null);
                rvPengeluaran.setAdapter(null);
                pengeluaranChart.invalidate();
                return;
            }

            updateExpensePieChart(transaksiList);
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
        lineChart.getLegend().setEnabled(false);
        lineChart.animateY(1000);
        lineChart.invalidate();
    }

    private void updateIncomePieChart(List<Transaksi> transaksiList){
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        List<ChartLegendItem> pemasukanItems = new ArrayList<>();

        Map<String, Double> incomeMap = new HashMap<>();
        Map<String, Integer> kategoriColorMap = new HashMap<>();

        int index = 0;
        for(Transaksi transaksi : transaksiList){
            String kategori = transaksi.getKategori();
            Double nomimal = transaksi.getNominal();

            if (!kategoriColorMap.containsKey(kategori)) {
                float[] hues = {120f, 60f, 0f, 240f, 300f};
                float hue = hues[index % hues.length];
                float saturation = 0.65f;
                float brightness = 0.85f;

                int color = Color.HSVToColor(new float[]{hue, saturation, brightness});
                kategoriColorMap.put(kategori, color);
                index++;
            }

            incomeMap.put(kategori, incomeMap.getOrDefault(kategori, 0.0) + nomimal);
        }

        for (Map.Entry<String, Double> entry : incomeMap.entrySet()) {
            entries.add(new PieEntry(
                    (float) entry.getValue().doubleValue(),
                    entry.getKey()));
            colors.add(kategoriColorMap.get(entry.getKey()));
            pemasukanItems.add(new ChartLegendItem(
                    entry.getKey(),
                    entry.getValue(),
                    kategoriColorMap.get(entry.getKey())
            ));
        }

        ChartLegendAdapter adapter = new ChartLegendAdapter(pemasukanItems, "pemasukan");
        rvPemasukan.setAdapter(adapter);

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setValueFormatter(new PercentFormatter(pemasukanChart));
        dataSet.setColors(colors);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pemasukanChart.setData(data);
        pemasukanChart.setUsePercentValues(true);
        pemasukanChart.setEntryLabelColor(Color.BLACK);
        pemasukanChart.setEntryLabelTextSize(12f);
        pemasukanChart.setDrawHoleEnabled(false);
        pemasukanChart.setHoleColor(Color.TRANSPARENT);
        pemasukanChart.setCenterText(null);
        pemasukanChart.setCenterTextSize(16f);

        pemasukanChart.getDescription().setEnabled(false);
        pemasukanChart.getLegend().setEnabled(false);
        pemasukanChart.animateY(1000, Easing.EaseInOutQuad);
        pemasukanChart.invalidate();
    }

    private void updateExpensePieChart(List<Transaksi> transaksiList){
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        List<ChartLegendItem> pengeluaranItems = new ArrayList<>();

        Map<String, Double> expenseMap = new HashMap<>();
        Map<String, Integer> kategoriColorMap = new HashMap<>();

        int index = 0;
        for(Transaksi transaksi : transaksiList){
            String kategori = transaksi.getKategori();
            Double nomimal = transaksi.getNominal();

            if (!kategoriColorMap.containsKey(kategori)) {
                float[] hues = {120f, 60f, 0f, 240f, 300f};
                float hue = hues[(index + 111) % hues.length];
                float saturation = 0.65f;
                float brightness = 0.85f;

                int color = Color.HSVToColor(new float[]{hue, saturation, brightness});
                kategoriColorMap.put(kategori, color);
                index++;
            }

            expenseMap.put(kategori, expenseMap.getOrDefault(kategori, 0.0) + nomimal);
        }

        for (Map.Entry<String, Double> entry : expenseMap.entrySet()) {
            entries.add(new PieEntry(
                    (float) entry.getValue().doubleValue(),
                    entry.getKey()));
            colors.add(kategoriColorMap.get(entry.getKey()));
            pengeluaranItems.add(new ChartLegendItem(
                    entry.getKey(),
                    entry.getValue(),
                    kategoriColorMap.get(entry.getKey())
            ));
        }

        ChartLegendAdapter adapter = new ChartLegendAdapter(pengeluaranItems, "pengeluaran");
        rvPengeluaran.setAdapter(adapter);

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setValueFormatter(new PercentFormatter(pengeluaranChart));
        dataSet.setColors(colors);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pengeluaranChart.setData(data);
        pengeluaranChart.setUsePercentValues(true);
        pengeluaranChart.setEntryLabelColor(Color.BLACK);
        pengeluaranChart.setEntryLabelTextSize(12f);
        pengeluaranChart.setDrawHoleEnabled(false);
        pengeluaranChart.setHoleColor(Color.TRANSPARENT);
        pengeluaranChart.setCenterText(null);
        pengeluaranChart.setCenterTextSize(16f);

        pengeluaranChart.getDescription().setEnabled(false);
        pengeluaranChart.getLegend().setEnabled(false);
        pengeluaranChart.animateY(1000, Easing.EaseInOutQuad);
        pengeluaranChart.invalidate();
    }

    private void updatePieChart(List<AkunWithTransaksi> akunList){
        List<PieEntry> pemasukanEntries = new ArrayList<>();
        List<PieEntry> pengeluaranEntries = new ArrayList<>();

        for (AkunWithTransaksi item : akunList) {
            Akun akun = item.getAkun();
            double totalPemasukan = 0;
            double totalPengeluaran = 0;

            for (Transaksi t : item.getTransaksiList()) {
                if ("pemasukan".equalsIgnoreCase(t.getJenis())) {
                    totalPemasukan += t.getNominal();
                } else if ("pengeluaran".equalsIgnoreCase(t.getJenis())) {
                    totalPengeluaran += t.getNominal();
                } else {
                    Log.e("LaporanFragment", "Invalid transaction type: " + t.getJenis());
                }
            }

            if (totalPemasukan > 0) {
                pemasukanEntries.add(new PieEntry((float) totalPemasukan, akun.getNama()));
            } else if (totalPengeluaran > 0) {
                pengeluaranEntries.add(new PieEntry((float) totalPengeluaran, akun.getNama()));
            } else {
                Log.d("LaporanFragment", "No transactions for akun: " + akun.getNama());
            }
        }

        PieDataSet dataSet = new PieDataSet(pemasukanEntries, "");
        dataSet.setValueFormatter(new PercentFormatter(pemasukanChart));
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieDataSet pengeluaranDataSet = new PieDataSet(pengeluaranEntries, "");
        pengeluaranDataSet.setValueFormatter(new PercentFormatter(pengeluaranChart));
        pengeluaranDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        pemasukanChart.setData(data);
        pemasukanChart.setUsePercentValues(true);
        pemasukanChart.setEntryLabelColor(Color.BLACK);
        pemasukanChart.setEntryLabelTextSize(12f);
        pemasukanChart.setDrawHoleEnabled(false);
        pemasukanChart.setHoleColor(Color.TRANSPARENT);
        pemasukanChart.setCenterText(null);
        pemasukanChart.setCenterTextSize(16f);

        pemasukanChart.getDescription().setEnabled(false);
        pemasukanChart.getLegend().setEnabled(true);
        pemasukanChart.animateY(1000, Easing.EaseInOutQuad);
        pemasukanChart.invalidate();

        PieData pengeluaranData = new PieData(pengeluaranDataSet);
        pengeluaranChart.setData(pengeluaranData);
        pengeluaranChart.setUsePercentValues(true);
        pengeluaranChart.setEntryLabelColor(Color.BLACK);
        pengeluaranChart.setEntryLabelTextSize(12f);
        pengeluaranChart.setDrawHoleEnabled(false);
        pengeluaranChart.setHoleColor(Color.TRANSPARENT);
        pengeluaranChart.setCenterText(null);
        pengeluaranChart.setCenterTextSize(16f);

        pengeluaranChart.getDescription().setEnabled(false);
        pengeluaranChart.getLegend().setEnabled(true);
        pengeluaranChart.animateY(1000, Easing.EaseInOutQuad);
        pengeluaranChart.invalidate();
    }

    private void loadDataByPeriod(){
        switch (currentPeriod) {
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
