package com.neurallift.keuanganku.ui.transaksi.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.ui.transaksi.dialog.TambahTransaksiBottomSheet;
import com.neurallift.keuanganku.ui.transaksi.viewmodel.TransaksiViewModel;
import com.neurallift.keuanganku.utils.DateTimeUtils;
import com.neurallift.keuanganku.utils.FormatUtils;

public class DetailTransaksiFragment extends Fragment {

    public static final String ARG_TRANSAKSI_ID = "transaksi_id";

    private TransaksiViewModel transaksiViewModel;
    private Transaksi transaksi;
    private Toolbar toolbar;
    private ImageView ivJenisIcon;
    private TextView tvNominal;
    private Chip chipPemasukan;
    private Chip chipPengeluaran;
    private TextView tvTanggal;
    private TextView tvJam;
    private TextView tvAkun;
    private TextView tvKategori;
    private TextView tvCatatan;
    FloatingActionButton fabMain;
    FloatingActionButton fabEdit;
    FloatingActionButton fabDelete;
    private MaterialButton btnShare;

    boolean isFabOpen = false;
    private int transaksiId;

    public static DetailTransaksiFragment newInstance(int transaksiId) {
        DetailTransaksiFragment fragment = new DetailTransaksiFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TRANSAKSI_ID, transaksiId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transaksiId = getArguments().getInt(ARG_TRANSAKSI_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_transaksi, container, false);

        initViews(view);
        setupToolbar();
        loadDataTransaksi();
        setupFab();

        return view;
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        ivJenisIcon = view.findViewById(R.id.ivJenisIcon);
        chipPemasukan = view.findViewById(R.id.chipPemasukan);
        chipPengeluaran = view.findViewById(R.id.chipPengeluaran);
        tvNominal = view.findViewById(R.id.tvNominal);
        tvTanggal = view.findViewById(R.id.tvTanggal);
        tvJam = view.findViewById(R.id.tvJam);
        tvAkun = view.findViewById(R.id.tvAkun);
        tvKategori = view.findViewById(R.id.tvKategori);
        tvCatatan = view.findViewById(R.id.tvCatatan);
        fabMain = view.findViewById(R.id.fab_main);
        fabEdit = view.findViewById(R.id.fab_edit);
        fabDelete = view.findViewById(R.id.fab_delete);
        btnShare = view.findViewById(R.id.button_share);
    }

    private void setupToolbar(){
        toolbar.setNavigationOnClickListener(v ->{
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void setupFab(){
        fabMain.setOnClickListener(v -> {
            if (isFabOpen) {
                fabEdit.animate().translationY(0).alpha(0f).setDuration(200).withEndAction(() -> fabEdit.setVisibility(View.GONE)).start();
                fabDelete.animate().translationY(0).alpha(0f).setDuration(200).withEndAction(() -> fabDelete.setVisibility(View.GONE)).start();
            } else {
                // Show with animation
                fabEdit.setVisibility(View.VISIBLE);
                fabDelete.setVisibility(View.VISIBLE);
                fabEdit.setAlpha(0f);
                fabDelete.setAlpha(0f);
                fabEdit.animate().translationY(-40f).alpha(1f).setDuration(200).start();
                fabDelete.animate().translationY(-30f).alpha(1f).setDuration(200).start();
            }
            isFabOpen = !isFabOpen;
        });

        fabEdit.setOnClickListener(v -> {
            showEditTransaksiDialog();
        });

        fabDelete.setOnClickListener(v -> {
            showDeleteTransaksiDialog();
        });

        btnShare.setOnClickListener(v -> {
            // Handle share button click
            if (transaksi != null) {
                String shareText = generateShareText(transaksi);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Detail Transaksi");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

                startActivity(Intent.createChooser(shareIntent, "Bagikan melalui"));
            } else {
                Toast.makeText(getContext(), "Transaksi belum dimuat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataTransaksi(){

        // Setup viewmodel
        transaksiViewModel = new ViewModelProvider(this).get(TransaksiViewModel.class);

        // Load data transaksi from database based on transaksiId
        transaksiViewModel.getTransaksiById(transaksiId).observe(getViewLifecycleOwner(), transaksi -> {
            this.transaksi = transaksi;
            if (transaksi != null) {
                // Update UI with transaksi data
                updateUi(transaksi);
            }
        });
    }

    private void updateUi(Transaksi transaksi){
        // Update UI with transaksi data
        tvNominal.setText(FormatUtils.formatCurrency(transaksi.getNominal()));
        tvTanggal.setText(DateTimeUtils.formatDateFull(transaksi.getTanggal()));
        tvJam.setText(transaksi.getJam());
        tvAkun.setText(transaksi.getAkun());
        tvKategori.setText(transaksi.getKategori());
        tvCatatan.setText(transaksi.getCatatan().isEmpty() ? "-" : transaksi.getCatatan());

        // Set icon and text color based on jenis
        if (transaksi.getJenis().equals("pemasukan")) {
            ivJenisIcon.setImageResource(R.drawable.ic_arrow_upward);
            chipPengeluaran.setVisibility(View.GONE);
            chipPemasukan.setChecked(true);
            tvNominal.setTextColor(getResources().getColor(R.color.colorIncome));
        } else {
            ivJenisIcon.setImageResource(R.drawable.ic_arrow_downward);
            chipPemasukan.setVisibility(View.GONE);
            chipPengeluaran.setChecked(true);
            tvNominal.setTextColor(getResources().getColor(R.color.colorExpense));
        }
    }

    private void showEditTransaksiDialog() {
        TambahTransaksiBottomSheet bottomSheet = TambahTransaksiBottomSheet.newInstance(transaksi);
        bottomSheet.show(getChildFragmentManager(), "editTransaksi");
    }

    private void showDeleteTransaksiDialog() {
        // Show delete confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi")
                .setMessage(getString(R.string.konfirmasi_hapus_transaksi))
                .setPositiveButton(getString(R.string.hapus), (dialog, which) -> {
                    transaksiViewModel.delete(transaksi);
                    Toast.makeText(getContext(), getString(R.string.transaksi_dihapus), Toast.LENGTH_SHORT).show();

                    // Navigate back to the previous fragment
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .setNegativeButton(getString(R.string.batal), null)
                .show();
    }

    private String generateShareText(Transaksi transaksi) {
        return "📄 Detail Transaksi:\n" +
                "💰 Nominal: " + FormatUtils.formatCurrency(transaksi.getNominal()) + "\n" +
                "📂 Jenis: " + transaksi.getJenis().toUpperCase() + "\n" +
                "🏦 Akun: " + transaksi.getAkun() + "\n" +
                "🗃️ Kategori: " + transaksi.getKategori() + "\n" +
                "📝 Catatan: " + (transaksi.getCatatan().isEmpty() ? "-" : transaksi.getCatatan() + "\n" +
                "🗓️ Tanggal: " + DateTimeUtils.formatDateFull(transaksi.getTanggal()) + "\n" +
                "⏰ Jam: " + transaksi.getJam() + "\n");
    }

}
