package com.neurallift.keuanganku.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.neurallift.keuanganku.data.AppDatabase;
import com.neurallift.keuanganku.data.dao.AkunDao;
import com.neurallift.keuanganku.data.dao.KategoriDao;
import com.neurallift.keuanganku.data.dao.TransaksiDao;
import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.data.model.Kategori;
import com.neurallift.keuanganku.data.model.Transaksi;

import java.util.List;

/**
 * Repository class that abstracts access to multiple data sources. This is a
 * recommended pattern for data operations.
 */
public class TransaksiRepository {

    private TransaksiDao transaksiDao;

    private LiveData<List<Transaksi>> allTransaksi;

    private LiveData<Double> totalPemasukan;
    private LiveData<Double> totalPengeluaran;

    public TransaksiRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        transaksiDao = db.transaksiDao();

        allTransaksi = transaksiDao.getAllTransaksi();

        totalPemasukan = transaksiDao.getTotalPemasukan();
        totalPengeluaran = transaksiDao.getTotalPengeluaran();
    }

    // Transaksi methods
    public LiveData<List<Transaksi>> getAllTransaksi() {
        return allTransaksi;
    }

    public LiveData<List<Transaksi>> getTransaksiByFilters(String kategori, String jenis, String akun, String startDate, String endDate) {
        return transaksiDao.getTransaksiByFilters(kategori, jenis, akun, startDate, endDate);
    };

    public LiveData<Transaksi> getTransaksiById(int transaksiId) {
        return transaksiDao.getTransaksiById(transaksiId);
    }

    public LiveData<List<Transaksi>> getTransaksiByTanggal(String tanggal) {
        return transaksiDao.getTransaksiByTanggal(tanggal);
    }

    public LiveData<List<Transaksi>> getTransaksiByKategori(String kategori) {
        return transaksiDao.getTransaksiByKategori(kategori);
    }

    public LiveData<List<Transaksi>> getTransaksiByJenis(String jenis) {
        return transaksiDao.getTransaksiByJenis(jenis);
    }

    public LiveData<List<Transaksi>> getTransaksiByPeriode(String startDate, String endDate) {
        return transaksiDao.getTransaksiByPeriode(startDate, endDate);
    }

    public void insert(Transaksi transaksi) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transaksiDao.insert(transaksi);
        });
    }

    public void update(Transaksi transaksi) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transaksiDao.update(transaksi);
        });
    }

    public void delete(Transaksi transaksi) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transaksiDao.delete(transaksi);
        });
    }

    // Financial summary methods
    public LiveData<Double> getTotalPemasukan() {
        return totalPemasukan;
    }

    public LiveData<Double> getTotalPengeluaran() {
        return totalPengeluaran;
    }

    public LiveData<Double> getTotalPemasukanByPeriode(String startDate, String endDate) {
        return transaksiDao.getTotalPemasukanByPeriode(startDate, endDate);
    }

    public LiveData<Double> getTotalPengeluaranByPeriode(String startDate, String endDate) {
        return transaksiDao.getTotalPengeluaranByPeriode(startDate, endDate);
    }

    public LiveData<List<Transaksi>> getTransaksiByAkunId(int akunId) {
        return transaksiDao.getTransaksiByAkunId(akunId);
    }
}
