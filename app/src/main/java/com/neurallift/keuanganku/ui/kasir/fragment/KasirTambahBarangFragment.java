package com.neurallift.keuanganku.ui.kasir.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Barang;
import com.neurallift.keuanganku.ui.kasir.dialog.SatuanSelectionBottomSheet;
import com.neurallift.keuanganku.ui.kasir.viewmodel.KasirViewModel;
import com.neurallift.keuanganku.ui.transaksi.dialog.KategoriSelectionBottomSheet;

public class KasirTambahBarangFragment extends Fragment {

    private KasirViewModel kasirViewModel;
    private Toolbar toolbar;
    private EditText etNamaBarang;
    private EditText etHargaBarang;
    private EditText etStokBarang;
    private TextView tvSatuan;
    private TextView tvKategori;
    private Button btn_save;

    private String selectedKategori = "";
    private String selectedSatuan = "";
    private Boolean isEditMode = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        kasirViewModel = new ViewModelProvider(this).get(KasirViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kasir_tambah_barang, container, false);

        initViews(view);
        setupToolbar();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvKategori.setOnClickListener(v -> {
            showKategoriBottomSheet();
        });

        tvSatuan.setOnClickListener(v -> {
            showSatuanBottomSheet();
        });

        btn_save.setOnClickListener(v -> {
            simpanBarang();
        });
    }

    private void initViews(View view){
        toolbar = view.findViewById(R.id.toolbar);
        etNamaBarang = view.findViewById(R.id.etNamaBarang);
        etHargaBarang = view.findViewById(R.id.etHargaBarang);
        etStokBarang = view.findViewById(R.id.etStokBarang);
        tvSatuan = view.findViewById(R.id.tvSatuan);
        tvKategori = view.findViewById(R.id.tvKategori);
        btn_save = view.findViewById(R.id.btn_save);
    }

    private void setupToolbar(){
        toolbar.setNavigationOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigateUp();
        });
    }

    private void simpanBarang(){
        String namaBarang = etNamaBarang.getText().toString();
        String hargaBarang = etHargaBarang.getText().toString();
        String stokBarang = etStokBarang.getText().toString();

        if(TextUtils.isEmpty(namaBarang)){
            etNamaBarang.setError("Nama barang tidak boleh kosong");
            return;
        }

        if(TextUtils.isEmpty(hargaBarang)){
            etHargaBarang.setError("Harga barang tidak boleh kosong");
            return;
        }

        if(TextUtils.isEmpty(selectedKategori)){
            tvKategori.setError("Kategori barang tidak boleh kosong");
            return;
        }

        if(TextUtils.isEmpty(selectedSatuan)){
            tvSatuan.setError("Satuan barang tidak boleh kosong");
            return;
        }


        double harga = Double.parseDouble(hargaBarang);
        int stok = Integer.parseInt(stokBarang.isEmpty() ? "0" : stokBarang);

        if(isEditMode){
            return;
        } else {

            Barang barang = new Barang(
                    namaBarang,
                    harga,
                    stok,
                    selectedSatuan,
                    selectedKategori
            );

            kasirViewModel.insert(barang);
            Toast.makeText(getContext(), R.string.barang_disimpan, Toast.LENGTH_SHORT).show();

            NavController navController = NavHostFragment.findNavController(this);
            navController.navigateUp();
            clearForm();
        }
    }

    private void showKategoriBottomSheet() {
        KategoriSelectionBottomSheet bottomSheet = new KategoriSelectionBottomSheet();
        bottomSheet.setKategoriSelectedListener(kategori -> {
            selectedKategori = kategori;
            tvKategori.setText(kategori);
        });
        bottomSheet.show(getChildFragmentManager(), "pilihKategori");
    }

    private void showSatuanBottomSheet() {
        SatuanSelectionBottomSheet bottomSheet = new SatuanSelectionBottomSheet();
        bottomSheet.setSatuanSelectedListener(satuan -> {
            selectedSatuan = satuan;
            tvSatuan.setText(satuan);
        });
        bottomSheet.show(getChildFragmentManager(), "pilihSatuan");
    }

    void clearForm(){
        etNamaBarang.setText("");
        etHargaBarang.setText("");
        etStokBarang.setText("");
        tvSatuan.setText("");
        tvKategori.setText("");
    }
}
