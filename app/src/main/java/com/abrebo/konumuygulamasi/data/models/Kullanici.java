package com.abrebo.konumuygulamasi.data.models;

public class Kullanici {
    private String adSoyad;
    private String kullaniciAdi;
    private String email;
    private String dogumTarihi;
    private String cinsiyet;
    private String kisilik;
    private String tel;
    private String kisilik_durum;

    public Kullanici() {
    }

    public Kullanici(String adSoyad, String kullaniciAdi, String email, String dogumTarihi, String cinsiyet, String kisilik, String tel, String kisilik_durum) {
        this.adSoyad = adSoyad;
        this.kullaniciAdi = kullaniciAdi;
        this.email = email;
        this.dogumTarihi = dogumTarihi;
        this.cinsiyet = cinsiyet;
        this.kisilik = kisilik;
        this.tel = tel;
        this.kisilik_durum = kisilik_durum;
    }

    public String getAdSoyad() {
        return adSoyad;
    }

    public void setAdSoyad(String adSoyad) {
        this.adSoyad = adSoyad;
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDogumTarihi() {
        return dogumTarihi;
    }

    public void setDogumTarihi(String dogumTarihi) {
        this.dogumTarihi = dogumTarihi;
    }

    public String getCinsiyet() {
        return cinsiyet;
    }

    public void setCinsiyet(String cinsiyet) {
        this.cinsiyet = cinsiyet;
    }

    public String getKisilik() {
        return kisilik;
    }

    public void setKisilik(String kisilik) {
        this.kisilik = kisilik;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getKisilik_durum() {
        return kisilik_durum;
    }

    public void setKisilik_durum(String kisilik_durum) {
        this.kisilik_durum = kisilik_durum;
    }
}
