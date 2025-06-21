package com.neurallift.keuanganku.ui.akun.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.data.repository.AkunRepository;
import com.neurallift.keuanganku.data.repository.TransaksiRepository;
import com.neurallift.keuanganku.ui.akun.model.AkunWithSaldo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AkunViewModel extends AndroidViewModel {

    private TransaksiRepository transaksiRepository;
    private AkunRepository akunRepository;
    private MediatorLiveData<List<AkunWithSaldo>> akunWithSaldoLiveData;
    private final Map<String, LiveData<Double>> saldoLiveDataMap = new HashMap<>();
    private final Map<String, Double> latestSaldoValues = new HashMap<>();
    private List<Akun> currentAkunList = new ArrayList<>();

    public AkunViewModel(@NonNull Application application) {
        super(application);
        transaksiRepository = new TransaksiRepository(application);
        akunRepository = new AkunRepository(application);
        akunWithSaldoLiveData = new MediatorLiveData<>();
        setupAkunWithSaldoLiveData();
    }

    private void setupAkunWithSaldoLiveData() {
        LiveData<List<Akun>> akunListLiveData = akunRepository.getAllAkun();

        // Observe the Akun list
        akunWithSaldoLiveData.addSource(akunListLiveData, akunList -> {
            if (akunList != null) {
                currentAkunList = akunList;
                updateAkunWithSaldoList();
            } else {
                currentAkunList = new ArrayList<>();
                akunWithSaldoLiveData.postValue(new ArrayList<>());
            }
        });
    }

    private void updateAkunWithSaldoList() {
        // Remove old saldo LiveData sources
        for (LiveData<Double> saldoLiveData : saldoLiveDataMap.values()) {
            akunWithSaldoLiveData.removeSource(saldoLiveData);
        }
        saldoLiveDataMap.clear();
        latestSaldoValues.clear();

        List<AkunWithSaldo> akunWithSaldoList = new ArrayList<>();

        // Add new AkunWithSaldo entries and observe their saldo LiveData
        for (Akun akun : currentAkunList) {
            String akunName = akun.getNama();
            LiveData<Double> saldoLiveData = akunRepository.getSaldoByAkun(akunName);
            saldoLiveDataMap.put(akunName, saldoLiveData);

            // Initialize saldo value
            Double saldoValue = saldoLiveData.getValue();
            latestSaldoValues.put(akunName, saldoValue != null ? saldoValue : 0.0);

            // Add observer for saldo LiveData
            akunWithSaldoLiveData.addSource(saldoLiveData, saldo -> {
                if (saldo != null) {
                    latestSaldoValues.put(akunName, saldo);
                    // Rebuild and post the updated list
                    List<AkunWithSaldo> updatedList = new ArrayList<>();
                    for (Akun a : currentAkunList) {
                        Double currentSaldo = latestSaldoValues.getOrDefault(a.getNama(), 0.0);
                        updatedList.add(new AkunWithSaldo(a, currentSaldo));
                    }
                    akunWithSaldoLiveData.postValue(updatedList);
                }
            });

            // Initial population of the list
            akunWithSaldoList.add(new AkunWithSaldo(akun, latestSaldoValues.get(akunName)));
        }

        // Post the initial list
        akunWithSaldoLiveData.postValue(akunWithSaldoList);
    }

    public LiveData<List<AkunWithSaldo>> getAllAkunWithSaldo() {
        return akunWithSaldoLiveData;
    }

    public LiveData<Akun> getAkunByNama(String akun) {
        return akunRepository.getAkunByNama(akun);
    }

    public LiveData<Double> getTotalSaldo() {
        MediatorLiveData<Double> totalSaldo = new MediatorLiveData<>();

        LiveData<Double> totalPemasukan = transaksiRepository.getTotalPemasukan();
        LiveData<Double> totalPengeluaran = transaksiRepository.getTotalPengeluaran();

        class SaldoHolder {
            Double pemasukan = 0.0;
            Double pengeluaran = 0.0;

            void updateSaldo() {
                totalSaldo.setValue(pemasukan - pengeluaran);
            }
        }

        SaldoHolder holder = new SaldoHolder();

        totalSaldo.addSource(totalPemasukan, pemasukan -> {
            holder.pemasukan = pemasukan != null ? pemasukan : 0.0;
            holder.updateSaldo();
        });

        totalSaldo.addSource(totalPengeluaran, pengeluaran -> {
            holder.pengeluaran = pengeluaran != null ? pengeluaran : 0.0;
            holder.updateSaldo();
        });

        return totalSaldo;
    }


    public void insert(Akun akun) {
        akunRepository.insert(akun);
    }

    public void update(Akun akun) {
        akunRepository.update(akun);
    }

    public void delete(Akun akun) {
        akunRepository.delete(akun);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}