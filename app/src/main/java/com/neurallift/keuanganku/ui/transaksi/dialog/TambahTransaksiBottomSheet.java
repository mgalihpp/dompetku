package com.neurallift.keuanganku.ui.transaksi.dialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.ui.transaksi.viewmodel.TransaksiViewModel;
import com.neurallift.keuanganku.utils.DateTimeUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DecimalFormat;
import java.util.Calendar;

public class TambahTransaksiBottomSheet extends BottomSheetDialogFragment {

    private TransaksiViewModel transaksiViewModel;
    private TextView tvTransaksiBottomTitle;

    private TextView tvKategori;
    private TextView tvAkun;
    private RadioGroup rgJenis;
    private RadioButton rbPemasukan;
    private RadioButton rbPengeluaran;
    private EditText etNominal;
    private TextView tvTanggal;
    private TextView tvJam;
    private EditText etCatatan;
    private Button btnBatal;
    private Button btnSimpan;

    private String selectedKategori = "";
    private String selectedAkun = "";
    private String selectedTanggal = "";
    private String selectedJam = "";

    private Transaksi existingTransaksi;
    private boolean isEditMode = false;

    public static TambahTransaksiBottomSheet newInstance(Transaksi transaksi) {
        TambahTransaksiBottomSheet fragment = new TambahTransaksiBottomSheet();

        Bundle args = new Bundle();
        args.putInt("id", transaksi.getId());
        args.putString("tanggal", transaksi.getTanggal());
        args.putString("jam", transaksi.getJam());
        args.putString("kategori", transaksi.getKategori());
        args.putString("akun", transaksi.getAkun());
        args.putString("jenis", transaksi.getJenis());
        args.putDouble("nominal", transaksi.getNominal());
        args.putString("catatan", transaksi.getCatatan());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);

        // Get ViewModel
        transaksiViewModel = new ViewModelProvider(requireActivity()).get(TransaksiViewModel.class);

        // Check if in edit mode
        Bundle args = getArguments();
        if (args != null && args.containsKey("id")) {
            isEditMode = true;

            // Recreate the existing transaksi object
            existingTransaksi = new Transaksi(
                    args.getString("tanggal"),
                    args.getString("jam"),
                    args.getString("kategori"),
                    args.getString("akun"),
                    args.getString("jenis"),
                    args.getDouble("nominal"),
                    args.getString("catatan")
            );
            existingTransaksi.setId(args.getInt("id"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_tambah_transaksi, container, false);

        // Initialize views
        tvTransaksiBottomTitle = view.findViewById(R.id.tvTransaksi_title);
        tvKategori = view.findViewById(R.id.tvKategori);
        tvAkun = view.findViewById(R.id.tvAkun);
        rgJenis = view.findViewById(R.id.rgJenis);
        rbPemasukan = view.findViewById(R.id.rbPemasukan);
        rbPengeluaran = view.findViewById(R.id.rbPengeluaran);
        etNominal = view.findViewById(R.id.etNominal);
        tvTanggal = view.findViewById(R.id.tvTanggal);
        tvJam = view.findViewById(R.id.tvJam);
        etCatatan = view.findViewById(R.id.etCatatan);
        btnBatal = view.findViewById(R.id.btnBatal);
        btnSimpan = view.findViewById(R.id.btnSimpan);

        // Set current date and time
        Calendar calendar = Calendar.getInstance();
        selectedTanggal = DateTimeUtils.formatDate(calendar.getTime());
        selectedJam = DateTimeUtils.formatTime(calendar.getTime());

        tvTanggal.setText(selectedTanggal);
        tvJam.setText(selectedJam);

        // If in edit mode, fill in the data
        if (isEditMode && existingTransaksi != null) {
            tvTransaksiBottomTitle.setText(R.string.edit_transaksi);
            selectedKategori = existingTransaksi.getKategori();
            selectedAkun = existingTransaksi.getAkun();
            selectedTanggal = existingTransaksi.getTanggal();
            selectedJam = existingTransaksi.getJam();

            tvKategori.setText(selectedKategori);
            tvAkun.setText(selectedAkun);
            tvTanggal.setText(selectedTanggal);
            tvJam.setText(selectedJam);

            DecimalFormat df = new DecimalFormat("#.##");
            etNominal.setText(df.format(existingTransaksi.getNominal()));

            etCatatan.setText(existingTransaksi.getCatatan());

            if (existingTransaksi.getJenis().equals("pemasukan")) {
                rbPemasukan.setChecked(true);
            } else {
                rbPengeluaran.setChecked(true);
            }

            btnSimpan.setText(R.string.perbarui);
        }

        // Set click listeners
        tvKategori.setOnClickListener(v -> showKategoriBottomSheet());
        tvAkun.setOnClickListener(v -> showAkunBottomSheet());
        tvTanggal.setOnClickListener(v -> showDatePicker());
        tvJam.setOnClickListener(v -> showTimePicker());

        btnBatal.setOnClickListener(v -> dismiss());
        btnSimpan.setOnClickListener(v -> saveTransaksi());

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

    private void showAkunBottomSheet() {
        AkunSelectionBottomSheet bottomSheet = new AkunSelectionBottomSheet();
        bottomSheet.setAkunSelectedListener(akun -> {
            selectedAkun = akun;
            tvAkun.setText(akun);
        });
        bottomSheet.show(getChildFragmentManager(), "pilihAkun");
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth1) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year1, month1, dayOfMonth1);
                    selectedTanggal = DateTimeUtils.formatDate(newDate.getTime());
                    tvTanggal.setText(selectedTanggal);
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay1, minute1) -> {
                    Calendar newTime = Calendar.getInstance();
                    newTime.set(Calendar.HOUR_OF_DAY, hourOfDay1);
                    newTime.set(Calendar.MINUTE, minute1);
                    selectedJam = DateTimeUtils.formatTime(newTime.getTime());
                    tvJam.setText(selectedJam);
                }, hourOfDay, minute, true);

        timePickerDialog.show();
    }

    private void saveTransaksi() {
        // Validate inputs
        if (TextUtils.isEmpty(selectedKategori)) {
            Toast.makeText(getContext(), R.string.kategori_kosong, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(selectedAkun)) {
            Toast.makeText(getContext(), R.string.akun_kosong, Toast.LENGTH_SHORT).show();
            return;
        }

        String nominalStr = etNominal.getText().toString().trim();
        if (TextUtils.isEmpty(nominalStr)) {
            Toast.makeText(getContext(), R.string.nominal_kosong, Toast.LENGTH_SHORT).show();
            return;
        }

        // Get jenis selected
        int selectedJenisId = rgJenis.getCheckedRadioButtonId();
        if (selectedJenisId == -1) {
            Toast.makeText(getContext(), R.string.jenis_kosong, Toast.LENGTH_SHORT).show();
            return;
        }

        String jenis = selectedJenisId == R.id.rbPemasukan ? "pemasukan" : "pengeluaran";
        double nominal = Double.parseDouble(nominalStr);
        String catatan = etCatatan.getText().toString().trim();

        if (isEditMode) {
            // Update existing transaction
            existingTransaksi.setKategori(selectedKategori);
            existingTransaksi.setAkun(selectedAkun);
            existingTransaksi.setJenis(jenis);
            existingTransaksi.setNominal(nominal);
            existingTransaksi.setTanggal(selectedTanggal);
            existingTransaksi.setJam(selectedJam);
            existingTransaksi.setCatatan(catatan);

            transaksiViewModel.update(existingTransaksi);
            Toast.makeText(getContext(), R.string.transaksi_diperbarui, Toast.LENGTH_SHORT).show();
        } else {
            // Create new transaction
            Transaksi transaksi = new Transaksi(
                    selectedTanggal,
                    selectedJam,
                    selectedKategori,
                    selectedAkun,
                    jenis,
                    nominal,
                    catatan
            );

            transaksiViewModel.insert(transaksi);
            Toast.makeText(getContext(), R.string.transaksi_disimpan, Toast.LENGTH_SHORT).show();
        }

        dismiss();
    }
}
