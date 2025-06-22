package com.neurallift.keuanganku.ui.kasir.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Barang;
import com.neurallift.keuanganku.utils.FormatUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BarangInvoiceAdapter extends RecyclerView.Adapter<BarangInvoiceAdapter.BarangInvoiceViewHolder> {
    private List<Barang> barangList = new ArrayList<>();
    private List<Integer> jumlahList = new ArrayList<>();

    public void setData(List<Barang> barangList, List<Integer> jumlahList){
        this.barangList = barangList;
        this.jumlahList = jumlahList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BarangInvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kasir_invoice, parent, false);
        return new BarangInvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarangInvoiceViewHolder holder, int position) {
        Barang barang = barangList.get(position);
        int jumlah = jumlahList.get(position);
        holder.bind(barang, jumlah);
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    class BarangInvoiceViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_nama_barang;
        private TextView tv_detail_harga;
        private TextView tv_total_harga;

        public BarangInvoiceViewHolder(View itemView) {
            super(itemView);
            tv_nama_barang = itemView.findViewById(R.id.tv_nama_barang);
            tv_detail_harga = itemView.findViewById(R.id.tv_detail_harga);
            tv_total_harga = itemView.findViewById(R.id.tv_total_harga);
        }

        public void bind(Barang barang, int jumlah){
            DecimalFormat decimalFormat = new DecimalFormat("#,###", new DecimalFormatSymbols(new Locale("in", "ID")));
            String formattedHarga = decimalFormat.format(barang.getHarga());

            tv_nama_barang.setText(barang.getNama());
            tv_detail_harga.setText(jumlah + " x " + formattedHarga);
            tv_total_harga.setText(FormatUtils.formatCurrency(barang.getHarga() * jumlah));
        }

    }
}
