package com.neurallift.keuanganku.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "kategori")
public class Kategori {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nama;

    public Kategori(String nama) {
        this.nama = nama;
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
}
