package com.neurallift.keuanganku.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.neurallift.keuanganku.data.model.Kategori;

import java.util.List;

@Dao
public interface KategoriDao {

    @Insert
    long insert(Kategori kategori);

    @Update
    void update(Kategori kategori);

    @Delete
    void delete(Kategori kategori);

    @Query("SELECT * FROM kategori ORDER BY nama ASC")
    LiveData<List<Kategori>> getAllKategori();

    @Query("SELECT * FROM kategori WHERE id = :id")
    LiveData<Kategori> getKategoriById(int id);

    @Query("SELECT * FROM kategori WHERE nama = :nama")
    LiveData<Kategori> getKategoriByNama(String nama);
}
