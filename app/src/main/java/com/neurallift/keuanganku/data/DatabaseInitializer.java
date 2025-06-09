package com.neurallift.keuanganku.data;

import com.neurallift.keuanganku.data.dao.AkunDao;
import com.neurallift.keuanganku.data.dao.KategoriDao;
import com.neurallift.keuanganku.data.dao.TransaksiDao;
import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.data.model.Kategori;
import com.neurallift.keuanganku.data.model.Transaksi;

import java.util.Calendar;

/**
 * Utility class to populate the database with 200 realistic transactions for an UMKM
 */
public class DatabaseInitializer {

    public static void populateDatabase(AppDatabase db) {
        populateKategori(db.kategoriDao());
        populateAkun(db.akunDao());
        populateTransaksi(db.transaksiDao());
    }

    private static void populateKategori(KategoriDao kategoriDao) {
        // Kategori relevan untuk UMKM
        kategoriDao.insert(new Kategori("Penjualan")); // Income from sales
        kategoriDao.insert(new Kategori("Pembelian Bahan Baku")); // Supplier payments
        kategoriDao.insert(new Kategori("Listrik")); // Electricity bills
        kategoriDao.insert(new Kategori("Internet")); // Internet bills
        kategoriDao.insert(new Kategori("Sewa Tempat")); // Rent
        kategoriDao.insert(new Kategori("Gaji Karyawan")); // Employee wages
        kategoriDao.insert(new Kategori("Pemasaran")); // Marketing/advertising
        kategoriDao.insert(new Kategori("Peralatan")); // Equipment/tools
        kategoriDao.insert(new Kategori("Lain-lain")); // Miscellaneous
    }

    private static void populateAkun(AkunDao akunDao) {
        // Akun relevan untuk UMKM
        akunDao.insert(new Akun("Kas")); // Cash
        akunDao.insert(new Akun("Bank")); // Bank account
        akunDao.insert(new Akun("Dompet Digital")); // E.g., OVO, GoPay
    }

    private static void populateTransaksi(TransaksiDao transaksiDao) {
        // Periode dari Januari 2024 hingga Juni 2025 (18 bulan)
        Calendar startDate = Calendar.getInstance();
        startDate.set(2024, Calendar.JANUARY, 1); // 1 Januari 2024
        Calendar endDate = Calendar.getInstance();
        endDate.set(2025, Calendar.JUNE, 30); // 30 Juni 2025

        int targetTransactions = 200;
        int transactionCount = 0;

        // Transaction distribution plan:
        // - Daily sales (3 days/week, ~12/month) = 12 * 18 months = 216, take 120
        // - Supplier payments (weekly, 4/month) = 4 * 18 = 72, take 36
        // - Monthly rent = 1 * 18 = 18
        // - Monthly utilities (electricity, internet) = 2 * 18 = 36
        // - Monthly wages (1/month) = 1 * 18 = 18
        // - Marketing (2/month) = 2 * 18 = 36, take 5
        // - Equipment (occasional) = 3
        // Total: 120 + 36 + 18 + 36 + 18 + 5 + 3 = 236, adjust to 200

        Calendar currentDate = (Calendar) startDate.clone();
        int transactionId = 1;

        while (!currentDate.after(endDate) && transactionCount < targetTransactions) {
            String tanggal = String.format("%04d-%02d-%02d",
                    currentDate.get(Calendar.YEAR),
                    currentDate.get(Calendar.MONTH) + 1,
                    currentDate.get(Calendar.DAY_OF_MONTH));

            int dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);

            // Daily sales (Monday, Wednesday, Friday for variety, ~120 transactions)
            if ((dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY)
                    && transactionCount < 120) {
                double salesAmount = 500000.0 + (Math.random() * 500000); // Rp500,000 - Rp1,000,000
                transaksiDao.insert(new Transaksi(
                        tanggal,
                        "17:00",
                        "Penjualan",
                        Math.random() < 0.7 ? "Kas" : "Dompet Digital",
                        "pemasukan",
                        salesAmount,
                        "Penjualan harian produk/jasa"
                ));
                transactionCount++;
                transactionId++;
            }

            // Weekly supplier payments (every Tuesday, ~36 transactions)
            if (dayOfWeek == Calendar.TUESDAY && transactionCount < 156) { // 120 + 36
                transaksiDao.insert(new Transaksi(
                        tanggal,
                        "10:00",
                        "Pembelian Bahan Baku",
                        "Bank",
                        "pengeluaran",
                        2000000.0 + (Math.random() * 1000000), // Rp2,000,000 - Rp3,000,000
                        "Pembayaran ke supplier bahan baku"
                ));
                transactionCount++;
                transactionId++;
            }

            // Monthly fixed costs (day 1 of each month)
            if (currentDate.get(Calendar.DAY_OF_MONTH) == 1) {
                // Rent (~18 transactions)
                if (transactionCount < 174) { // 120 + 36 + 18
                    transaksiDao.insert(new Transaksi(
                            tanggal,
                            "09:00",
                            "Sewa Tempat",
                            "Bank",
                            "pengeluaran",
                            3000000.0, // Rp3,000,000
                            "Pembayaran sewa tempat usaha"
                    ));
                    transactionCount++;
                    transactionId++;
                }

                // Electricity (~18 transactions)
                if (transactionCount < 192) { // 120 + 36 + 18 + 18
                    transaksiDao.insert(new Transaksi(
                            tanggal,
                            "09:30",
                            "Listrik",
                            "Bank",
                            "pengeluaran",
                            500000.0 + (Math.random() * 200000), // Rp500,000 - Rp700,000
                            "Tagihan listrik bulanan"
                    ));
                    transactionCount++;
                    transactionId++;
                }

                // Internet (~18 transactions, take 8 to fit 200)
                if (transactionCount < 200 && currentDate.get(Calendar.MONTH) % 2 == 0) { // Every 2 months
                    transaksiDao.insert(new Transaksi(
                            tanggal,
                            "09:45",
                            "Internet",
                            "Dompet Digital",
                            "pengeluaran",
                            300000.0,
                            "Tagihan internet bulanan"
                    ));
                    transactionCount++;
                    transactionId++;
                }

                // Employee wages (~18 transactions, take 6)
                if (transactionCount < 200 && currentDate.get(Calendar.MONTH) % 3 == 0) { // Every 3 months
                    transaksiDao.insert(new Transaksi(
                            tanggal,
                            "10:00",
                            "Gaji Karyawan",
                            "Bank",
                            "pengeluaran",
                            4000000.0, // Rp4,000,000 for 2-3 employees
                            "Pembayaran gaji karyawan"
                    ));
                    transactionCount++;
                    transactionId++;
                }
            }

            // Marketing (occasional, ~5 transactions)
            if (dayOfWeek == Calendar.THURSDAY && Math.random() < 0.1 && transactionCount < 195) {
                transaksiDao.insert(new Transaksi(
                        tanggal,
                        "14:00",
                        "Pemasaran",
                        "Dompet Digital",
                        "pengeluaran",
                        200000.0 + (Math.random() * 300000), // Rp200,000 - Rp500,000
                        "Biaya iklan online atau promosi"
                ));
                transactionCount++;
                transactionId++;
            }

            // Equipment (rare, ~3 transactions)
            if (currentDate.get(Calendar.MONTH) == Calendar.MARCH ||
                    currentDate.get(Calendar.MONTH) == Calendar.SEPTEMBER ||
                    currentDate.get(Calendar.MONTH) == Calendar.DECEMBER) {
                if (currentDate.get(Calendar.DAY_OF_MONTH) == 15 && transactionCount < 198) {
                    transaksiDao.insert(new Transaksi(
                            tanggal,
                            "11:00",
                            "Peralatan",
                            "Bank",
                            "pengeluaran",
                            1000000.0 + (Math.random() * 2000000), // Rp1,000,000 - Rp3,000,000
                            "Pembelian peralatan usaha"
                    ));
                    transactionCount++;
                    transactionId++;
                }
            }

            // Miscellaneous (fill remaining, ~2 transactions)
            if (dayOfWeek == Calendar.SATURDAY && Math.random() < 0.05 && transactionCount < 200) {
                transaksiDao.insert(new Transaksi(
                        tanggal,
                        String.format("%02d:%02d", 12 + (transactionId % 3), transactionId % 60),
                        "Lain-lain",
                        "Kas",
                        "pengeluaran",
                        50000.0 + (Math.random() * 150000), // Rp50,000 - Rp200,000
                        "Biaya operasional lainnya"
                ));
                transactionCount++;
                transactionId++;
            }

            currentDate.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}