package com.neurallift.keuanganku.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.neurallift.keuanganku.data.AppDatabase;
import com.neurallift.keuanganku.data.dao.KategoriDao;
import com.neurallift.keuanganku.data.model.Kategori;

import java.util.List;

public class KategoriRepository {

    private KategoriDao kategoriDao;

    private LiveData<List<Kategori>> allKategori;

    public KategoriRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        kategoriDao = db.kategoriDao();

        allKategori = kategoriDao.getAllKategori();

    }

    public LiveData<List<Kategori>> getAllKategori() {
        return allKategori;
    }

    public LiveData<Kategori> getKategoriByNama(String nama) {
        return kategoriDao.getKategoriByNama(nama);
    }

    public void insert(Kategori kategori) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            kategoriDao.insert(kategori);
        });
    }

    public void update(Kategori kategori) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            kategoriDao.update(kategori);
        });
    }

    public void delete(Kategori kategori) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            kategoriDao.delete(kategori);
        });
    }

}
