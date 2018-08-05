package com.mokoko.model;


public class TokoUpload {

    private String idToko;
    private String alamatToko;
    private String email;
    private String namaPemilik;
    private String namaToko;
    private String telpToko;
    private String kec;
    private String ktpURL;
    private String fotoURL;
    private String lokasiPeta;
    private String zID;
    private String verified;
    private int meter;
    private String credit;

    public TokoUpload(){

    }

    public TokoUpload(String idToko, String alamatToko, String email, String namaPemilik, String namaToko, String telpToko, String kec, String ktpURL, String fotoURL, String lokasiPeta, String zID, String verified, int meter, String credit) {
        this.idToko = idToko;
        this.alamatToko = alamatToko;
        this.email = email;
        this.namaPemilik = namaPemilik;
        this.namaToko = namaToko;
        this.telpToko = telpToko;
        this.kec = kec;
        this.ktpURL = ktpURL;
        this.fotoURL = fotoURL;
        this.lokasiPeta = lokasiPeta;
        this.zID = zID;
        this.verified = verified;
        this.meter = meter;
        this.credit = credit;
    }

    public String getIdToko() {
        return idToko;
    }

    public void setIdToko(String idToko) {
        this.idToko = idToko;
    }

    public String getAlamatToko() {
        return alamatToko;
    }

    public void setAlamatToko(String alamatToko) {
        this.alamatToko = alamatToko;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNamaPemilik() {
        return namaPemilik;
    }

    public void setNamaPemilik(String namaPemilik) {
        this.namaPemilik = namaPemilik;
    }

    public String getNamaToko() {
        return namaToko;
    }

    public void setNamaToko(String namaToko) {
        this.namaToko = namaToko;
    }

    public String getTelpToko() {
        return telpToko;
    }

    public void setTelpToko(String telpToko) {
        this.telpToko = telpToko;
    }

    public String getKec() {
        return kec;
    }

    public void setKec(String kec) {
        this.kec = kec;
    }

    public String getKtpURL() {
        return ktpURL;
    }

    public void setKtpURL(String ktpURL) {
        this.ktpURL = ktpURL;
    }

    public String getFotoURL() {
        return fotoURL;
    }

    public void setFotoURL(String fotoURL) {
        this.fotoURL = fotoURL;
    }

    public String getLokasiPeta() {
        return lokasiPeta;
    }

    public void setLokasiPeta(String lokasiPeta) {
        this.lokasiPeta = lokasiPeta;
    }

    public String getzID() {
        return zID;
    }

    public void setzID(String zID) {
        this.zID = zID;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public int getMeter() {
        return meter;
    }

    public void setMeter(int meter) {
        this.meter = meter;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }
}
