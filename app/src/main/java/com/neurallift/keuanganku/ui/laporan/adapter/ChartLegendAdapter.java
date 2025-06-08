package com.neurallift.keuanganku.ui.laporan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.ui.laporan.model.ChartLegendItem;
import com.neurallift.keuanganku.utils.FormatUtils;

import java.util.Comparator;
import java.util.List;

public class ChartLegendAdapter extends RecyclerView.Adapter<ChartLegendAdapter.ViewHolder> {
    private final List<ChartLegendItem> legendItems;

    public ChartLegendAdapter(List<ChartLegendItem> legendItems) {
        this.legendItems = legendItems;
        this.legendItems.sort(Comparator.comparingDouble(ChartLegendItem::getNominal).reversed());
    }

    @NonNull
    @Override
    public ChartLegendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chart_legend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartLegendAdapter.ViewHolder holder, int position) {
        ChartLegendItem item = legendItems.get(position);
        holder.colorIndicator.setBackgroundColor(item.getColor());
        holder.tvCategoryName.setText(item.getKategori());
        holder.tvAmount.setText(FormatUtils.formatCurrency(item.getNominal()));
    }

    @Override
    public int getItemCount() {
        return legendItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View colorIndicator;
        TextView tvCategoryName;
        TextView tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
