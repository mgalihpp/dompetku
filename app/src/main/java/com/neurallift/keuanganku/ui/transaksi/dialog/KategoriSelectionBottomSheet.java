package com.neurallift.keuanganku.ui.transaksi.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Kategori;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.neurallift.keuanganku.ui.akun.dialog.TambahAkunBottomSheet;
import com.neurallift.keuanganku.ui.kategori.dialog.TambahKategoriBottomSheet;
import com.neurallift.keuanganku.ui.transaksi.viewmodel.TransaksiViewModel;

import java.util.ArrayList;
import java.util.List;

public class KategoriSelectionBottomSheet extends BottomSheetDialogFragment implements TambahKategoriBottomSheet.OnKategoriSavedListener {

    private TransaksiViewModel transaksiViewModel;
    private RecyclerView recyclerView;
    private KategoriAdapter adapter;
    private KategoriSelectedListener listener;
    private Button btnTambahKategori;
    private ImageView btnEditKategori;

    public interface KategoriSelectedListener {

        void onKategoriSelected(String kategori);
    }

    public void setKategoriSelectedListener(KategoriSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_selection, container, false);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.pilih_kategori);

        recyclerView = view.findViewById(R.id.recyclerView);
        btnTambahKategori = view.findViewById(R.id.tambah_selection);
        btnEditKategori = view.findViewById(R.id.edit_kategori);

        btnTambahKategori.setText(R.string.tambah_kategori);
        btnEditKategori.setVisibility(View.VISIBLE);

        setupRecyclerView();

        transaksiViewModel = new ViewModelProvider(requireActivity()).get(TransaksiViewModel.class);

        // Observe kategori list
        transaksiViewModel.getAllKategori().observe(getViewLifecycleOwner(), kategoris -> {
            List<String> kategoriNames = new ArrayList<>();
            for (Kategori kategori : kategoris) {
                kategoriNames.add(kategori.getNama());
            }
            adapter.setItems(kategoriNames);
        });


        btnTambahKategori.setOnClickListener(v -> {
            showTambahKategoriBottomSheet();
        });

        btnEditKategori.setOnClickListener(v -> {
            navigateToKategori();
        });

        return view;
    }

    private void setupRecyclerView() {
        adapter = new KategoriAdapter(item -> {
            if (listener != null) {
                listener.onKategoriSelected(item);
            }
            dismiss();
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    // Inner adapter class for kategori list
    private static class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder> {

        private List<String> items = new ArrayList<>();
        private OnItemClickListener listener;

        public interface OnItemClickListener {

            void onItemClick(String item);
        }

        public KategoriAdapter(OnItemClickListener listener) {
            this.listener = listener;
        }

        public void setItems(List<String> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_selection, parent, false);
            return new KategoriViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull KategoriViewHolder holder, int position) {
            holder.textView.setText(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class KategoriViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            public KategoriViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.tvNama_item);

                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(items.get(position));
                    }
                });
            }
        }
    }
    private void showTambahKategoriBottomSheet(){
        TambahKategoriBottomSheet bottomSheet = new TambahKategoriBottomSheet();
        bottomSheet.setOnKategoriSavedListener(this);

        bottomSheet.show(getParentFragmentManager(), "TambahKategoriBottomSheet");
    }

    private void navigateToKategori() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.navigation_kategori, null, getNavOptions());
    }

    private NavOptions getNavOptions() {
        return new NavOptions.Builder()
                .setEnterAnim(R.anim.enter_from_right)
                .setExitAnim(R.anim.exit_to_left)
                .setPopEnterAnim(R.anim.enter_from_left)
                .setPopExitAnim(R.anim.exit_to_right)
                .build();
    }

    @Override
    public void onKategoriSaved(Kategori kategori) {
        transaksiViewModel.insert(kategori);
    }

    @Override
    public void onKategoriUpdated(Kategori kategori) {
        transaksiViewModel.update(kategori);
    }
}
