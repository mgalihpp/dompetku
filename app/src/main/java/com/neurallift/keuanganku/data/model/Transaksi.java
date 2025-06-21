package com.neurallift.keuanganku.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transaksi")
public class Transaksi {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String tanggal;
    private String jam;
    private String kategori;
    private String akun;
    private String jenis; // pemasukan/pengeluaran
    private double nominal;
    private String catatan;

    public Transaksi(String tanggal, String jam, String kategori, String akun, String jenis, double nominal, String catatan) {
        this.tanggal = tanggal;
        this.jam = jam;
        this.kategori = kategori;
        this.akun = akun;
        this.jenis = jenis;
        this.nominal = nominal;
        this.catatan = catatan;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getAkun() {
        return akun;
    }

    public void setAkun(String akun) {
        this.akun = akun;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }
}
