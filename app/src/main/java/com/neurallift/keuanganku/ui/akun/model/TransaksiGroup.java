package com.neurallift.keuanganku.ui.akun.model;

import com.neurallift.keuanganku.data.model.Transaksi;

import java.util.List;

public class TransaksiGroup {

    private String tanggal;
    private List<Transaksi> transaksiList;

    public TransaksiGroup(String tanggal, List<Transaksi> transaksiList){
        this.tanggal = tanggal;
        this.transaksiList = transaksiList;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public List<Transaksi> getTransaksiList() {
        return transaksiList;
    }

    public void setTransaksiList(List<Transaksi> transaksiList) {
        this.transaksiList = transaksiList;
    }

    public int getJumlahTransaksi() {
        return transaksiList != null ? transaksiList.size() : 0;
    }
}
