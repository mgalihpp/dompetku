package com.neurallift.keuanganku.ui.akun.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.neurallift.keuanganku.data.model.Transaksi;
import com.neurallift.keuanganku.data.repository.AkunRepository;
import com.neurallift.keuanganku.data.repository.TransaksiRepository;
import com.neurallift.keuanganku.ui.akun.model.TransaksiGroup;
import com.neurallift.keuanganku.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DetailAkunViewModel extends AndroidViewModel {
    private AkunRepository akunRepository;
    private TransaksiRepository transaksiRepository;
    private MediatorLiveData<List<TransaksiGroup>> groupedTransaksiLiveData;
    private final Map<Integer, List<TransaksiGroup>> transaksiCache = new HashMap<>();

    public DetailAkunViewModel(@NonNull Application application){
        super(application);

        akunRepository = new AkunRepository(application);
        transaksiRepository = new TransaksiRepository(application);

        groupedTransaksiLiveData = new MediatorLiveData<>();
    }

    public LiveData<Double> getSaldoAkunById(int akunId){
        return akunRepository.getSaldoAkunById(akunId);
    }

    public LiveData<List<TransaksiGroup>> getGroupedTransaksiByAkunId(int akunId) {

        if(transaksiCache.containsKey(akunId)){
            groupedTransaksiLiveData.setValue(transaksiCache.getOrDefault(akunId, new ArrayList<>()));
            return groupedTransaksiLiveData;
        }

        LiveData<List<Transaksi>> transaksiListLiveData = transaksiRepository.getTransaksiByAkunId(akunId);

        // Use a background executor for grouping
        Executor executor = Executors.newSingleThreadExecutor();

        groupedTransaksiLiveData.addSource(transaksiListLiveData, transaksiList -> {
            if (transaksiList != null) {
                executor.execute(() -> {
                    List<TransaksiGroup> groups = groupTransaksiByDate(transaksiList);
                    transaksiCache.put(akunId, groups);
                    groupedTransaksiLiveData.postValue(groups);
                });
            }
        });

        return groupedTransaksiLiveData;
    }

    private List<TransaksiGroup> groupTransaksiByDate(List<Transaksi> transaksiList){
        Map<String, List<Transaksi>> groupedMap = new LinkedHashMap<>();

        for(Transaksi transaksi : transaksiList){
            String dateKey = DateTimeUtils.formatDateFull(transaksi.getTanggal());

            if(!groupedMap.containsKey(dateKey)){
                groupedMap.put(dateKey, new ArrayList<>());
            }
            groupedMap.get(dateKey).add(transaksi);
        }

        List<TransaksiGroup> groups = new ArrayList<>();
        for (Map.Entry<String, List<Transaksi>> entry : groupedMap.entrySet()){
         groups.add(new TransaksiGroup(entry.getKey(), entry.getValue()));
        }

        return groups;
    }

}
