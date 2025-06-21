package com.neurallift.keuanganku.ui.transaksi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.MainActivity;
import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.neurallift.keuanganku.ui.akun.adapter.TransaksiGroupAdapter;
import com.neurallift.keuanganku.ui.transaksi.dialog.FilterTransaksiBottomSheet;
import com.neurallift.keuanganku.ui.transaksi.dialog.TambahTransaksiBottomSheet;
import com.neurallift.keuanganku.ui.transaksi.viewmodel.TransaksiViewModel;

public class TransaksiFragment extends Fragment implements TransaksiGroupAdapter.OnTransaksiClickListener {

    private TransaksiViewModel transaksiViewModel;
    private RecyclerView recyclerView;
    private LinearLayout tvEmpty;
    private TransaksiGroupAdapter adapter;
    private FloatingActionButton fabFilter;
    private FloatingActionButton fabAdd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        transaksiViewModel = new ViewModelProvider(this).get(TransaksiViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaksi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvTransaksi);
        tvEmpty = view.findViewById(R.id.layout_empty_state);
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
    }

    @Override
    public void onStart() {
        super.onStart();

            // Observe the filtered transactions
            transaksiViewModel.getFilteredTransaksi().observe(getViewLifecycleOwner(), transaksiGroups -> {
                if (transaksiGroups != null && !transaksiGroups.isEmpty()) {
                    adapter.setTransaksiGroups(transaksiGroups);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            });
    }

    private void setupRecyclerView() {
        adapter = new TransaksiGroupAdapter();
        adapter.setHasStableIds(true);
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
        navigateToDetailTransaksi(transaksi);
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

    private void navigateToDetailTransaksi(Transaksi transaksi){
        // Create and show the DetailTransaksi
        Bundle args = new Bundle();
        args.putInt(DetailTransaksiFragment.ARG_TRANSAKSI_ID, transaksi.getId());
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.navigation_detail_transaksi, args, getNavOptions());

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectBottomNav(R.id.navigation_detail_transaksi);
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
