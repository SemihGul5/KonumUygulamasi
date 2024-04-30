package com.abrebo.konumuygulamasi.data.models;

public class Etkinlik {
    private String foto;
    private String ad;
    private String tur;
    private String konum;
    private String aciklama;
    private String enlem;
    private String boylam;
    private String email;
    private String paylasildi_mi;
    private String tarih;
    private String saat;


    public Etkinlik() {
    }

    public Etkinlik(String foto, String ad, String tur, String konum, String aciklama, String enlem, String boylam,String email,
                    String paylasildi_mi,String tarih,String saat) {
        this.foto = foto;
        this.ad = ad;
        this.tur = tur;
        this.konum = konum;
        this.aciklama = aciklama;
        this.enlem = enlem;
        this.boylam = boylam;
        this.email=email;
        this.paylasildi_mi=paylasildi_mi;
        this.tarih=tarih;
        this.saat=saat;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getTur() {
        return tur;
    }

    public void setTur(String tur) {
        this.tur = tur;
    }

    public String getKonum() {
        return konum;
    }

    public void setKonum(String konum) {
        this.konum = konum;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getEnlem() {
        return enlem;
    }

    public void setEnlem(String enlem) {
        this.enlem = enlem;
    }

    public String getBoylam() {
        return boylam;
    }

    public void setBoylam(String boylam) {
        this.boylam = boylam;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaylasildi_mi() {
        return paylasildi_mi;
    }

    public void setPaylasildi_mi(String paylasildi_mi) {
        this.paylasildi_mi = paylasildi_mi;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getSaat() {
        return saat;
    }

    public void setSaat(String saat) {
        this.saat = saat;
    }
}
