package com.neurallift.keuanganku.ui.akun.model;

import com.neurallift.keuanganku.data.model.Akun;

public class AkunWithSaldo {
    private Akun akun;
    private double saldo;

    public AkunWithSaldo(Akun akun, double saldo) {
        this.akun = akun;
        this.saldo = saldo;
    }

    public Akun getAkun() {
        return akun;
    }

    public void setAkun(Akun akun) {
        this.akun = akun;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}