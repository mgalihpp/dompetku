package com.neurallift.keuanganku.ui.laporan;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.neurallift.keuanganku.data.TransaksiRepository;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.utils.DateTimeUtils;

import java.util.List;

public class LaporanViewModel extends AndroidViewModel {

    private TransaksiRepository repository;

    private MutableLiveData<String[]> selectedPeriod = new MutableLiveData<>();

    public LaporanViewModel(@NonNull Application application) {
        super(application);
        repository = new TransaksiRepository(application);

        // Set default period to current month
        setCurrentMonth();
    }

    // Period setters
    public void setDailyPeriod() {
        String today = DateTimeUtils.getTodayDate();
        selectedPeriod.setValue(new String[]{today, today});
    }

    public void setWeeklyPeriod() {
        selectedPeriod.setValue(DateTimeUtils.getWeekDateRange());
    }

    public void setMonthlyPeriod() {
        selectedPeriod.setValue(DateTimeUtils.getMonthDateRange());
    }

    public void setCurrentMonth() {
        setMonthlyPeriod();
    }

    public void setCustomPeriod(String startDate, String endDate) {
        selectedPeriod.setValue(new String[]{startDate, endDate});
    }

    // Get period range
    public LiveData<String[]> getSelectedPeriod() {
        return selectedPeriod;
    }

    // Transactions for period
    public LiveData<List<Transaksi>> getTransaksiByPeriod() {
        return Transformations.switchMap(selectedPeriod, period -> {
            if (period != null && period.length == 2) {
                return repository.getTransaksiByPeriode(period[0], period[1]);
            }
            return repository.getAllTransaksi();
        });
    }

    // Financial summaries
    public LiveData<Double> getTotalPemasukanByPeriod() {
        return Transformations.switchMap(selectedPeriod, period -> {
            if (period != null && period.length == 2) {
                return repository.getTotalPemasukanByPeriode(period[0], period[1]);
            }
            return repository.getTotalPemasukan();
        });
    }

    public LiveData<Double> getTotalPengeluaranByPeriod() {
        return Transformations.switchMap(selectedPeriod, period -> {
            if (period != null && period.length == 2) {
                return repository.getTotalPengeluaranByPeriode(period[0], period[1]);
            }
            return repository.getTotalPengeluaran();
        });
    }

    // Calculated values
    public LiveData<Double> getLaba() {
        return Transformations.switchMap(getTotalPemasukanByPeriod(), pemasukan -> {
            final Double pemasukanValue = pemasukan != null ? pemasukan : 0;

            return Transformations.map(getTotalPengeluaranByPeriod(), pengeluaran -> {
                Double pengeluaranValue = pengeluaran != null ? pengeluaran : 0;
                return pemasukanValue - pengeluaranValue;
            });
        });
    }
}
