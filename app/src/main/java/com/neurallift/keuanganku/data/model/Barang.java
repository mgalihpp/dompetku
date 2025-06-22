package com.neurallift.keuanganku.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "barang")
public class Barang implements Parcelable {

    @PrimaryKey (autoGenerate = true)
    private int id;

    private String nama;
    private double harga;
    private int stok;
    private String satuan;
    private String kategori;

    public Barang(String nama, double harga, int stok, String satuan, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
        this.satuan = satuan;
        this.kategori = kategori;
    }

    protected Barang(Parcel in) {
        id = in.readInt();
        nama = in.readString();
        harga = in.readDouble();
        satuan = in.readString();
        kategori = in.readString();
    }

    public static final Creator<Barang> CREATOR = new Creator<Barang>() {
        @Override
        public Barang createFromParcel(Parcel in) {
            return new Barang(in);
        }

        @Override
        public Barang[] newArray(int size) {
            return new Barang[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nama);
        dest.writeDouble(harga);
        dest.writeString(satuan);
        dest.writeString(kategori);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
}
