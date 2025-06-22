package com.neurallift.keuanganku.ui.kasir.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.neurallift.keuanganku.data.model.Barang;
import com.neurallift.keuanganku.data.repository.BarangRepository;

import java.util.List;

public class KasirViewModel extends AndroidViewModel {

    private BarangRepository barangRepository;
    private final MutableLiveData<String> selectedKategori = new MutableLiveData<>("");
    private final LiveData<List<Barang>> filteredBarangList;

    public KasirViewModel(@NonNull Application application) {
        super(application);
        barangRepository = new BarangRepository(application);

        filteredBarangList = Transformations.switchMap(selectedKategori, kategori -> {
            if (kategori == null || kategori.isEmpty() || kategori.equalsIgnoreCase("Semua")) {
                return barangRepository.getAllBarang();
            } else {
                return barangRepository.getBarangByKategori(kategori);
            }
        });
    }

    public LiveData<List<Barang>> getBarangFiltered() {
        return filteredBarangList;
    }

    public void setKategori(String kategori) {
        selectedKategori.setValue(kategori);
    }

    public void insert(Barang barang){
        barangRepository.insert(barang);
    }

    public void update(Barang barang){
        barangRepository.update(barang);
    }

    public void delete(Barang barang){
        barangRepository.delete(barang);
    }

}
