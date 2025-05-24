package com.neurallift.keuanganku.ui.transaksi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class TransaksiFragment extends Fragment implements TransaksiAdapter.OnTransaksiClickListener {

    private TransaksiViewModel transaksiViewModel;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private TransaksiAdapter adapter;
    private FloatingActionButton fabFilter;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaksi, container, false);

        recyclerView = view.findViewById(R.id.rvTransaksi);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        fabFilter = view.findViewById(R.id.fabFilter);
        fabAdd = view.findViewById(R.id.fabAdd);

        setupRecyclerView();

        // Set up filter FAB
        fabFilter.setOnClickListener(v -> {
            showFilterBottomSheet();
        });

        // Set up add transaction FAB
        fabAdd.setOnClickListener(v -> {
            showAddTransactionBottomSheet();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transaksiViewModel = new ViewModelProvider(this).get(TransaksiViewModel.class);

        // Observe the filtered transactions
        transaksiViewModel.getFilteredTransaksi().observe(getViewLifecycleOwner(), transaksiList -> {
            adapter.submitList(transaksiList);

            if (transaksiList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            }

        });
    }

    private void setupRecyclerView() {
        adapter = new TransaksiAdapter();
        adapter.setOnTransaksiClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void showFilterBottomSheet() {
        FilterTransaksiBottomSheet bottomSheet = new FilterTransaksiBottomSheet();
        bottomSheet.setTransaksiViewModel(transaksiViewModel);
        bottomSheet.show(getChildFragmentManager(), "filterTransaksi");
    }

    private void showAddTransactionBottomSheet() {
        TambahTransaksiBottomSheet bottomSheet = new TambahTransaksiBottomSheet();
        bottomSheet.show(getChildFragmentManager(), "tambahTransaksi");
    }

    @Override
    public void onTransaksiClick(Transaksi transaksi) {
        // Show detail or edit options
        TambahTransaksiBottomSheet bottomSheet = TambahTransaksiBottomSheet.newInstance(transaksi);
        bottomSheet.show(getChildFragmentManager(), "editTransaksi");
    }

    @Override
    public void onTransaksiLongClick(Transaksi transaksi) {
        // Show delete confirmation
        Snackbar.make(requireView(), R.string.konfirmasi_hapus, Snackbar.LENGTH_LONG)
                .setAction(R.string.hapus, v -> {
                    transaksiViewModel.delete(transaksi);
                    Toast.makeText(requireContext(), R.string.transaksi_dihapus, Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
