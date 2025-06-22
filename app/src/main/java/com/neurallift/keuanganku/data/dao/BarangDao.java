package com.neurallift.keuanganku.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.neurallift.keuanganku.data.model.Barang;

import java.util.List;

@Dao
public interface BarangDao {

    @Insert
    void insert(Barang barang);

    @Update
    void update(Barang barang);

    @Delete
    void delete(Barang barang);

    @Query("SELECT * FROM barang")
    LiveData<List<Barang>> getAllBarang();

    @Query("SELECT * FROM barang WHERE kategori = :kategori")
    LiveData<List<Barang>> getBarangByKategori(String kategori);

}
