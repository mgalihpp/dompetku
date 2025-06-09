package com.neurallift.keuanganku.ui.akun.dialog;

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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.neurallift.keuanganku.R;
import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.ui.akun.viewmodel.AkunViewModel;

public class TambahAkunBottomSheet extends BottomSheetDialogFragment {

    private EditText etNamaAkun;
    private Button btnSimpan;
    private TextView tvTitle;
    private TextView tvSelection;

    private OnAkunSavedListener onAkunSavedListener;

    private Akun existingAkun;
    private boolean isEditMode = false;


    public interface OnAkunSavedListener {
        void onAkunSaved(Akun akun);
        void onAkunUpdated(Akun akun);
    }

    public void setOnAkunSavedListener(OnAkunSavedListener listener) {
        this.onAkunSavedListener = listener;
    }

    public static TambahAkunBottomSheet newInstance(Akun akun){
        TambahAkunBottomSheet fragment = new TambahAkunBottomSheet();

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
            existingAkun = new Akun(
                    args.getString("nama")
            );
            existingAkun.setId(args.getInt("id"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_tambah_selection, container, false);

        etNamaAkun = view.findViewById(R.id.etSelection);
        btnSimpan = view.findViewById(R.id.btnSimpan);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvSelection = view.findViewById(R.id.tvSelection);


        tvTitle.setText(R.string.tambah_akun);
        tvSelection.setText(R.string.akun);

        if (isEditMode && existingAkun != null) {
            tvTitle.setText(R.string.edit_akun);
            etNamaAkun.setText(existingAkun.getNama());
            btnSimpan.setText(R.string.perbarui);
        }

        btnSimpan.setOnClickListener(v -> {
            saveAkun();
        });

        return view;
    }

    private void saveAkun(){
        String namaAkun = etNamaAkun.getText().toString().trim();

        if(TextUtils.isEmpty(namaAkun)){
            etNamaAkun.setError(getString(R.string.nama_akun_kosong));
            return;
        }

        if (onAkunSavedListener != null) {
            AkunViewModel akunViewModel = new ViewModelProvider(requireActivity()).get(AkunViewModel.class);

            akunViewModel.getAkunByNama(namaAkun).observe(getViewLifecycleOwner(), exitsAkun -> {
                if (exitsAkun == null) {
                    if(isEditMode){
                        existingAkun.setNama(namaAkun);
                        onAkunSavedListener.onAkunUpdated(existingAkun);
                    } else {
                        Akun akun = new Akun(namaAkun);
                        onAkunSavedListener.onAkunSaved(akun);
                    }

                    dismiss();
                } else {
                    etNamaAkun.setError(getString(R.string.ada_akun));
                }
            });
        }

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etNamaAkun.requestFocus();
    }
}
