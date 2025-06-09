package com.neurallift.keuanganku.ui.kategori.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Kategori;
import com.neurallift.keuanganku.ui.kategori.adapter.KategoriAdapter;
import com.neurallift.keuanganku.ui.kategori.dialog.TambahKategoriBottomSheet;
import com.neurallift.keuanganku.ui.kategori.viewmodel.KategoriViewModel;

public class KategoriFragment extends Fragment implements TambahKategoriBottomSheet.OnKategoriSavedListener {
    private KategoriViewModel kategoriViewModel;
    private RecyclerView rvKategori;
    private View layoutEmptyState;
    private KategoriAdapter kategoriAdapter;
    private FloatingActionButton fabTambahKategori;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kategori, container, false);

        initViews(view);
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        rvKategori = view.findViewById(R.id.rv_kategori);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        fabTambahKategori = view.findViewById(R.id.fab_tambah_kategori);
    }

    private void setupRecyclerView() {
        kategoriAdapter = new KategoriAdapter();
        rvKategori.setLayoutManager(new LinearLayoutManager(getContext()));
        rvKategori.setAdapter(kategoriAdapter);

        kategoriAdapter.setOnItemClickListener(kategori -> {
            // Handle item click
        });

        kategoriAdapter.setOnEditClickListener(kategori -> {
            showEditKategoriBottomSheet(kategori);
        });

        kategoriAdapter.setOnDeleteClickListener(kategori -> {
            showDeleteConfirmation(kategori);
        });
    }

    private void setupViewModel() {
        kategoriViewModel = new ViewModelProvider(this).get(KategoriViewModel.class);

        kategoriViewModel.getAllKategori().observe(getViewLifecycleOwner(), kategoriList -> {
            if (kategoriList != null && !kategoriList.isEmpty()) {
                kategoriAdapter.setKategoriList(kategoriList);
                rvKategori.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
            } else {
                rvKategori.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupClickListeners() {
        fabTambahKategori.setOnClickListener(v -> showTambahKategoriBottomSheet());
    }

    private void showTambahKategoriBottomSheet() {
        TambahKategoriBottomSheet bottomSheet = new TambahKategoriBottomSheet();
        bottomSheet.setOnKategoriSavedListener(this);

        bottomSheet.show(getParentFragmentManager(), "TambahKategoriBottomSheet");
    }

    private void showEditKategoriBottomSheet(Kategori kategori){
        TambahKategoriBottomSheet bottomSheet = new TambahKategoriBottomSheet();
        bottomSheet.setOnKategoriSavedListener(this);

        Bundle args = new Bundle();
        args.putInt("id", kategori.getId());
        args.putString("nama", kategori.getNama());

        bottomSheet.setArguments(args);
        bottomSheet.show(getParentFragmentManager(), "EditKategoriBottomSheet");
    }

    private void showDeleteConfirmation(Kategori kategori) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi")
                .setMessage(getString(R.string.konfirmasi_hapus_kategori))
                .setPositiveButton(getString(R.string.hapus), (dialog, which) -> {
                    kategoriViewModel.delete(kategori);
                });
    }

    @Override
    public void onKategoriSaved(Kategori kategori) {
        kategoriViewModel.insert(kategori);
    }

    @Override
    public void onKategoriUpdated(Kategori kategori) {
        kategoriViewModel.update(kategori);
    }
}
