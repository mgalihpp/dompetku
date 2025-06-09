package com.neurallift.keuanganku.ui.kategori.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.neurallift.keuanganku.data.model.Kategori;
import com.neurallift.keuanganku.data.repository.KategoriRepository;

import java.util.List;

public class KategoriViewModel extends AndroidViewModel {

    private final KategoriRepository kategoriRepository;

    public KategoriViewModel(Application application) {
        super(application);
        kategoriRepository = new KategoriRepository(application);
    }

    public LiveData<List<Kategori>> getAllKategori() {
        return kategoriRepository.getAllKategori();
    }

    public void insert(Kategori kategori){
        kategoriRepository.insert(kategori);
    }

    public void update(Kategori kategori){
        kategoriRepository.update(kategori);
    }

    public void delete(Kategori kategori){
        kategoriRepository.delete(kategori);
    }

}
