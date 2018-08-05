package com.mokoko.model;


public class Transaksi {

    private String idTrans;
    private String idPengirim;
    private String namaPengirim;
    private String idPenerima;
    private String namaPenerima;
    private String nominal;
    private String timeStamp;
    private String zID;

    public Transaksi(){

    }

    public Transaksi(String idTrans, String idPengirim, String namaPengirim, String idPenerima, String namaPenerima, String nominal, String timeStamp, String zID) {
        this.idTrans = idTrans;
        this.idPengirim = idPengirim;
        this.namaPengirim = namaPengirim;
        this.idPenerima = idPenerima;
        this.namaPenerima = namaPenerima;
        this.nominal = nominal;
        this.timeStamp = timeStamp;
        this.zID = zID;
    }

    public String getIdTrans() {
        return idTrans;
    }

    public void setIdTrans(String idTrans) {
        this.idTrans = idTrans;
    }

    public String getIdPengirim() {
        return idPengirim;
    }

    public void setIdPengirim(String idPengirim) {
        this.idPengirim = idPengirim;
    }

    public String getNamaPengirim() {
        return namaPengirim;
    }

    public void setNamaPengirim(String namaPengirim) {
        this.namaPengirim = namaPengirim;
    }

    public String getIdPenerima() {
        return idPenerima;
    }

    public void setIdPenerima(String idPenerima) {
        this.idPenerima = idPenerima;
    }

    public String getNamaPenerima() {
        return namaPenerima;
    }

    public void setNamaPenerima(String namaPenerima) {
        this.namaPenerima = namaPenerima;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getzID() {
        return zID;
    }

    public void setzID(String zID) {
        this.zID = zID;
    }
}
