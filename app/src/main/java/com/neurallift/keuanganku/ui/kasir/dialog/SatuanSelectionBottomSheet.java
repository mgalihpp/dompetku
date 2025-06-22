package com.neurallift.keuanganku.ui.kasir.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.neurallift.keuanganku.R;

import java.util.ArrayList;
import java.util.List;

public class SatuanSelectionBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private SatuanAdapter adapter;
    private Button btnTambahSatuan;
    private SatuanSelectedListener listener;

    public interface SatuanSelectedListener {

        void onSatuanSelected(String satuan);
    }

    public void setSatuanSelectedListener(SatuanSelectionBottomSheet.SatuanSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_selection, container, false);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.pilih_satuan);

        recyclerView = view.findViewById(R.id.recyclerView);
        btnTambahSatuan = view.findViewById(R.id.tambah_selection);
        btnTambahSatuan.setText(R.string.tambah_satuan);
        btnTambahSatuan.setVisibility(View.GONE);

        setupRecyclerView();

        String[] satuan = getAllSatuan();
        List<String> satuanList = new ArrayList<>(List.of(satuan));
        adapter.setItems(satuanList);

        return view;
    }

    private void setupRecyclerView() {
        adapter = new SatuanAdapter(item -> {
            if (listener != null) {
                listener.onSatuanSelected(item);
            }
            dismiss();
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private String[] getAllSatuan() {
        return new String[] {
                "Ampul",
                "Bale",
                "Batang",
                "Biji",
                "Botol",
                "Box",
                "Bungkus",
                "Butir",
                "Centimeter",
                "Dus",
                "Ekor",
                "Gal",
                "Gram",
                "Ikat",
                "Kaleng",
                "Kantong",
                "Kapsul",
                "Karton",
                "Kg",
                "Kodi",
                "Liter",
                "Lembar",
                "Meter",
                "Pack",
                "Pcs",
                "Porsi",
                "Pot",
                "Rim",
                "Roll",
                "Sachet",
                "Set",
                "Tabung",
                "Tablet",
                "Ton",
                "Tube",
                "Unit"
        };
    }


    private static class SatuanAdapter extends RecyclerView.Adapter<SatuanSelectionBottomSheet.SatuanAdapter.SatuanViewHolder> {

        private List<String> items = new ArrayList<>();
        private SatuanSelectionBottomSheet.SatuanAdapter.OnItemClickListener listener;

        public interface OnItemClickListener {

            void onItemClick(String item);
        }

        public SatuanAdapter(SatuanSelectionBottomSheet.SatuanAdapter.OnItemClickListener listener) {
            this.listener = listener;
        }

        public void setItems(List<String> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public SatuanSelectionBottomSheet.SatuanAdapter.SatuanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_selection, parent, false);
            return new SatuanSelectionBottomSheet.SatuanAdapter.SatuanViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SatuanSelectionBottomSheet.SatuanAdapter.SatuanViewHolder holder, int position) {
            holder.textView.setText(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class SatuanViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            public SatuanViewHolder(@NonNull View itemView) {
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

}
