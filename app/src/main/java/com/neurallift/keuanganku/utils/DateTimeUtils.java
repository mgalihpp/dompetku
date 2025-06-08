package com.neurallift.keuanganku.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for date and time operations
 */
public class DateTimeUtils {

    private static final Locale INDONESIA_LOCALE = new Locale("in", "ID");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", INDONESIA_LOCALE);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", INDONESIA_LOCALE);
    private static final SimpleDateFormat DATE_FORMAT_FULL = new SimpleDateFormat("d MMMM yyyy", INDONESIA_LOCALE); private static final SimpleDateFormat DATE_FORMAT_MEDIUM = new SimpleDateFormat("dd MMMM yyyy", INDONESIA_LOCALE);
    private static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("d MMM", INDONESIA_LOCALE);
    private static final SimpleDateFormat DATE_FORMAT_HOUR_MINUTE = new SimpleDateFormat("HH:mm", INDONESIA_LOCALE);

    public static Date parseDate(String dateStr) {
            try {
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                return sdf.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
                return null; // Atau lempar exception sesuai kebutuhan
            }
    }

    /**
     * Get today's date in yyyy-MM-dd format
     *
     * @return Today's date string
     */
    public static String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        return DATE_FORMAT.format(calendar.getTime());
    }

    /**
     * Format Date object to yyyy-MM-dd string
     *
     * @param date Date object to format
     * @return Formatted date string
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * Format Date object to HH:mm string
     *
     * @param date Date object to format
     * @return Formatted time string
     */
    public static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    /**
     * Format date string to full display format (e.g., "10 Januari 2023")
     *
     * @param dateStr Date string in yyyy-MM-dd format
     * @return Formatted date string for display
     */
    public static String formatDateFull(String dateStr) {
        try {
            Date date = DATE_FORMAT.parse(dateStr);
            return DATE_FORMAT_FULL.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }

    /**
     * Format date string to short display format (e.g., "10 Jan")
     *
     * @param dateStr Date string in yyyy-MM-dd format
     * @return Formatted date string for display
     */
    public static String formatDateShort(String dateStr) {
        try {
            Date date = DATE_FORMAT.parse(dateStr);
            return DATE_FORMAT_SHORT.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }

    /**
     * Get date range for current week (Monday to Sunday)
     *
     * @return Array with start and end date strings
     */
    public static String[] getWeekDateRange() {
        Calendar calendar = Calendar.getInstance();

        // Atur agar minggu dimulai dari Senin
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        // Cari hari ini dan geser ke Senin minggu ini
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        int delta = (currentDay == Calendar.SUNDAY) ? -6 : Calendar.MONDAY - currentDay;
        calendar.add(Calendar.DAY_OF_MONTH, delta);
        String startDate = DATE_FORMAT.format(calendar.getTime());

        // Tambahkan 6 hari untuk dapatkan akhir minggu (Minggu)
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        String endDate = DATE_FORMAT.format(calendar.getTime());

        return new String[]{startDate, endDate};
    }


    /**
     * Get date range for current month (1st to last day)
     *
     * @return Array with start and end date strings
     */
    public static String[] getMonthDateRange() {
        Calendar calendar = Calendar.getInstance();

        // Set to first day of month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = DATE_FORMAT.format(calendar.getTime());

        // Set to last day of month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = DATE_FORMAT.format(calendar.getTime());

        return new String[]{startDate, endDate};
    }
}
