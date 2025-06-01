package com.neurallift.keuanganku.ui.akun.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.ui.akun.model.TransaksiGroup;

import java.util.ArrayList;
import java.util.List;

public class TransaksiGroupAdapter extends RecyclerView.Adapter<TransaksiGroupAdapter.GroupViewHolder>  {

    private TransaksiDetailAdapter transaksiDetailAdapter;
    private List<TransaksiGroup> transaksiGroups = new ArrayList<>();
    private OnTransaksiClickListener listener;

    public void setTransaksiGroups(List<TransaksiGroup> transaksiGroups){
        this.transaksiGroups = transaksiGroups;
        notifyDataSetChanged();
    }

    public interface OnTransaksiClickListener {
        void onTransaksiClick(Transaksi transaksi);
        void onTransaksiLongClick(Transaksi transaksi);
    }

    // Set listener
    public void setOnTransaksiClickListener(OnTransaksiClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaksi_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        TransaksiGroup group = transaksiGroups.get(position);
        holder.bind(group);
    }

    @Override
    public int getItemCount() {
        return transaksiGroups.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTanggalGroup;
        private TextView tvTotalTransaksi;
        private RecyclerView rvTransaksiItems;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggalGroup = itemView.findViewById(R.id.tv_tanggal_group);
            tvTotalTransaksi = itemView.findViewById(R.id.tv_total_transaksi);
            rvTransaksiItems = itemView.findViewById(R.id.rv_transaksi_items);

            setupRecyclerView();
        }

        private void setupRecyclerView(){
            transaksiDetailAdapter = new TransaksiDetailAdapter();
            transaksiDetailAdapter.setOnTransaksiClickListener(listener);
            rvTransaksiItems.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            rvTransaksiItems.setAdapter(transaksiDetailAdapter);
            rvTransaksiItems.setNestedScrollingEnabled(false);
        }

        public void bind(TransaksiGroup group){
            tvTanggalGroup.setText(group.getTanggal());

            int jumlahTransaksi = group.getJumlahTransaksi();
            String totalText = jumlahTransaksi + " transaksi";
            tvTotalTransaksi.setText(totalText);

            transaksiDetailAdapter.submitList(new ArrayList<>(group.getTransaksiList()));
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
