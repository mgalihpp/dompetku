package com.neurallift.keuanganku.ui.transaksi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.utils.FormatUtils;

public class TransaksiAdapter extends ListAdapter<Transaksi, TransaksiAdapter.TransaksiViewHolder> {

    private OnTransaksiClickListener listener;

    public TransaksiAdapter() {
        super(DIFF_CALLBACK);
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
                    && oldItem.getNominal() == newItem.getNominal();
        }
    };

    @NonNull
    @Override
    public TransaksiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaksi, parent, false);
        return new TransaksiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaksiViewHolder holder, int position) {
        Transaksi current = getItem(position);
        holder.bind(current);
    }

    public void setOnTransaksiClickListener(OnTransaksiClickListener listener) {
        this.listener = listener;
    }

    public interface OnTransaksiClickListener {

        void onTransaksiClick(Transaksi transaksi);

        void onTransaksiLongClick(Transaksi transaksi);
    }

    class TransaksiViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTanggal;
        private TextView tvJam;
        private TextView tvKategori;
        private TextView tvAkun;
        private TextView tvJenis;
        private TextView tvNominal;

        public TransaksiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvJam = itemView.findViewById(R.id.tvJam);
            tvKategori = itemView.findViewById(R.id.tvKategori);
            tvAkun = itemView.findViewById(R.id.tvAkun);
            tvJenis = itemView.findViewById(R.id.tvJenis);
            tvNominal = itemView.findViewById(R.id.tvNominal);

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

        public void bind(Transaksi transaksi) {
            tvTanggal.setText(FormatUtils.formatTanggalDenganHari(transaksi.getTanggal()));
            tvJam.setText(transaksi.getJam());
            tvKategori.setText(transaksi.getKategori());
            tvAkun.setText(transaksi.getAkun());
            tvJenis.setText(FormatUtils.capitalize(transaksi.getJenis()));

            String formattedNominal = FormatUtils.formatCurrency(transaksi.getNominal());
            tvNominal.setText(formattedNominal);

            // Set text color based on jenis (pemasukan/pengeluaran)
            if (transaksi.getJenis().equals("pemasukan")) {
                tvNominal.setTextColor(itemView.getContext().getResources().getColor(R.color.colorIncome));
            } else {
                tvNominal.setTextColor(itemView.getContext().getResources().getColor(R.color.colorExpense));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
