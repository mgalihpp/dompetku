package com.neurallift.keuanganku.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.neurallift.keuanganku.data.model.Akun;

import java.util.List;

@Dao
public interface AkunDao {

    @Insert
    long insert(Akun akun);

    @Update
    void update(Akun akun);

    @Delete
    void delete(Akun akun);

    @Query("SELECT * FROM akun ORDER BY nama ASC")
    LiveData<List<Akun>> getAllAkun();

    @Query("SELECT * FROM akun WHERE id = :id")
    LiveData<Akun> getAkunById(int id);

    @Query("SELECT * FROM akun WHERE nama = :nama")
    LiveData<Akun> getAkunByNama(String nama);

    @Query("SELECT COALESCE(SUM(CASE WHEN jenis = 'pemasukan' THEN nominal ELSE -nominal END), 0) FROM transaksi WHERE akun = :akun")
    LiveData<Double> getSaldoByAkun(String akun);

    @Query("SELECT COALESCE(SUM(CASE WHEN jenis = 'pemasukan' THEN nominal ELSE -nominal END), 0) FROM transaksi WHERE akun = (SELECT nama FROM akun WHERE id = :akunId)")
    LiveData<Double> getSaldoAkunById(int akunId);
}
