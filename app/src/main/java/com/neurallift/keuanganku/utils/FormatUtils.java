package com.neurallift.keuanganku.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for formatting various data types
 */
public class FormatUtils {

    private static final Locale INDONESIA_LOCALE = new Locale("in", "ID");

    /**
     * Format currency in Indonesian Rupiah (IDR)
     *
     * @param value Amount to format
     * @return Formatted currency string
     */
    public static String formatCurrency(double value) {
        Locale INDONESIA_LOCALE = new Locale("in", "ID");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(INDONESIA_LOCALE);
        currencyFormatter.setMinimumFractionDigits(0);
        currencyFormatter.setMaximumFractionDigits(0);

        // Tambahkan spasi setelah simbol Rp
        DecimalFormatSymbols symbols = ((DecimalFormat) currencyFormatter).getDecimalFormatSymbols();
        symbols.setCurrencySymbol("Rp "); // ← tambahkan spasi di sini
        ((DecimalFormat) currencyFormatter).setDecimalFormatSymbols(symbols);

        return currencyFormatter.format(value);
    }

    /**
     * Format percentage value
     *
     * @param value Percentage value to format
     * @return Formatted percentage string
     */
    public static String formatPercentage(float value) {
        DecimalFormat percentFormatter = new DecimalFormat("0.0%");
        return percentFormatter.format(value / 100);
    }

    /**
     * Format number with thousand separators
     *
     * @param value Number to format
     * @return Formatted number string
     */
    public static String formatNumber(double value) {
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(INDONESIA_LOCALE);
        return numberFormatter.format(value);
    }

    /**
     * Format date with day of the week
     * @param tanggalInput Date string to format
     * @return Formatted date string
     */
    public static String formatTanggalDenganHari(String tanggalInput) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("id", "ID"));
            Date date = inputFormat.parse(tanggalInput);

            SimpleDateFormat outputFormat = new SimpleDateFormat("EE, dd MMM yyyy", new Locale("id", "ID"));
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return tanggalInput; // fallback kalau gagal
        }
    }

    /**
     * Capitalize the first letter of a string
     * @param text String to capitalize
     * @return Capitalized string
     */
    public static String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }


}
