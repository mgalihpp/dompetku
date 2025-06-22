package com.neurallift.keuanganku.ui.kasir.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.tabs.TabLayout;
import com.neurallift.keuanganku.MainActivity;
import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.data.model.Barang;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.data.repository.AkunRepository;
import com.neurallift.keuanganku.ui.akun.viewmodel.AkunViewModel;
import com.neurallift.keuanganku.ui.transaksi.viewmodel.TransaksiViewModel;
import com.neurallift.keuanganku.utils.DateTimeUtils;
import com.neurallift.keuanganku.utils.FormatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KasirPembayaranFragment extends Fragment {

    public static final String ARG_TOTAL_PEMBAYARAN = "total_pembayaran";
    public static final String ARGS_BARANG_LIST = "barangList";
    public static final String ARGS_JUMLAH_LIST = "jumlahList";

    private double totalPembayaran;
    private final Map<Barang, Integer> selectedBarangMap = new HashMap<>();

    private TransaksiViewModel transaksiViewModel;
    private AkunViewModel akunViewModel;
    private Toolbar toolbar;
    private TextView tv_total;
    private TabLayout tab_layout;
    private LinearLayout layoutTunai;
    private LinearLayout layoutNonTunai;
    private EditText etNominal;
    private TextView tv_change;
    private Button btn_exact_amount;
    private Button btn_finish;

    private boolean isTunai = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            ArrayList<Barang> barangList = getArguments().getParcelableArrayList(ARGS_BARANG_LIST);
            int[] jumlahList = getArguments().getIntArray(ARGS_JUMLAH_LIST);
            totalPembayaran = getArguments().getFloat(ARG_TOTAL_PEMBAYARAN);

            if (barangList != null && jumlahList != null) {
                for (int i = 0; i < barangList.size(); i++) {
                    selectedBarangMap.put(barangList.get(i), jumlahList[i]);
                }
            }
        }

        transaksiViewModel = new ViewModelProvider(this).get(TransaksiViewModel.class);
        akunViewModel = new ViewModelProvider(this).get(AkunViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kasir_pembayaran, container, false);

        initViews(view);
        setupToolbar();
        setupTabLayout();
        setupButtonListeners();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    try {
                        double jumlah = Double.parseDouble(s.toString());
                        double kembalian = jumlah - totalPembayaran;

                        if(jumlah < totalPembayaran){
                            return;
                        }

                        tv_change.setText(FormatUtils.formatCurrency(kembalian));
                    } catch (NumberFormatException e) {
                        tv_change.setText(FormatUtils.formatCurrency(0));
                    }
                } else {
                    tv_change.setText(FormatUtils.formatCurrency(0));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    void initViews(View view){
        toolbar = view.findViewById(R.id.toolbar);
        tv_total = view.findViewById(R.id.tv_total);
        tab_layout = view.findViewById(R.id.tab_layout);
        layoutTunai = view.findViewById(R.id.layout_payment_tunai);
        layoutNonTunai = view.findViewById(R.id.layout_payment_nontunai);
        etNominal = view.findViewById(R.id.etNominal);
        tv_change = view.findViewById(R.id.tv_change);
        btn_exact_amount = view.findViewById(R.id.btn_exact_amount);
        btn_finish = view.findViewById(R.id.btn_finish);

        tv_total.setText(FormatUtils.formatCurrency(totalPembayaran));
    }

    void setupToolbar(){
        toolbar.setNavigationOnClickListener(v ->{
            Navigation.findNavController(v).navigateUp();
        });
    }

    void setupTabLayout() {
        layoutTunai.setVisibility(View.VISIBLE);
        layoutNonTunai.setVisibility(View.GONE);

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    layoutTunai.setVisibility(View.VISIBLE);
                    layoutNonTunai.setVisibility(View.GONE);
                    isTunai = true;
                } else {
                    layoutTunai.setVisibility(View.GONE);
                    layoutNonTunai.setVisibility(View.VISIBLE);
                    isTunai = false;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Optional: tidak perlu isi
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optional: tidak perlu isi
            }
        });
    }

    void setupButtonListeners() {
        btn_exact_amount.setOnClickListener(v -> {
            simpanTransaksi();
            navigateToKasirInvoice();
        });

        btn_finish.setOnClickListener(v -> {

            double nominal = Double.parseDouble(etNominal.getText().toString().trim().isEmpty() ? "0" : etNominal.getText().toString().trim());

            if(nominal < totalPembayaran && isTunai){
                Toast.makeText(getContext(), "Mohon isi nominal", Toast.LENGTH_SHORT).show();
                return;
            }

            simpanTransaksi();
            navigateToKasirInvoice();
        });
    }

    void navigateToKasirInvoice(){
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

        float nominal = Float.parseFloat(etNominal.getText().toString().isEmpty() ? String.valueOf(totalPembayaran) : etNominal.getText().toString());

        args.putFloat("total_pembayaran", (float) totalHarga);
        args.putString("metode_pembayaran", isTunai ? "Tunai" : "Non Tunai");
        args.putParcelableArrayList("barangList", barangList);
        args.putIntArray("jumlahList", jumlahList);
        args.putFloat("total_dibayar", nominal);
        args.putFloat("total_kembalian", (float) Double.parseDouble(tv_change.getText().toString()
                .replace("Rp", "")
                .replace(".", "")
                .trim()));

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.navigation_kasir_invoice, args, getNavOptions());

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectBottomNav(R.id.navigation_kasir);
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

    private void simpanTransaksi(){

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = inputFormat.format(new Date()); // "2025-06-21"

        String tanggal = DateTimeUtils.formatDateFull(todayStr);
        String jam = DateTimeUtils.formatTime(new Date());
        String akunNama = isTunai ? "Kas" : "QRIS";

        observeOnce(akunViewModel.getAkunByNama(akunNama), getViewLifecycleOwner(), akun -> {
            if (akun == null) {
                akunViewModel.insert(new Akun(akunNama));
            }
        });


        String jenis = "pemasukan";

        for (Map.Entry<Barang, Integer> entry : selectedBarangMap.entrySet()) {
            String kategori = entry.getKey().getKategori();
            String catatan = "Penjualan " + entry.getValue() + " " + entry.getKey().getSatuan()  + " " + entry.getKey().getNama();

            Transaksi transaksi = new Transaksi(
                    tanggal,
                    jam,
                    kategori,
                    akunNama,
                    jenis,
                    entry.getKey().getHarga() * entry.getValue(),
                    catatan
            );
            transaksiViewModel.insert(transaksi);

            Toast.makeText(getContext(), R.string.transaksi_disimpan, Toast.LENGTH_LONG).show();
        }
    }

    public static <T> void observeOnce(LiveData<T> liveData, LifecycleOwner owner, Observer<T> observer) {
        liveData.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(T t) {
                observer.onChanged(t);
                liveData.removeObserver(this);
            }
        });
    }

}
