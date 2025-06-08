package com.neurallift.keuanganku.ui.laporan.model;

public class ChartLegendItem {
    private String kategori;
    private double nominal;
    private int color;

    public ChartLegendItem(String categoryName, double nominal, int color) {
        this.kategori = categoryName;
        this.nominal = nominal;
        this.color = color;
    }

    public String getKategori() {
        return kategori;
    }

    public double getNominal() {
        return nominal;
    }

    public int getColor() {
        return color;
    }
}