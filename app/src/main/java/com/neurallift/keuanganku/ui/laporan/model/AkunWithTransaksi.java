package com.neurallift.keuanganku.ui.laporan.model;


import androidx.room.Embedded;
import androidx.room.Relation;

import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.data.model.Transaksi;

import java.util.List;

public class AkunWithTransaksi {

    @Embedded
    private Akun akun;

    @Relation(parentColumn = "nama", entityColumn = "akun")
    private List<Transaksi> transaksiList;

    // 🔧 Tambahkan SETTER supaya Room bisa isi
    public void setAkun(Akun akun) {
        this.akun = akun;
    }

    public void setTransaksiList(List<Transaksi> transaksiList) {
        this.transaksiList = transaksiList;
    }

    public Akun getAkun() {
        return akun;
    }

    public List<Transaksi> getTransaksiList() {
        return transaksiList;
    }
}
