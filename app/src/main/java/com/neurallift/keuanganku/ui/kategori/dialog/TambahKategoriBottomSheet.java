package com.neurallift.keuanganku.ui.kategori.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.data.model.Kategori;

public class TambahKategoriBottomSheet extends BottomSheetDialogFragment {

    private EditText etNamaKategori;
    private Button btnSimpan;
    private TextView tvTitle;
    private TextView tvSelection;

    private OnKategoriSavedListener onKategoriSavedListener;

    private Kategori existingKategori;
    private boolean isEditMode = false;


    public interface OnKategoriSavedListener {
        void onKategoriSaved(Kategori kategori);
        void onKategoriUpdated(Kategori kategori);
    }

    public void setOnKategoriSavedListener(OnKategoriSavedListener listener) {
        this.onKategoriSavedListener = listener;
    }

    public static TambahKategoriBottomSheet newInstance(Akun akun){
        TambahKategoriBottomSheet fragment = new TambahKategoriBottomSheet();

        Bundle args = new Bundle();
        args.putInt("id", akun.getId());
        args.putString("nama", akun.getNama());

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);

        // Check if in edit mode
        Bundle args = getArguments();
        if (args != null && args.containsKey("id")) {
            isEditMode = true;

            // Recreate the existing akun object
            existingKategori = new Kategori(
                    args.getString("nama")
            );
            existingKategori.setId(args.getInt("id"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_tambah_selection, container, false);

        etNamaKategori = view.findViewById(R.id.etSelection);
        btnSimpan = view.findViewById(R.id.btnSimpan);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvSelection = view.findViewById(R.id.tvSelection);


        tvTitle.setText(R.string.tambah_kategori);
        tvSelection.setText(R.string.kategori);

        if (isEditMode && existingKategori != null) {
            tvTitle.setText(R.string.edit_akun);
            etNamaKategori.setText(existingKategori.getNama());
            btnSimpan.setText(R.string.perbarui);
        }

        btnSimpan.setOnClickListener(v -> {
            saveAkun();
        });

        return view;
    }

    private void saveAkun(){
        String namaKategori = etNamaKategori.getText().toString().trim();

        if(TextUtils.isEmpty(namaKategori)){
            etNamaKategori.setError(getString(R.string.nama_akun_kosong));
        } else {
            if (onKategoriSavedListener != null) {
                if(isEditMode){
                    existingKategori.setNama(namaKategori);
                    onKategoriSavedListener.onKategoriUpdated(existingKategori);
                } else {
                    Kategori kategori = new Kategori(namaKategori);
                    onKategoriSavedListener.onKategoriSaved(kategori);
                }
            }

            dismiss();
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etNamaKategori.requestFocus();
    }
}
