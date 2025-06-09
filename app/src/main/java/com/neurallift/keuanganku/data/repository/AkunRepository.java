package com.neurallift.keuanganku.data.repository;


import android.app.Application;

import androidx.lifecycle.LiveData;

import com.neurallift.keuanganku.data.AppDatabase;
import com.neurallift.keuanganku.data.dao.AkunDao;
import com.neurallift.keuanganku.data.dao.TransaksiDao;
import com.neurallift.keuanganku.data.model.Akun;

import java.util.List;

public class AkunRepository {

    private AkunDao akunDao;
    private TransaksiDao transaksiDao;
    public AkunRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        akunDao = db.akunDao();
        transaksiDao = db.transaksiDao();
    }

    public LiveData<List<Akun>> getAllAkun() {
        return akunDao.getAllAkun();
    }

    public LiveData<Akun> getAkunById(int id) {
        return akunDao.getAkunById(id);
    }

    public void insert(Akun akun) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
                akunDao.insert(akun);
                }
        );
    }

    public void update(Akun akun) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
                    transaksiDao.updateByAkun(akun.getNama(), akun.getId());
                    akunDao.update(akun);
                }
        );
    }

    public void delete(Akun akun) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
                    transaksiDao.deleteByAkun(akun.getNama());
                    akunDao.delete(akun);
                }
        );
    }

    public LiveData<Double> getSaldoByAkun(String akun) {
        return akunDao.getSaldoByAkun(akun);
    }

    public LiveData<Double> getSaldoAkunById(int akunId) {
        return akunDao.getSaldoAkunById(akunId);
    }

}