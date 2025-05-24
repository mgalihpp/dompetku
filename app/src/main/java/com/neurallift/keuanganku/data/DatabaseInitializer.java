package com.neurallift.keuanganku.data;

import com.neurallift.keuanganku.data.dao.AkunDao;
import com.neurallift.keuanganku.data.dao.KategoriDao;
import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.data.model.Kategori;

/**
 * Utility class to populate the database with initial data
 */
public class DatabaseInitializer {

    public static void populateDatabase(AppDatabase db) {
        populateKategori(db.kategoriDao());
        populateAkun(db.akunDao());
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
        akunDao.insert(new Akun("Modal"));
        akunDao.insert(new Akun("Kartu Kredit"));
    }
}
