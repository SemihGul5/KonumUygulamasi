package com.abrebo.konumuygulamasi.data.models;

import java.io.Serializable;

public class Etkinlik implements Serializable {
    private String foto;
    private String foto2;
    private String foto3;
    private String foto4;
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
    private String docID;
    private String uuid;

    public Etkinlik() {
    }

    public Etkinlik(String foto,String foto2,String foto3,String foto4, String ad, String tur, String konum, String aciklama, String enlem, String boylam,String email,
                    String paylasildi_mi,String tarih,String saat,String docID,String uuid) {
        this.foto = foto;
        this.foto2=foto2;
        this.foto3=foto3;
        this.foto4=foto4;
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
        this.docID=docID;
        this.uuid=uuid;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFoto2() {
        return foto2;
    }

    public void setFoto2(String foto2) {
        this.foto2 = foto2;
    }

    public String getFoto3() {
        return foto3;
    }

    public void setFoto3(String foto3) {
        this.foto3 = foto3;
    }

    public String getFoto4() {
        return foto4;
    }

    public void setFoto4(String foto4) {
        this.foto4 = foto4;
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

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
