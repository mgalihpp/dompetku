package com.neurallift.keuanganku.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.neurallift.keuanganku.data.AppDatabase;
import com.neurallift.keuanganku.data.dao.BarangDao;
import com.neurallift.keuanganku.data.model.Barang;

import java.util.List;

public class BarangRepository {
    BarangDao barangDao;

    public BarangRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        barangDao = db.barangDao();
    }

    public LiveData<List<Barang>> getAllBarang(){
        return barangDao.getAllBarang();
    }

    public LiveData<List<Barang>> getBarangByKategori(String kategori){
        return barangDao.getBarangByKategori(kategori);
    }

    public void insert(Barang barang){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            barangDao.insert(barang);
        });
    }

    public void update(Barang barang){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            barangDao.update(barang);
        });
    }

    public void delete(Barang barang){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            barangDao.delete(barang);
        });
    }

}
