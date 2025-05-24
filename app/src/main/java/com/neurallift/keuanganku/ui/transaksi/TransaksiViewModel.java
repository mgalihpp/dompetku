package com.neurallift.keuanganku.ui.transaksi;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.neurallift.keuanganku.data.TransaksiRepository;
import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.data.model.Kategori;
import com.neurallift.keuanganku.data.model.Transaksi;

import java.util.List;

public class TransaksiViewModel extends AndroidViewModel {

    private TransaksiRepository repository;

    // Live data objects
    private LiveData<List<Transaksi>> allTransaksi;
    private LiveData<List<Kategori>> allKategori;
    private LiveData<List<Akun>> allAkun;

    // Filter criteria
    private MutableLiveData<String> filterKategori = new MutableLiveData<>();
    private MutableLiveData<String> filterJenis = new MutableLiveData<>();
    private MutableLiveData<String[]> filterPeriode = new MutableLiveData<>();

    // Filtered transactions
    private MediatorLiveData<List<Transaksi>> filteredTransaksi = new MediatorLiveData<>();

    public TransaksiViewModel(@NonNull Application application) {
        super(application);

        repository = new TransaksiRepository(application);
        allTransaksi = repository.getAllTransaksi();
        allKategori = repository.getAllKategori();
        allAkun = repository.getAllAkun();

        // Initialize filtered transactions with all transactions
        filteredTransaksi.addSource(allTransaksi, filteredTransaksi::setValue);
    }

    // Getters for LiveData
    public LiveData<List<Transaksi>> getAllTransaksi() {
        return allTransaksi;
    }

    public LiveData<List<Kategori>> getAllKategori() {
        return allKategori;
    }

    public LiveData<List<Akun>> getAllAkun() {
        return allAkun;
    }

    // Filter methods
    public void setFilterKategori(String kategori) {
        filterKategori.setValue(kategori);
        applyFilters();
    }

    public void setFilterJenis(String jenis) {
        filterJenis.setValue(jenis);
        applyFilters();
    }

    public void setFilterPeriode(String startDate, String endDate) {
        filterPeriode.setValue(new String[]{startDate, endDate});
        applyFilters();
    }

    public void clearFilters() {
        filterKategori.setValue(null);
        filterJenis.setValue(null);
        filterPeriode.setValue(null);

        // Reset ke allTransaksi
        filteredTransaksi.removeSource(allTransaksi);
        filteredTransaksi.addSource(allTransaksi, filteredTransaksi::setValue);
    }

    private void applyFilters() {
        //LiveData<List<Transaksi>> result = allTransaksi;
        //
        //// Apply kategori filter
        //if (filterKategori.getValue() != null && !filterKategori.getValue().isEmpty()) {
        //    result = repository.getTransaksiByKategori(filterKategori.getValue());
        //}
        //
        //// Apply jenis filter
        //if (filterJenis.getValue() != null && !filterJenis.getValue().isEmpty()) {
        //    final LiveData<List<Transaksi>> previousResult = result;
        //    result = Transformations.switchMap(previousResult, transaksiList -> {
        //        MutableLiveData<List<Transaksi>> filtered = new MutableLiveData<>();
        //        filtered.setValue(filterByJenis(transaksiList, filterJenis.getValue()));
        //        return filtered;
        //    });
        //}
        //
        //// Apply periode filter
        //if (filterPeriode.getValue() != null) {
        //    String startDate = filterPeriode.getValue()[0];
        //    String endDate = filterPeriode.getValue()[1];
        //    if (startDate != null && endDate != null) {
        //        final LiveData<List<Transaksi>> previousResult = result;
        //        result = Transformations.switchMap(previousResult, transaksiList -> {
        //            LiveData<List<Transaksi>> byPeriode = repository.getTransaksiByPeriode(startDate, endDate);
        //            return byPeriode;
        //        });
        //    }
        //}
        //
        //filteredTransaksi = result;
        LiveData<List<Transaksi>> result = allTransaksi;

        // Apply kategori filter
        if (filterKategori.getValue() != null && !filterKategori.getValue().isEmpty()) {
            result = repository.getTransaksiByKategori(filterKategori.getValue());
        }

        // Apply jenis filter
        if (filterJenis.getValue() != null && !filterJenis.getValue().isEmpty()) {
            final LiveData<List<Transaksi>> previousResult = result;
            result = Transformations.switchMap(previousResult, transaksiList -> {
                MutableLiveData<List<Transaksi>> filtered = new MutableLiveData<>();
                filtered.setValue(filterByJenis(transaksiList, filterJenis.getValue()));
                return filtered;
            });
        }

        // Apply periode filter
        if (filterPeriode.getValue() != null) {
            String startDate = filterPeriode.getValue()[0];
            String endDate = filterPeriode.getValue()[1];
            if (startDate != null && endDate != null) {
                final LiveData<List<Transaksi>> previousResult = result;
                result = Transformations.switchMap(previousResult, transaksiList -> {
                    return repository.getTransaksiByPeriode(startDate, endDate);
                });
            }
        }

        // Gunakan MediatorLiveData agar bisa update observer
        filteredTransaksi.addSource(result, data -> {
            filteredTransaksi.setValue(data);
        });
    }

    private List<Transaksi> filterByJenis(List<Transaksi> transaksi, String jenis) {
        List<Transaksi> result = new java.util.ArrayList<>();
        for (Transaksi t : transaksi) {
            if (t.getJenis().equals(jenis)) {
                result.add(t);
            }
        }
        return result;
    }

    public LiveData<List<Transaksi>> getFilteredTransaksi() {
        return filteredTransaksi;
    }

    // CRUD operations
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
