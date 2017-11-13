package com.mulai_berkarya.member.entitas;

public class Ongkir {
    private String jumlah, ongkir, total;
    private String nama, desc, harga, waktu;


    public Ongkir() {

    }

    public Ongkir(String jumlah, String ongkir, String total) {
        super();
        this.jumlah = jumlah;
        this.ongkir = ongkir;
        this.total = total;
    }

    public String getjumlah() {
        return jumlah;
    }

    public void setjumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getongkir() {
        return ongkir;
    }

    public void setongkir(String ongkir) {
        this.ongkir = ongkir;
    }

    public String gettotal() {
        return total;
    }

    public void settotal(String total) {
        this.total = total;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
}
