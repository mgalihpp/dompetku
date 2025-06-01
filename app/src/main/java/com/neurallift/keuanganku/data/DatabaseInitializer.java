package com.neurallift.keuanganku.data;

import com.neurallift.keuanganku.data.dao.AkunDao;
import com.neurallift.keuanganku.data.dao.KategoriDao;
import com.neurallift.keuanganku.data.dao.TransaksiDao;
import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.data.model.Kategori;
import com.neurallift.keuanganku.data.model.Transaksi;

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
        // Add default categories
        kategoriDao.insert(new Kategori("Penjualan"));
        kategoriDao.insert(new Kategori("Pembelian"));
        kategoriDao.insert(new Kategori("Persediaan"));
        kategoriDao.insert(new Kategori("Gaji"));
        kategoriDao.insert(new Kategori("Sewa"));
        kategoriDao.insert(new Kategori("Utilitas"));
        kategoriDao.insert(new Kategori("Transportasi"));
        kategoriDao.insert(new Kategori("Makanan & Minuman"));
        kategoriDao.insert(new Kategori("Peralatan Kantor"));
        kategoriDao.insert(new Kategori("Lain-lain"));
    }

    private static void populateAkun(AkunDao akunDao) {
        // Add default accounts
        akunDao.insert(new Akun("Kas"));
        akunDao.insert(new Akun("Bank"));
        akunDao.insert(new Akun("Piutang"));
        akunDao.insert(new Akun("Hutang"));
    }

    private static void populateTransaksi(TransaksiDao transaksiDao) {
        // Add default transactions
        String[] kategoriList = {
                "Penjualan", "Pembelian", "Persediaan", "Gaji", "Sewa",
                "Utilitas", "Transportasi", "Makanan & Minuman", "Peralatan Kantor", "Lain-lain"
        };
        String[] akunList = { "Kas", "Bank", "Piutang", "Hutang" };
        String[] jenisList = { "pemasukan", "pengeluaran" };

        for (int i = 1; i <= 100; i++) {
            String tanggal = String.format("2025-05-%02d", (i % 28) + 1);  // 01 s/d 28
            String jam = String.format("%02d:%02d", 8 + (i % 10), i % 60); // 08:00 s/d 17:59
            String kategori = kategoriList[i % kategoriList.length];
            String akun = akunList[i % akunList.length];
            String jenis = jenisList[i % 2];
            double nominal = 10000 + (i * 500); // Naik tiap transaksi
            String catatan = "Transaksi ke-" + i;

            transaksiDao.insert(new Transaksi(tanggal, jam, kategori, akun, jenis, nominal, catatan));
        }
    }
}
