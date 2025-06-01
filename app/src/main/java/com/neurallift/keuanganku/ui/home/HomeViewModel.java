package com.neurallift.keuanganku.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.neurallift.keuanganku.data.repository.TransaksiRepository;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.utils.DateTimeUtils;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private TransaksiRepository repository;

    private LiveData<List<Transaksi>> allTransaksi;
    private LiveData<Double> totalPemasukan;
    private LiveData<Double> totalPengeluaran;

    public HomeViewModel(@NonNull Application application) {
        super(application);

        repository = new TransaksiRepository(application);
        allTransaksi = repository.getAllTransaksi();
        totalPemasukan = repository.getTotalPemasukan();
        totalPengeluaran = repository.getTotalPengeluaran();
    }

    public LiveData<List<Transaksi>> getAllTransaksi() {
        return allTransaksi;
    }

    public LiveData<Double> getTotalPemasukan() {
        return totalPemasukan;
    }

    public LiveData<Double> getTotalPengeluaran() {
        return totalPengeluaran;
    }

    public LiveData<List<Transaksi>> getTransaksiHarian() {
        String today = DateTimeUtils.getTodayDate();
        return repository.getTransaksiByTanggal(today);
    }

    public LiveData<List<Transaksi>> getTransaksiMingguan() {
        String[] dateRange = DateTimeUtils.getWeekDateRange();
        return repository.getTransaksiByPeriode(dateRange[0], dateRange[1]);
    }

    public LiveData<List<Transaksi>> getTransaksiBulanan() {
        String[] dateRange = DateTimeUtils.getMonthDateRange();
        return repository.getTransaksiByPeriode(dateRange[0], dateRange[1]);
    }

    public LiveData<Double> getTotalPemasukanHarian() {
        String[] dateRange = new String[]{DateTimeUtils.getTodayDate(), DateTimeUtils.getTodayDate()};
        return repository.getTotalPemasukanByPeriode(dateRange[0], dateRange[1]);
    }

    public LiveData<Double> getTotalPengeluaranHarian() {
        String[] dateRange = new String[]{DateTimeUtils.getTodayDate(), DateTimeUtils.getTodayDate()};
        return repository.getTotalPengeluaranByPeriode(dateRange[0], dateRange[1]);
    }

    public LiveData<Double> getTotalPemasukanMingguan() {
        String[] dateRange = DateTimeUtils.getWeekDateRange();
        return repository.getTotalPemasukanByPeriode(dateRange[0], dateRange[1]);
    }

    public LiveData<Double> getTotalPengeluaranMingguan() {
        String[] dateRange = DateTimeUtils.getWeekDateRange();
        return repository.getTotalPengeluaranByPeriode(dateRange[0], dateRange[1]);
    }

    public LiveData<Double> getTotalPemasukanBulanan() {
        String[] dateRange = DateTimeUtils.getMonthDateRange();
        return repository.getTotalPemasukanByPeriode(dateRange[0], dateRange[1]);
    }

    public LiveData<Double> getTotalPengeluaranBulanan() {
        String[] dateRange = DateTimeUtils.getMonthDateRange();
        return repository.getTotalPengeluaranByPeriode(dateRange[0], dateRange[1]);
    }

    public void insert(Transaksi transaksi) {
        repository.insert(transaksi);
    }

    public void update(Transaksi transaksi) {
        repository.update(transaksi);
    }

    public void delete(Transaksi transaksi) {
        repository.delete(transaksi);
    }
}
