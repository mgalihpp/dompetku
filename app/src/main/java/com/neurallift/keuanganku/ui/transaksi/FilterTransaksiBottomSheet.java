package com.neurallift.keuanganku.ui.transaksi;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.utils.DateTimeUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Calendar;

public class FilterTransaksiBottomSheet extends BottomSheetDialogFragment {

    private TransaksiViewModel transaksiViewModel;

    private TextView tvKategori;
    private ChipGroup chipGroupJenis;
    private Chip chipPemasukan;
    private Chip chipPengeluaran;
    private TextView tvTanggalMulai;
    private TextView tvTanggalSelesai;
    private Button btnReset;
    private Button btnTerapkan;

    private String selectedKategori = "";
    private String selectedJenis = "";
    private String tanggalMulai = "";
    private String tanggalSelesai = "";

    public void setTransaksiViewModel(TransaksiViewModel viewModel) {
        this.transaksiViewModel = viewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_filter_transaksi, container, false);

        // Initialize views
        tvKategori = view.findViewById(R.id.tvKategori);
        chipGroupJenis = view.findViewById(R.id.chipGroupJenis);
        chipPemasukan = view.findViewById(R.id.chipPemasukan);
        chipPengeluaran = view.findViewById(R.id.chipPengeluaran);
        tvTanggalMulai = view.findViewById(R.id.tvTanggalMulai);
        tvTanggalSelesai = view.findViewById(R.id.tvTanggalSelesai);
        btnReset = view.findViewById(R.id.btnReset);
        btnTerapkan = view.findViewById(R.id.btnTerapkan);

        // Set click listeners
        tvKategori.setOnClickListener(v -> showKategoriBottomSheet());
        tvTanggalMulai.setOnClickListener(v -> showDatePickerMulai());
        tvTanggalSelesai.setOnClickListener(v -> showDatePickerSelesai());

        chipGroupJenis.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipPemasukan) {
                selectedJenis = "pemasukan";
            } else if (checkedId == R.id.chipPengeluaran) {
                selectedJenis = "pengeluaran";
                chipPengeluaran.setChecked(true);
            } else {
                chipPemasukan.setChecked(false);
                chipPengeluaran.setChecked(false);
                selectedJenis = "";
            }
        });

        btnReset.setOnClickListener(v -> {
            resetFilters();
        });

        btnTerapkan.setOnClickListener(v -> {
            applyFilters();
            dismiss();
        });

        return view;
    }

    private void showKategoriBottomSheet() {
        KategoriSelectionBottomSheet bottomSheet = new KategoriSelectionBottomSheet();
        bottomSheet.setKategoriSelectedListener(kategori -> {
            selectedKategori = kategori;
            tvKategori.setText(kategori);
        });
        bottomSheet.show(getChildFragmentManager(), "pilihKategori");
    }

    private void showDatePickerMulai() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth1) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year1, month1, dayOfMonth1);
                    tanggalMulai = DateTimeUtils.formatDate(newDate.getTime());
                    tvTanggalMulai.setText(tanggalMulai);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void showDatePickerSelesai() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth1) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year1, month1, dayOfMonth1);
                    tanggalSelesai = DateTimeUtils.formatDate(newDate.getTime());
                    tvTanggalSelesai.setText(tanggalSelesai);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void resetFilters() {
        selectedKategori = "";
        selectedJenis = "";
        tanggalMulai = "";
        tanggalSelesai = "";

        tvKategori.setText(R.string.pilih_kategori);
        chipGroupJenis.clearCheck();
        tvTanggalMulai.setText(R.string.tanggal_mulai);
        tvTanggalSelesai.setText(R.string.tanggal_selesai);

        if (transaksiViewModel != null) {
            transaksiViewModel.clearFilters();
        }
    }

    private void applyFilters() {
        if (transaksiViewModel != null) {
            if (!selectedKategori.isEmpty()) {
                transaksiViewModel.setFilterKategori(selectedKategori);
            }

            if (!selectedJenis.isEmpty()) {
                transaksiViewModel.setFilterJenis(selectedJenis);
            }

            if (!tanggalMulai.isEmpty() && !tanggalSelesai.isEmpty()) {
                transaksiViewModel.setFilterPeriode(tanggalMulai, tanggalSelesai);
            }
        }
    }
}
