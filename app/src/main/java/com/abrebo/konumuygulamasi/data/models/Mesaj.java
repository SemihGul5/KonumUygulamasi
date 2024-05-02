package com.abrebo.konumuygulamasi.data.models;

public class Mesaj {
    private String alici_etkinlik_id;
    private String alici_email;
    private String gonderen_ad;
    private String gonderen_email;
    private String mesaj;
    private String saat;
    private String mesajId;

    public Mesaj() {
    }

    public Mesaj(String alici_etkinlik_id,String alici_email, String gonderen_ad, String gonderen_email, String mesaj, String saat, String mesajId) {
        this.alici_etkinlik_id = alici_etkinlik_id;
        this.alici_email=alici_email;
        this.gonderen_ad = gonderen_ad;
        this.gonderen_email = gonderen_email;
        this.mesaj = mesaj;
        this.saat = saat;
        this.mesajId = mesajId;
    }

    public String getAlici_etkinlik_id() {
        return alici_etkinlik_id;
    }

    public void setAlici_etkinlik_id(String alici_etkinlik_id) {
        this.alici_etkinlik_id = alici_etkinlik_id;
    }

    public String getAlici_email() {
        return alici_email;
    }

    public void setAlici_email(String alici_email) {
        this.alici_email = alici_email;
    }

    public String getGonderen_ad() {
        return gonderen_ad;
    }

    public void setGonderen_ad(String gonderen_ad) {
        this.gonderen_ad = gonderen_ad;
    }

    public String getGonderen_email() {
        return gonderen_email;
    }

    public void setGonderen_email(String gonderen_email) {
        this.gonderen_email = gonderen_email;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getSaat() {
        return saat;
    }

    public void setSaat(String saat) {
        this.saat = saat;
    }

    public String getMesajId() {
        return mesajId;
    }

    public void setMesajId(String mesajId) {
        this.mesajId = mesajId;
    }
}
