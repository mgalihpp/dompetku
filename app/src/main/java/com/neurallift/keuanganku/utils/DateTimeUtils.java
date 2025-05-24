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
    private static final SimpleDateFormat DATE_FORMAT_FULL = new SimpleDateFormat("d MMMM yyyy", INDONESIA_LOCALE);
    private static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("d MMM", INDONESIA_LOCALE);

    /**
     * Get today's date in yyyy-MM-dd format
     *
     * @return Today's date string
     */
    public static String getTodayDate() {
        return DATE_FORMAT.format(new Date());
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

        // Set to start of week (Monday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String startDate = DATE_FORMAT.format(calendar.getTime());

        // Set to end of week (Sunday)
        calendar.add(Calendar.DAY_OF_WEEK, 6);
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
