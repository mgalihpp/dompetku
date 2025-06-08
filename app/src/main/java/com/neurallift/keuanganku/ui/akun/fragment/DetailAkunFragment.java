package com.neurallift.keuanganku.ui.akun.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.ui.akun.viewmodel.DetailAkunViewModel;
import com.neurallift.keuanganku.ui.akun.adapter.TransaksiGroupAdapter;
import com.neurallift.keuanganku.ui.transaksi.fragment.DetailTransaksiFragment;
import com.neurallift.keuanganku.ui.transaksi.viewmodel.TransaksiViewModel;
import com.neurallift.keuanganku.utils.FormatUtils;

public class DetailAkunFragment extends Fragment implements TransaksiGroupAdapter.OnTransaksiClickListener {

    public static final String ARG_AKUN_ID = "akun_id";
    public static final String ARG_AKUN_NAMA = "akun_nama";

    private DetailAkunViewModel detailAkunViewModel;
    private TransaksiViewModel transaksiViewModel;
    private TransaksiGroupAdapter transaksiGroupAdapter;
    private RecyclerView rvTransaksi;
    private View layoutEmptyState;
    private ImageView tvIcon;
    private TextView tvNamaAkun;
    private TextView tvSaldoAkhir;
    private Toolbar toolbar;


    private int akunId;
    private String akunNama;

    public static DetailAkunFragment newInstance(int akunId, String akunNama) {
        DetailAkunFragment fragment = new DetailAkunFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_AKUN_ID, akunId);
        args.putString(ARG_AKUN_NAMA, akunNama);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            akunId = getArguments().getInt(ARG_AKUN_ID);
            akunNama = getArguments().getString(ARG_AKUN_NAMA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_akun, container, false);

        initViews(view);
        setupToolbar();
        setupRecyclerView();
        loadDetailAkun();

        return view;
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        tvIcon = view.findViewById(R.id.iv_icon);
        tvNamaAkun = view.findViewById(R.id.tv_nama_akun);
        tvSaldoAkhir = view.findViewById(R.id.tv_saldo_akhir);
        rvTransaksi = view.findViewById(R.id.rv_transaksi);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);


        final int[] icons = {
                android.R.drawable.ic_menu_camera,
                android.R.drawable.ic_menu_compass,
                android.R.drawable.ic_menu_directions,
                android.R.drawable.ic_menu_gallery,
                android.R.drawable.ic_menu_manage,
                android.R.drawable.ic_menu_call,
                android.R.drawable.ic_menu_send,
                android.R.drawable.ic_menu_view,
                android.R.drawable.ic_menu_agenda
        };

        int id = akunId;
        int index = Math.abs(id) % icons.length;
        tvIcon.setImageResource(icons[index]);
        ImageViewCompat.setImageTintList(tvIcon, ColorStateList.valueOf(Color.WHITE));

        tvNamaAkun.setText(akunNama);
    }

    private void setupToolbar(){
        toolbar.setNavigationOnClickListener(v ->{
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void setupRecyclerView(){
         transaksiGroupAdapter = new TransaksiGroupAdapter();
         transaksiGroupAdapter.setOnTransaksiClickListener(this);
        rvTransaksi.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTransaksi.setAdapter(transaksiGroupAdapter);
    }

    private void loadDetailAkun(){
        detailAkunViewModel = new ViewModelProvider(this).get(DetailAkunViewModel.class);
        transaksiViewModel = new ViewModelProvider(this).get(TransaksiViewModel.class);

        // Oberserve saldo akun
        detailAkunViewModel.getSaldoAkunById(akunId).observe(getViewLifecycleOwner(), saldo -> {
            if(saldo != null){
                tvSaldoAkhir.setText(FormatUtils.formatCurrency(saldo));

                // Set color based on value
                if(saldo >= 0){
                    tvSaldoAkhir.setTextColor(getResources().getColor(R.color.colorIncome));
                } else {
                    tvSaldoAkhir.setTextColor(getResources().getColor(R.color.colorExpense));
                }
            }
        });

        // Observe grouped transactions
        detailAkunViewModel.getGroupedTransaksiByAkunId(akunId).observe(getViewLifecycleOwner(), transaksiGroups -> {
            if (transaksiGroups != null && !transaksiGroups.isEmpty()) {
                transaksiGroupAdapter.setTransaksiGroups(transaksiGroups);
                rvTransaksi.setAdapter(transaksiGroupAdapter);
                rvTransaksi.setItemViewCacheSize(transaksiGroups.size());
                rvTransaksi.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
            } else {
                rvTransaksi.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onTransaksiClick(Transaksi transaksi) {
        // Handle click on transaksi
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
