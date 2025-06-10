package com.neurallift.keuanganku.ui.akun.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.ui.akun.model.AkunWithSaldo;
import com.neurallift.keuanganku.utils.FormatUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AkunAdapter extends RecyclerView.Adapter<AkunAdapter.AkunViewHolder> {

    private List<AkunWithSaldo> akunList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(AkunWithSaldo akun);
    }

    public interface OnEditClickListener {
        void onEditClick(AkunWithSaldo akun);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(AkunWithSaldo akun);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public void setAkunList(List<AkunWithSaldo> akunList) {
        this.akunList = akunList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AkunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_akun, parent, false);
        return new AkunViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AkunViewHolder holder, int position) {
        AkunWithSaldo akunWithSaldo = akunList.get(position);
        holder.bind(akunWithSaldo);
    }

    @Override
    public int getItemCount() {
        return akunList.size();
    }

    class AkunViewHolder extends RecyclerView.ViewHolder {
        private ImageView tvIcon;
        private TextView tvNamaAkun;
        private TextView tvSaldo;
        private ImageButton btnMenu;

        public AkunViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.iv_icon);
            tvNamaAkun = itemView.findViewById(R.id.tv_nama_akun);
            tvSaldo = itemView.findViewById(R.id.tv_saldo);
            btnMenu = itemView.findViewById(R.id.btn_menu);
        }

        public void bind(AkunWithSaldo akunWithSaldo) {
            final int[] icons = {
                    android.R.drawable.ic_menu_camera,
                    android.R.drawable.ic_menu_compass,
                    android.R.drawable.ic_menu_directions,
                    android.R.drawable.ic_menu_gallery,
                    android.R.drawable.ic_menu_manage,
                    android.R.drawable.ic_menu_call,
                    android.R.drawable.ic_menu_send,
                    android.R.drawable.ic_menu_view,
                    android.R.drawable.ic_menu_agenda
            };

            int id = akunWithSaldo.getAkun().getId();
            int index = Math.abs(id) % icons.length;

            tvIcon.setImageResource(icons[index]);
            ImageViewCompat.setImageTintList(tvIcon, ColorStateList.valueOf(Color.WHITE));

            tvNamaAkun.setText(akunWithSaldo.getAkun().getNama());

            // Format saldo dengan IDR currency
             String formattedSaldo = FormatUtils.formatCurrency(akunWithSaldo.getSaldo());
            tvSaldo.setText(formattedSaldo);

            // Set color based on saldo (red for negative, green for positive)
            if (akunWithSaldo.getSaldo() < 0) {
                tvSaldo.setTextColor(itemView.getResources().getColor(R.color.colorExpense));
            } else {
                tvSaldo.setTextColor(itemView.getResources().getColor(R.color.colorIncome));
            }

            // Item click
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(akunWithSaldo);
                }
            });

            // Menu click
            btnMenu.setOnClickListener(v -> showPopupMenu(v, akunWithSaldo));
        }

        private void showPopupMenu(View view, AkunWithSaldo akunWithSaldo) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.menu_akun_item);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (item.getItemId() == R.id.action_edit) {
                        if (onEditClickListener != null) {
                            onEditClickListener.onEditClick(akunWithSaldo);
                        }
                        return true;
                    }

                    if (item.getItemId() == R.id.action_hapus) {
                        if (onDeleteClickListener != null) {
                            onDeleteClickListener.onDeleteClick(akunWithSaldo);
                        }
                        return true;
                    }
                    return false;
                }
            });

            popupMenu.show();
        }
    }
}