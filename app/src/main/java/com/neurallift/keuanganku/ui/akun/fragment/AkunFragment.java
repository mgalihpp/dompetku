package com.neurallift.keuanganku.ui.akun.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.ui.akun.viewmodel.AkunViewModel;
import com.neurallift.keuanganku.ui.akun.dialog.TambahAkunBottomSheet;
import com.neurallift.keuanganku.ui.akun.adapter.AkunAdapter;
import com.neurallift.keuanganku.ui.akun.model.AkunWithSaldo;

public class AkunFragment extends Fragment implements TambahAkunBottomSheet.OnAkunSavedListener {

    private AkunViewModel akunViewModel;
    private RecyclerView rvAkun;
    private View layoutEmptyState;
    private AkunAdapter akunAdapter;
    private FloatingActionButton fabTambahAkun;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_akun, container, false);

        initViews(view);
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        rvAkun = view.findViewById(R.id.rv_akun);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        fabTambahAkun = view.findViewById(R.id.fab_tambah_akun);
    }

    private void setupRecyclerView() {
        akunAdapter = new AkunAdapter();
        rvAkun.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAkun.setAdapter(akunAdapter);

        akunAdapter.setOnItemClickListener(akun -> {
            navigateToDetailAkun(akun);
        });

        akunAdapter.setOnEditClickListener(akun -> {
            showEditAkunBottomSheet(akun.getAkun());
        });

        akunAdapter.setOnDeleteClickListener(akun -> {
            showDeleteConfirmation(akun);
        });
    }

    private void setupViewModel() {
        akunViewModel = new ViewModelProvider(this).get(AkunViewModel.class);

        akunViewModel.getAllAkunWithSaldo().observe(getViewLifecycleOwner(), akunWithSaldoList -> {

            if (akunWithSaldoList != null && !akunWithSaldoList.isEmpty()) {
                akunAdapter.setAkunList(akunWithSaldoList);
                rvAkun.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
            } else {
                rvAkun.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupClickListeners() {
        fabTambahAkun.setOnClickListener(v -> showTambahAkunBottomSheet());
    }

    private void showTambahAkunBottomSheet() {
        TambahAkunBottomSheet bottomSheet = new TambahAkunBottomSheet();
        bottomSheet.setOnAkunSavedListener(this);

        bottomSheet.show(getParentFragmentManager(), "TambahAkunBottomSheet");
    }

    private void showEditAkunBottomSheet(Akun akun){
        TambahAkunBottomSheet bottomSheet = TambahAkunBottomSheet.newInstance(akun);

        Bundle args = new Bundle();
        args.putInt("id", akun.getId());
        args.putString("nama", akun.getNama());

        bottomSheet.setOnAkunSavedListener(this);

        bottomSheet.setArguments(args);
        bottomSheet.show(getParentFragmentManager(), "EditAkunBottomSheet");

    }

    private void showDeleteConfirmation(AkunWithSaldo akunWithSaldo) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi")
                .setMessage(getString(R.string.konfirmasi_hapus_akun))
                .setPositiveButton(getString(R.string.hapus), (dialog, which) -> {
                    akunViewModel.delete(akunWithSaldo.getAkun());
                    Toast.makeText(getContext(), getString(R.string.akun_berhasil_dihapus), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.batal), null)
                .show();
    }

    private void navigateToDetailAkun(AkunWithSaldo akunWithSaldo){
        // Create and show the DetailAkunFragment
        Bundle args = new Bundle();
        args.putInt(DetailAkunFragment.ARG_AKUN_ID, akunWithSaldo.getAkun().getId());
        args.putString(DetailAkunFragment.ARG_AKUN_NAMA, akunWithSaldo.getAkun().getNama());

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.navigation_detail_akun, args);
    }

    @Override
    public void onAkunSaved(Akun akun) {
        akunViewModel.insert(akun);
        Toast.makeText(getContext(), getString(R.string.akun_berhasil_disimpan), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAkunUpdated(Akun akun) {
        akunViewModel.update(akun);
        Toast.makeText(getContext(), getString(R.string.akun_berhasil_disimpan), Toast.LENGTH_SHORT).show();
    }
}
