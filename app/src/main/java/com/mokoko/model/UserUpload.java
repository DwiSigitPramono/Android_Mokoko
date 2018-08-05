package com.mokoko.model;


public class UserUpload {

    private String idUser;
    private String namaUser;
    private String emailUser;
    private String telpUser;
    private String fotoURL;
    private String zID;
    private String verified;
    private String credit;

    public UserUpload(){

    }

    public UserUpload(String idUser, String namaUser, String emailUser, String telpUser, String fotoURL, String zID, String verified, String credit) {
        this.idUser = idUser;
        this.namaUser = namaUser;
        this.emailUser = emailUser;
        this.telpUser = telpUser;
        this.fotoURL = fotoURL;
        this.zID = zID;
        this.verified = verified;
        this.credit = credit;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getTelpUser() {
        return telpUser;
    }

    public void setTelpUser(String telpUser) {
        this.telpUser = telpUser;
    }

    public String getFotoURL() {
        return fotoURL;
    }

    public void setFotoURL(String fotoURL) {
        this.fotoURL = fotoURL;
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

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }
}