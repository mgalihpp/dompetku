package com.neurallift.keuanganku.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

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
    private KategoriDao kategoriDao;
    private AkunDao akunDao;

    private LiveData<List<Transaksi>> allTransaksi;
    private LiveData<List<Kategori>> allKategori;
    private LiveData<List<Akun>> allAkun;

    private LiveData<Double> totalPemasukan;
    private LiveData<Double> totalPengeluaran;

    public TransaksiRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        transaksiDao = db.transaksiDao();
        kategoriDao = db.kategoriDao();
        akunDao = db.akunDao();

        allTransaksi = transaksiDao.getAllTransaksi();
        allKategori = kategoriDao.getAllKategori();
        allAkun = akunDao.getAllAkun();

        totalPemasukan = transaksiDao.getTotalPemasukan();
        totalPengeluaran = transaksiDao.getTotalPengeluaran();
    }

    // Transaksi methods
    public LiveData<List<Transaksi>> getAllTransaksi() {
        return allTransaksi;
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

    // Kategori methods
    public LiveData<List<Kategori>> getAllKategori() {
        return allKategori;
    }

    public void insert(Kategori kategori) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            kategoriDao.insert(kategori);
        });
    }

    // Akun methods
    public LiveData<List<Akun>> getAllAkun() {
        return allAkun;
    }

    public void insert(Akun akun) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            akunDao.insert(akun);
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
}
