package com.neurallift.keuanganku.ui.transaksi.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Akun;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.neurallift.keuanganku.ui.akun.viewmodel.AkunViewModel;
import com.neurallift.keuanganku.ui.akun.dialog.TambahAkunBottomSheet;
import com.neurallift.keuanganku.ui.transaksi.viewmodel.TransaksiViewModel;

import java.util.ArrayList;
import java.util.List;

public class AkunSelectionBottomSheet extends BottomSheetDialogFragment implements TambahAkunBottomSheet.OnAkunSavedListener {

    private TransaksiViewModel transaksiViewModel;
    private AkunViewModel akunViewModel;
    private RecyclerView recyclerView;

    private Button btnTambahAkun;
    private AkunAdapter adapter;
    private AkunSelectedListener listener;

    public interface AkunSelectedListener {

        void onAkunSelected(String akun);
    }

    public void setAkunSelectedListener(AkunSelectedListener listener) {
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
        tvTitle.setText(R.string.pilih_akun);

        recyclerView = view.findViewById(R.id.recyclerView);
        btnTambahAkun = view.findViewById(R.id.tambah_selection);
        btnTambahAkun.setText(R.string.tambah_akun);

        setupRecyclerView();

        transaksiViewModel = new ViewModelProvider(requireActivity()).get(TransaksiViewModel.class);
        akunViewModel = new ViewModelProvider(requireActivity()).get(AkunViewModel.class);

        // Observe akun list
        transaksiViewModel.getAllAkun().observe(getViewLifecycleOwner(), akuns -> {
            List<String> akunNames = new ArrayList<>();
            for (Akun akun : akuns) {
                akunNames.add(akun.getNama());
            }
            adapter.setItems(akunNames);
        });

        btnTambahAkun.setOnClickListener(v -> {
            showTambahAkunBottomSheet();
        });

        return view;
    }

    private void setupRecyclerView() {
        adapter = new AkunAdapter(item -> {
            if (listener != null) {
                listener.onAkunSelected(item);
            }
            dismiss();
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void showTambahAkunBottomSheet(){
        TambahAkunBottomSheet bottomSheet = new TambahAkunBottomSheet();
        bottomSheet.setOnAkunSavedListener(this);

        bottomSheet.show(getParentFragmentManager(), "TambahAkunBottomSheet");
    }

    // Inner adapter class for akun list
    private static class AkunAdapter extends RecyclerView.Adapter<AkunAdapter.AkunViewHolder> {

        private List<String> items = new ArrayList<>();
        private OnItemClickListener listener;

        public interface OnItemClickListener {

            void onItemClick(String item);
        }

        public AkunAdapter(OnItemClickListener listener) {
            this.listener = listener;
        }

        public void setItems(List<String> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AkunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_selection, parent, false);
            return new AkunViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AkunViewHolder holder, int position) {
            holder.textView.setText(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class AkunViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            public AkunViewHolder(@NonNull View itemView) {
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
