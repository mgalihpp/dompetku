package com.neurallift.keuanganku.data;

import com.neurallift.keuanganku.data.dao.AkunDao;
import com.neurallift.keuanganku.data.dao.KategoriDao;
import com.neurallift.keuanganku.data.dao.TransaksiDao;
import com.neurallift.keuanganku.data.model.Akun;
import com.neurallift.keuanganku.data.model.Kategori;
import com.neurallift.keuanganku.data.model.Transaksi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * Utility class to initialize the database with realistic transactions for an Indonesian UMKM (nasi goreng warung).
 * Simulates transactions from January 2024 to June 2025, ensuring positive balance and realistic categories.
 */
public class DatabaseInitializer {

    private static final Random random = new Random();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public static void populateDatabase(AppDatabase db) {
        populateKategori(db.kategoriDao());
        populateAkun(db.akunDao());
        populateTransaksi(db.transaksiDao());
    }

    private static void populateKategori(KategoriDao kategoriDao) {
        String[] kategoriNames = {
                "Penjualan Nasi Goreng", "Penjualan Es Teh", "Bahan Baku",
                "Sewa", "Listrik", "Air", "Gaji Karyawan", "Peralatan", "Pemasaran"
        };
        for (String nama : kategoriNames) {
            kategoriDao.insert(new Kategori(nama));
        }
    }

    private static void populateAkun(AkunDao akunDao) {
        String[] akunNames = {"Kas", "QRIS"};
        for (String nama : akunNames) {
            akunDao.insert(new Akun(nama));
        }
    }

    private static void populateTransaksi(TransaksiDao transaksiDao) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.JANUARY, 1);

        double totalBalance = 0.0;

        while (calendar.get(Calendar.YEAR) <= 2025 &&
                (calendar.get(Calendar.YEAR) < 2025 || calendar.get(Calendar.MONTH) <= Calendar.JUNE)) {
            int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            double monthlyRevenue = 0.0;

            // Determine seasonal factor
            double seasonalFactor = 1.0;
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            if ((year == 2024 && month == Calendar.MARCH) || (year == 2024 && month == Calendar.APRIL) ||
                    (year == 2025 && month == Calendar.MARCH) || (year == 2024 && month == Calendar.DECEMBER)) {
                seasonalFactor = 1.3; // Ramadan, Idul Fitri, or Christmas/New Year
            } else if (month == Calendar.FEBRUARY || (year == 2024 && month == Calendar.MAY)) {
                seasonalFactor = 0.8; // Low season
            }

            // Daily sales (nasi goreng and es teh)
            for (int day = 1; day <= daysInMonth; day++) {
                calendar.set(Calendar.DAY_OF_MONTH, day);
                String date = dateFormat.format(calendar.getTime());

                // Weekend boost
                boolean isWeekend = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                double dailyFactor = isWeekend ? 1.2 : 1.0;

                // Nasi Goreng sales (0–50 portions, IDR 15,000 each)
                int nasiGorengCount = (int) (random.nextInt(51) * dailyFactor * seasonalFactor);
                double nasiGorengRevenue = nasiGorengCount * 15000;
                monthlyRevenue += nasiGorengRevenue;

                // Hitung transaksi cash (70%) dan QRIS (30%) berdasarkan jumlah porsi
                int cashNasiCount = (int) Math.round(nasiGorengCount * 0.7);
                int qrisNasiCount = nasiGorengCount - cashNasiCount; // Pastikan total porsi pas
                double cashNasi = cashNasiCount * 15000;
                double qrisNasi = qrisNasiCount * 15000;

                // Catat transaksi cash per porsi
                for (int i = 0; i < cashNasiCount; i++) {
                    transaksiDao.insert(new Transaksi(
                            date, randomTime(10, 22), "Penjualan Nasi Goreng", "Kas",
                            "pemasukan", 15000, "Penjualan 1 porsi nasi goreng"
                    ));
                    totalBalance += 15000;
                }

                // Catat transaksi QRIS per porsi
                for (int i = 0; i < qrisNasiCount; i++) {
                    transaksiDao.insert(new Transaksi(
                            date, randomTime(10, 22), "Penjualan Nasi Goreng", "QRIS",
                            "pemasukan", 15000, "Penjualan 1 porsi nasi goreng"
                    ));
                    totalBalance += 15000;
                }

                // Es Teh sales (0–30 portions, IDR 5,000 each)
                int esTehCount = (int) (random.nextInt(31) * dailyFactor * seasonalFactor);
                double esTehRevenue = esTehCount * 5000;
                monthlyRevenue += esTehRevenue;

                // Hitung transaksi cash (70%) dan QRIS (30%) berdasarkan jumlah gelas
                int cashEsTehCount = (int) Math.round(esTehCount * 0.7);
                int qrisEsTehCount = esTehCount - cashEsTehCount; // Pastikan total gelas pas
                double cashEsTeh = cashEsTehCount * 5000;
                double qrisEsTeh = qrisEsTehCount * 5000;

                // Catat transaksi cash per gelas
                for (int i = 0; i < cashEsTehCount; i++) {
                    transaksiDao.insert(new Transaksi(
                            date, randomTime(10, 22), "Penjualan Es Teh", "Kas",
                            "pemasukan", 5000, "Penjualan 1 gelas es teh"
                    ));
                    totalBalance += 5000;
                }

                // Catat transaksi QRIS per gelas
                for (int i = 0; i < qrisEsTehCount; i++) {
                    transaksiDao.insert(new Transaksi(
                            date, randomTime(10, 22), "Penjualan Es Teh", "QRIS",
                            "pemasukan", 5000, "Penjualan 1 gelas es teh"
                    ));
                    totalBalance += 5000;
                }
            }

            // Monthly expenses
            // Ingredients (45% of revenue, weekly purchases)
            double ingredientCost = Math.round(monthlyRevenue * 0.45 / 1000.0) * 1000.0;
            for (int i = 0; i < 4; i++) {
                double weeklyIngredient = Math.round(ingredientCost / 4.0 / 1000.0) * 1000.0;
                if (weeklyIngredient > 0) {
                    calendar.set(Calendar.DAY_OF_MONTH, 7 * (i + 1));
                    String date = dateFormat.format(calendar.getTime());
                    transaksiDao.insert(new Transaksi(
                            date, randomTime(8, 12), "Bahan Baku", "Kas",
                            "pengeluaran", weeklyIngredient, "Pembelian bahan baku mingguan"
                    ));
                    totalBalance -= weeklyIngredient;
                }
            }

            // Rent (IDR 1.5M/month, paid on 1st)
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            transaksiDao.insert(new Transaksi(
                    dateFormat.format(calendar.getTime()), randomTime(8, 12), "Sewa", "Kas",
                    "pengeluaran", 1500000.0, "Sewa warung bulanan"
            ));
            totalBalance -= 1500000.0;

            // Electricity (IDR 300K/month, paid on 5th)
            calendar.set(Calendar.DAY_OF_MONTH, 5);
            transaksiDao.insert(new Transaksi(
                    dateFormat.format(calendar.getTime()), randomTime(8, 12), "Listrik", "Kas",
                    "pengeluaran", 300000.0, "Tagihan listrik bulanan"
            ));
            totalBalance -= 300000.0;

            // Water (IDR 100K/month, paid on 5th)
            transaksiDao.insert(new Transaksi(
                    dateFormat.format(calendar.getTime()), randomTime(8, 12), "Air", "Kas",
                    "pengeluaran", 100000.0, "Tagihan air bulanan"
            ));
            totalBalance -= 100000.0;

            // Labor (IDR 1.5M/month per employee, 2 employees, paid on 25th)
            calendar.set(Calendar.DAY_OF_MONTH, 25);
            transaksiDao.insert(new Transaksi(
                    dateFormat.format(calendar.getTime()), randomTime(8, 12), "Gaji Karyawan", "Kas",
                    "pengeluaran", 3000000.0, "Gaji 2 karyawan bulanan"
            ));
            totalBalance -= 3000000.0;

            // Equipment maintenance (every 3 months, IDR 500K–1M)
            if (month % 3 == 0) {
                double equipmentCost = Math.round((random.nextInt(501) + 500) * 1000.0 / 1000.0) * 1000.0;
                calendar.set(Calendar.DAY_OF_MONTH, 15);
                transaksiDao.insert(new Transaksi(
                        dateFormat.format(calendar.getTime()), randomTime(8, 12), "Peralatan", "Kas",
                        "pengeluaran", equipmentCost, "Perbaikan peralatan"
                ));
                totalBalance -= equipmentCost;
            }

            // Marketing (every 3 months, IDR 200K–500K)
            if (month % 3 == 0) {
                double marketingCost = Math.round((random.nextInt(301) + 200) * 1000.0 / 1000.0) * 1000.0;
                calendar.set(Calendar.DAY_OF_MONTH, 20);
                transaksiDao.insert(new Transaksi(
                        dateFormat.format(calendar.getTime()), randomTime(8, 12), "Pemasaran", "Kas",
                        "pengeluaran", marketingCost, "Biaya pemasaran (spanduk, promosi)"
                ));
                totalBalance -= marketingCost;
            }

            // Move to next month
            calendar.add(Calendar.MONTH, 1);
        }
    }

    private static String randomTime(int startHour, int endHour) {
        int hour = random.nextInt(endHour - startHour + 1) + startHour;
        int minute = random.nextInt(60);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        return timeFormat.format(time.getTime());
    }
}