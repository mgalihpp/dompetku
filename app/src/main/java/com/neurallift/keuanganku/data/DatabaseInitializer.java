package com.neurallift.keuanganku.data;

import com.neurallift.keuanganku.data.dao.AkunDao;
import com.neurallift.keuanganku.data.dao.KategoriDao;
import com.neurallift.keuanganku.data.dao.TransaksiDao;
import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.data.model.Kategori;
import com.neurallift.keuanganku.data.model.Transaksi;

import java.util.Calendar;

/**
 * Utility class to populate the database with initial data
 */
public class DatabaseInitializer {

    public static void populateDatabase(AppDatabase db) {
        populateKategori(db.kategoriDao());
        populateAkun(db.akunDao());
        populateTransaksi(db.transaksiDao());
    }

    private static void populateKategori(KategoriDao kategoriDao) {
        // Kategori relevan untuk mahasiswa
        kategoriDao.insert(new Kategori("Uang Jajan"));
        kategoriDao.insert(new Kategori("Makan"));
        kategoriDao.insert(new Kategori("Transportasi"));
        kategoriDao.insert(new Kategori("Hiburan"));
        kategoriDao.insert(new Kategori("Belanja"));
        kategoriDao.insert(new Kategori("Kuota Internet"));
        kategoriDao.insert(new Kategori("Kos"));
        kategoriDao.insert(new Kategori("Lain-lain"));
    }

    private static void populateAkun(AkunDao akunDao) {
        // Akun relevan untuk mahasiswa
        akunDao.insert(new Akun("Dompet"));
        akunDao.insert(new Akun("E-Wallet"));
        akunDao.insert(new Akun("Bank"));
    }

    private static void populateTransaksi(TransaksiDao transaksiDao) {
        // Periode dari Januari 2024 hingga Juni 2025
        Calendar startDate = Calendar.getInstance();
        startDate.set(2024, Calendar.JANUARY, 1); // 1 Januari 2024
        Calendar endDate = Calendar.getInstance();
        endDate.set(2025, Calendar.JUNE, 30); // 30 Juni 2025

        String[] kategoriList = {
                "Uang Jajan", "Makan", "Transportasi", "Hiburan",
                "Belanja", "Kuota Internet", "Kos", "Lain-lain"
        };
        String[] akunList = {"Dompet", "E-Wallet", "Bank"};
        int transactionId = 1;

        Calendar currentDate = (Calendar) startDate.clone();
        while (!currentDate.after(endDate)) {
            // Format tanggal ke yyyy-MM-dd
            String tanggal = String.format("%04d-%02d-%02d",
                    currentDate.get(Calendar.YEAR),
                    currentDate.get(Calendar.MONTH) + 1, // Bulan dimulai dari 0
                    currentDate.get(Calendar.DAY_OF_MONTH));

            // Uang jajan Rp20.000 setiap Senin-Jumat
            int dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
                transaksiDao.insert(new Transaksi(
                        tanggal,
                        "08:00",
                        "Uang Jajan",
                        "Dompet",
                        "pemasukan",
                        20000.0,
                        "Uang jajan harian dari orang tua"
                ));

                // Pengeluaran makan Rp10.000 saat ngampus (Senin-Jumat)
                transaksiDao.insert(new Transaksi(
                        tanggal,
                        "12:00",
                        "Makan",
                        "Dompet",
                        "pengeluaran",
                        10000.0,
                        "Makan siang di kantin kampus"
                ));
                transactionId += 2;
            }

            // Tambahan pengeluaran realistis (acak, misalnya 1-2 transaksi per minggu)
            if (dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.SATURDAY) {
                // Pengeluaran transportasi (misalnya ojek online)
                if (Math.random() < 0.5) {
                    transaksiDao.insert(new Transaksi(
                            tanggal,
                            String.format("%02d:%02d", 7 + (transactionId % 10), transactionId % 60),
                            "Transportasi",
                            "E-Wallet",
                            "pengeluaran",
                            15000.0 + (Math.random() * 5000), // Rp15.000 - Rp20.000
                            "Ojek online ke kampus"
                    ));
                    transactionId++;
                }

                // Pengeluaran hiburan (misalnya nonton atau jajan)
                if (Math.random() < 0.3) {
                    transaksiDao.insert(new Transaksi(
                            tanggal,
                            String.format("%02d:%02d", 15 + (transactionId % 5), transactionId % 60),
                            "Hiburan",
                            "E-Wallet",
                            "pengeluaran",
                            25000.0 + (Math.random() * 25000), // Rp25.000 - Rp50.000
                            "Nonton film atau jajan"
                    ));
                    transactionId++;
                }
            }

            // Pengeluaran bulanan: Kuota internet (tanggal 1 setiap bulan)
            if (currentDate.get(Calendar.DAY_OF_MONTH) == 1) {
                transaksiDao.insert(new Transaksi(
                        tanggal,
                        "09:00",
                        "Kuota Internet",
                        "E-Wallet",
                        "pengeluaran",
                        50000.0,
                        "Isi ulang kuota internet bulanan"
                ));
                transactionId++;
            }

            // Pengeluaran bulanan: Kos (tanggal 1 setiap bulan)
            if (currentDate.get(Calendar.DAY_OF_MONTH) == 1) {
                transaksiDao.insert(new Transaksi(
                        tanggal,
                        "10:00",
                        "Kos",
                        "Bank",
                        "pengeluaran",
                        500000.0,
                        "Bayar kos bulanan"
                ));
                transactionId++;
            }

            // Lanjut ke hari berikutnya
            currentDate.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}