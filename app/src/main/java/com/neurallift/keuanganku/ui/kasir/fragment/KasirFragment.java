package com.neurallift.keuanganku.ui.kasir.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.MainActivity;
import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Barang;
import com.neurallift.keuanganku.ui.kasir.adapter.BarangAdapter;
import com.neurallift.keuanganku.ui.kasir.viewmodel.KasirViewModel;
import com.neurallift.keuanganku.ui.transaksi.dialog.KategoriSelectionBottomSheet;
import com.neurallift.keuanganku.utils.FormatUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KasirFragment extends Fragment {
    private KasirViewModel kasirViewModel;
    private TextView tvKategori;
    private ImageButton btnSearch;
    private ImageButton btnAdd;
    private RecyclerView rvKasir;
    private ConstraintLayout cl_jumlah_barang_harga;
    private TextView tv_jumlah_barang;
    private TextView tv_harga;
    private Button btn_lanjut;
    private TextView btn_delete;
    private LinearLayout layout_empty_state;

    private BarangAdapter barangAdapter;
    private final Map<Barang, Integer> selectedBarangMap = new HashMap<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        kasirViewModel = new ViewModelProvider(this).get(KasirViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kasir, container, false);

        initViews(view);
        setupRecyclerView();
        loadDataBarang();

        btnSearch.setVisibility(View.GONE);

        tvKategori.setOnClickListener(v -> showKategoriBottomSheet());

        btnAdd.setOnClickListener(v -> navigateToTambahBarang());

        btn_lanjut.setOnClickListener(v -> {

            if(selectedBarangMap.isEmpty()){
                Toast.makeText(getContext(), "Tidak ada barang yang dipilih", Toast.LENGTH_SHORT).show();
                return;
            }

            navigateToPembayaran();
        });

        btn_delete.setOnClickListener(v -> {
           selectedBarangMap.clear();
           updateSelectedUI();
        });

        return view;
    }

    void initViews(View view){
        tvKategori = view.findViewById(R.id.tvKategori);
        btnSearch = view.findViewById(R.id.btn_search);
        btnAdd = view.findViewById(R.id.btn_add);
        btn_delete = view.findViewById(R.id.btn_delete);
        rvKasir = view.findViewById(R.id.rvKasir);
        cl_jumlah_barang_harga = view.findViewById(R.id.cl_jumlah_barang_harga);
        tv_jumlah_barang = view.findViewById(R.id.tv_jumlah_barang);
        tv_harga = view.findViewById(R.id.tv_harga);
        btn_lanjut = view.findViewById(R.id.btn_lanjut);
        layout_empty_state = view.findViewById(R.id.layout_empty_state);
    }

    void setupRecyclerView(){
        barangAdapter = new BarangAdapter();
        rvKasir.setLayoutManager(new LinearLayoutManager(getContext()));
        rvKasir.setAdapter(barangAdapter);

        barangAdapter.setOnItemClickListener(barang -> {
            int count = selectedBarangMap.getOrDefault(barang, 0);
            selectedBarangMap.put(barang, count + 1); // tambah jumlah 1

            cl_jumlah_barang_harga.setVisibility(View.VISIBLE);

            // Hitung total harga
            double totalHarga = 0;
            int totalItem = 0;
            for (Map.Entry<Barang, Integer> entry : selectedBarangMap.entrySet()) {
                Barang b = entry.getKey();
                int jumlah = entry.getValue();
                totalHarga += b.getHarga() * jumlah;
                totalItem += jumlah;
            }

            tv_harga.setText(FormatUtils.formatCurrency(totalHarga));
            tv_jumlah_barang.setText(totalItem + " Barang");
        });

        barangAdapter.setOnDeleteClickListener(barang -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Hapus Barang")
                    .setMessage("Yakin ingin menghapus barang \"" + barang.getNama() + "\"?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        kasirViewModel.delete(barang);
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });


        updateSelectedUI();
    }

    void loadDataBarang(){

        kasirViewModel.getBarangFiltered().observe(getViewLifecycleOwner(), barangList -> {
            if (barangList != null && !barangList.isEmpty()) {
                layout_empty_state.setVisibility(View.GONE);
                rvKasir.setVisibility(View.VISIBLE);

                barangAdapter.setBarangList(barangList);
            } else {
                layout_empty_state.setVisibility(View.VISIBLE);
                rvKasir.setVisibility(View.GONE);
            }
        });

    }

    private void showKategoriBottomSheet() {
        KategoriSelectionBottomSheet bottomSheet = new KategoriSelectionBottomSheet();
        bottomSheet.setKategoriSelectedListener(kategori -> {
            tvKategori.setText(kategori);
            kasirViewModel.setKategori(kategori);
        });
        bottomSheet.show(getChildFragmentManager(), "pilihKategori");
    }

    private void navigateToTambahBarang(){
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.navigation_kasir_tambah_barang, null, getNavOptions());

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectBottomNav(R.id.navigation_kasir);
        }
    }

    private void navigateToPembayaran(){
        Bundle args = new Bundle();

        double totalHarga = 0;
        ArrayList<Barang> barangList = new ArrayList<>();
        int[] jumlahList = new int[selectedBarangMap.size()];
        int index = 0;

        for (Map.Entry<Barang, Integer> entry : selectedBarangMap.entrySet()) {
            Barang b = entry.getKey();
            double jumlah = entry.getValue();

            barangList.add(entry.getKey());
            jumlahList[index++] = entry.getValue();

            totalHarga += b.getHarga() * jumlah;
        }

        args.putFloat("total_pembayaran", (float) totalHarga);
        args.putParcelableArrayList("barangList", barangList);
        args.putIntArray("jumlahList", jumlahList);

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.navigation_kasir_pembayaran, args, getNavOptions());

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectBottomNav(R.id.navigation_kasir);
        }
    }

    private void updateSelectedUI() {
        if (selectedBarangMap.isEmpty()) {
            cl_jumlah_barang_harga.setVisibility(View.GONE);
        } else {
            cl_jumlah_barang_harga.setVisibility(View.VISIBLE);

            double totalHarga = 0;
            int totalItem = 0;
            for (Map.Entry<Barang, Integer> entry : selectedBarangMap.entrySet()) {
                Barang b = entry.getKey();
                int jumlah = entry.getValue();
                totalHarga += b.getHarga() * jumlah;
                totalItem += jumlah;
            }

            tv_harga.setText(FormatUtils.formatCurrency(totalHarga));
            tv_jumlah_barang.setText(totalItem + " Barang");
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
}
