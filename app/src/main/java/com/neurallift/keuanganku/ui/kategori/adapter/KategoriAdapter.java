package com.neurallift.keuanganku.ui.kategori.adapter;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Kategori;

import java.util.ArrayList;
import java.util.List;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder> {

    private List<Kategori> kategoriList = new ArrayList<>();

    private KategoriAdapter.OnItemClickListener onItemClickListener;
    private KategoriAdapter.OnEditClickListener onEditClickListener;
    private KategoriAdapter.OnDeleteClickListener onDeleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(Kategori kategori);
    }

    public interface OnEditClickListener {
        void onEditClick(Kategori kategori);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Kategori kategori);
    }

    public void setOnItemClickListener(KategoriAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnEditClickListener(KategoriAdapter.OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }

    public void setOnDeleteClickListener(KategoriAdapter.OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public void setKategoriList(List<Kategori> kategoriList) {
        this.kategoriList = kategoriList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KategoriAdapter.KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kategori, parent, false);
        return new KategoriViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriAdapter.KategoriViewHolder holder, int position) {
        Kategori kategori = kategoriList.get(position);
        holder.bind(kategori);
    }

    @Override
    public int getItemCount() {
        return kategoriList.size();
    }

    class KategoriViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNamaKategori;
        private ImageButton btnMenu;

        public KategoriViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaKategori = itemView.findViewById(R.id.tv_nama_kategori);
            btnMenu = itemView.findViewById(R.id.btn_menu);
        }

        public void bind(Kategori kategori) {
            tvNamaKategori.setText(kategori.getNama());


            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(kategori);
                }
            });

            btnMenu.setOnClickListener(v -> showPopupMenu(v, kategori));
        }

        private void showPopupMenu(View view, Kategori kategori) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.menu_akun_item);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (item.getItemId() == R.id.action_edit) {
                        if (onEditClickListener != null) {
                            onEditClickListener.onEditClick(kategori);
                        }
                        return true;
                    }

                    if (item.getItemId() == R.id.action_hapus) {
                        if (onDeleteClickListener != null) {
                            onDeleteClickListener.onDeleteClick(kategori);
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
