package com.neurallift.keuanganku.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.neurallift.keuanganku.data.model.Transaksi;

import java.util.List;

@Dao
public interface TransaksiDao {

    @Insert
    long insert(Transaksi transaksi);

    @Update
    void update(Transaksi transaksi);

    @Delete
    void delete(Transaksi transaksi);

    @Query("SELECT * FROM transaksi ORDER BY tanggal DESC, jam DESC")
    LiveData<List<Transaksi>> getAllTransaksi();

    @Query("SELECT * FROM transaksi WHERE tanggal = :tanggal ORDER BY jam DESC")
    LiveData<List<Transaksi>> getTransaksiByTanggal(String tanggal);

    @Query("SELECT * FROM transaksi WHERE kategori = :kategori ORDER BY tanggal DESC, jam DESC")
    LiveData<List<Transaksi>> getTransaksiByKategori(String kategori);

    @Query("SELECT * FROM transaksi WHERE jenis = :jenis ORDER BY tanggal DESC, jam DESC")
    LiveData<List<Transaksi>> getTransaksiByJenis(String jenis);

    @Query("SELECT SUM(nominal) FROM transaksi WHERE jenis = 'pemasukan'")
    LiveData<Double> getTotalPemasukan();

    @Query("SELECT SUM(nominal) FROM transaksi WHERE jenis = 'pengeluaran'")
    LiveData<Double> getTotalPengeluaran();

    @Query("SELECT SUM(nominal) FROM transaksi WHERE jenis = 'pemasukan' AND tanggal BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalPemasukanByPeriode(String startDate, String endDate);

    @Query("SELECT SUM(nominal) FROM transaksi WHERE jenis = 'pengeluaran' AND tanggal BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalPengeluaranByPeriode(String startDate, String endDate);

    @Query("SELECT * FROM transaksi WHERE tanggal BETWEEN :startDate AND :endDate ORDER BY tanggal DESC, jam DESC")
    LiveData<List<Transaksi>> getTransaksiByPeriode(String startDate, String endDate);
}
