package com.neurallift.keuanganku.ui.transaksi.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.neurallift.keuanganku.data.repository.TransaksiRepository;
import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.data.model.Kategori;
import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.data.repository.AkunRepository;
import com.neurallift.keuanganku.data.repository.KategoriRepository;
import com.neurallift.keuanganku.ui.akun.model.TransaksiGroup;
import com.neurallift.keuanganku.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TransaksiViewModel extends AndroidViewModel {

    private TransaksiRepository transaksiRepository;
    private AkunRepository akunRepository;
    private KategoriRepository kategoriRepository;

    // Live data objects
    private LiveData<List<Kategori>> allKategori;
    private LiveData<List<Akun>> allAkun;

    // Filter criteria
    private MutableLiveData<String> filterAkun = new MutableLiveData<>();
    private MutableLiveData<String> filterKategori = new MutableLiveData<>();
    private MutableLiveData<String> filterJenis = new MutableLiveData<>();
    private MutableLiveData<String[]> filterPeriode = new MutableLiveData<>();

    // Filtered transactions
    private MediatorLiveData<List<TransaksiGroup>> filteredTransaksi = new MediatorLiveData<>();

    private MediatorLiveData<List<TransaksiGroup>> groupedTransaksiLiveData = new MediatorLiveData<>();

    public TransaksiViewModel(@NonNull Application application) {
        super(application);

        transaksiRepository = new TransaksiRepository(application);
        kategoriRepository = new KategoriRepository(application);
        akunRepository = new AkunRepository(application);
        allKategori = kategoriRepository.getAllKategori();
        allAkun = akunRepository.getAllAkun();


        // Initialize filtered transactions with all transactions
        initializeGroupedTransaksi();
    }

    // Getters for LiveData
    public LiveData<List<TransaksiGroup>> getAllTransaksi() {
        return groupedTransaksiLiveData;
    }

    public LiveData<Transaksi> getTransaksiById(int transaksiId) {
        return transaksiRepository.getTransaksiById(transaksiId);
    }

    public LiveData<List<Kategori>> getAllKategori() {
        return allKategori;
    }

    public LiveData<List<Akun>> getAllAkun() {
        return allAkun;
    }

    // Filter methods
    public void setFilterAkun(String akun) {
        filterAkun.setValue(akun);
    }
    public void setFilterKategori(String kategori) {
        filterKategori.setValue(kategori);
    }

    public void setFilterJenis(String jenis) {
        filterJenis.setValue(jenis);
    }

    public void setFilterPeriode(String startDate, String endDate) {
        filterPeriode.setValue(new String[]{startDate, endDate});
    }

    public void clearFilters() {
        filterAkun.setValue(null);
        filterKategori.setValue(null);
        filterJenis.setValue(null);
        filterPeriode.setValue(null);

        // Reset ke allTransaksi
        filteredTransaksi.removeSource(groupedTransaksiLiveData);
        filteredTransaksi.addSource(groupedTransaksiLiveData, filteredTransaksi::setValue);
    }

    public void applyFilters() {
        String kategori = filterKategori.getValue();
        String jenis = filterJenis.getValue();
        String[] periode = filterPeriode.getValue();
        String akun = filterAkun.getValue();

        String startDate = periode != null && periode.length > 0 ? periode[0] : "";
        String endDate = periode != null && periode.length > 1 ? periode[1] : "";

        LiveData<List<Transaksi>> filteredResult = transaksiRepository.getTransaksiByFilters(
                kategori != null && !kategori.isEmpty() ? kategori : null,
                jenis != null && !jenis.isEmpty() ? jenis : null,
                akun != null && !akun.isEmpty() ? akun : null,
                startDate != null && !startDate.isEmpty() ? startDate : null,
                endDate != null && !endDate.isEmpty() ? endDate : null
        );

        // Remove previous source to avoid multiple active sources
        filteredTransaksi.removeSource(groupedTransaksiLiveData);
        filteredTransaksi.addSource(filteredResult, transaksiList -> {
            if (transaksiList != null) {
                List<TransaksiGroup> groups = groupTransaksiByDate(transaksiList);
                filteredTransaksi.postValue(groups);
            } else {
                filteredTransaksi.postValue(new ArrayList<>());
            }
        });
    }

    public LiveData<List<TransaksiGroup>> initializeGroupedTransaksi(){
        LiveData<List<Transaksi>> transaksiListLiveData = transaksiRepository.getAllTransaksi();

        groupedTransaksiLiveData.addSource(transaksiListLiveData, transaksiList -> {
            if(transaksiList != null){
                List<TransaksiGroup> groups = groupTransaksiByDate(transaksiList);
                groupedTransaksiLiveData.postValue(groups);
            }
        });

        filteredTransaksi.addSource(groupedTransaksiLiveData, filteredTransaksi::setValue);

        return groupedTransaksiLiveData;
    }

    private List<TransaksiGroup> groupTransaksiByDate(List<Transaksi> transaksiList) {
        Map<String, List<Transaksi>> groupedMap = new LinkedHashMap<>();
        for (Transaksi transaksi : transaksiList) {
            if (transaksi.getTanggal() == null) continue;
            String dateKey;
            try {
                dateKey = DateTimeUtils.formatDateFull(transaksi.getTanggal());
            } catch (Exception e) {
                continue;
            }
            if (!groupedMap.containsKey(dateKey)) {
                groupedMap.put(dateKey, new ArrayList<>());
            }
            groupedMap.get(dateKey).add(transaksi);
        }
        List<TransaksiGroup> groups = new ArrayList<>();
        for (Map.Entry<String, List<Transaksi>> entry : groupedMap.entrySet()) {
            groups.add(new TransaksiGroup(entry.getKey(), entry.getValue()));
        }
        // Sort groups by date (assuming dateKey is in a parseable format)
        Collections.sort(groups, (g1, g2) -> {
            try {
                Date date1 = DateTimeUtils.parseDate(g1.getTanggal());
                Date date2 = DateTimeUtils.parseDate(g2.getTanggal());
                return date2.compareTo(date1); // Descending order (newest first)
            } catch (Exception e) {
                return 0;
            }
        });
        return groups;
    }

    public LiveData<List<TransaksiGroup>> getFilteredTransaksi() {
        return Transformations.distinctUntilChanged(filteredTransaksi);
    }



    public LiveData<String> getFilterKategori(){
        return filterKategori;
    };

    public LiveData<String> getFilterAkun(){
        return filterAkun;
    }

    public LiveData<String> getFilterJenis() {
        return filterJenis;
    }

    public LiveData<String> getFilterTanggalMulai() {
        return Transformations.map(filterPeriode, periode -> periode != null ? periode[0] : "");
    }

    public LiveData<String> getFilterTanggalSelesai() {
        return Transformations.map(filterPeriode, periode -> periode != null ? periode[1] : "");
    }

    // CRUD operations
    public void insert(Transaksi transaksi) {
        transaksiRepository.insert(transaksi);
    }
    public void insert(Kategori kategori) {
        kategoriRepository.insert(kategori);
    }
    public void update(Transaksi transaksi) {
        transaksiRepository.update(transaksi);
    }
    public void update(Kategori kategori) {
        kategoriRepository.update(kategori);
    }

    public void delete(Transaksi transaksi) {
        transaksiRepository.delete(transaksi);
    }
}
