package com.neurallift.keuanganku.ui.akun.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class TransaksiDetailAdapter extends ListAdapter<Transaksi, TransaksiDetailAdapter.DetailViewHolder> {

    private TransaksiGroupAdapter.OnTransaksiClickListener listener;
    private List<Transaksi> transaksiList = new ArrayList<>();

    public TransaksiDetailAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setTransaksiList(List<Transaksi> transaksiList){
        this.transaksiList = transaksiList;
        notifyDataSetChanged();
    }

    public void setOnTransaksiClickListener(TransaksiGroupAdapter.OnTransaksiClickListener listener) {
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Transaksi> DIFF_CALLBACK = new DiffUtil.ItemCallback<Transaksi>() {
        @Override
        public boolean areItemsTheSame(@NonNull Transaksi oldItem, @NonNull Transaksi newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Transaksi oldItem, @NonNull Transaksi newItem) {
            return oldItem.getTanggal().equals(newItem.getTanggal())
                    && oldItem.getJam().equals(newItem.getJam())
                    && oldItem.getKategori().equals(newItem.getKategori())
                    && oldItem.getAkun().equals(newItem.getAkun())
                    && oldItem.getJenis().equals(newItem.getJenis())
                    && oldItem.getNominal() == newItem.getNominal()
                    && oldItem.getCatatan().equals(newItem.getCatatan());
        }
    };

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaksi_detail, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        Transaksi transaksi = getItem(position);
        holder.bind(transaksi);
    }

    class DetailViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivJenisIcon;
        private TextView tvKategori;
        private TextView tvCatatan;
        private TextView tvJam;
        private TextView tvNominal;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            ivJenisIcon = itemView.findViewById(R.id.iv_jenis_icon);
            tvKategori = itemView.findViewById(R.id.tv_kategori);
            tvCatatan = itemView.findViewById(R.id.tv_catatan);
            tvJam = itemView.findViewById(R.id.tv_jam);
            tvNominal = itemView.findViewById(R.id.tv_nominal);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onTransaksiClick(getItem(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onTransaksiLongClick(getItem(position));
                    return true;
                }
                return false;
            });
        }

        public void bind(Transaksi transaksi){
            tvKategori.setText(transaksi.getKategori());

            // Set catatan
            if (transaksi.getCatatan() != null && !transaksi.getCatatan().trim().isEmpty()) {
                tvCatatan.setText(transaksi.getCatatan());
                tvCatatan.setVisibility(View.VISIBLE);
            } else {
                tvCatatan.setVisibility(View.GONE);
            }

            // Set jam
            tvJam.setText(transaksi.getJam());

            // Set nominal with currency format
            String formattedNominal = (FormatUtils.formatCurrency(transaksi.getNominal()));

            if (transaksi.getJenis().equals("pemasukan")) {
                tvNominal.setText("+" + formattedNominal);
                tvNominal.setTextColor(itemView.getContext().getResources().getColor(R.color.colorIncome)); // Green
                ivJenisIcon.setImageResource(R.drawable.ic_arrow_upward);
            } else {
                tvNominal.setText("-" + formattedNominal);
                tvNominal.setTextColor(itemView.getContext().getResources().getColor(R.color.colorExpense));
                ivJenisIcon.setImageResource(R.drawable.ic_arrow_downward);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
