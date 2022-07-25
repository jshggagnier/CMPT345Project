package com.example;

public class Certification {
    private int CertificationNumber;
    private String CertificationName;
    private String DateObtained;
    private String ExpiryDate;
    private int TechnicianId;

    public int getCertificationNumber() {
        return this.CertificationNumber;
    }

    public void setCertificationNumber(int CertificationNumber) {
        this.CertificationNumber = CertificationNumber;
    }

    public String getCertificationName() {
        return this.CertificationName;
    }

    public void setCertificationName(String CertificationName) {
        this.CertificationName = CertificationName;
    }

    public String getDateObtained() {
        return this.DateObtained;
    }

    public void setDateObtained(String DateObtained) {
        this.DateObtained = DateObtained;
    }

    public String getExpiryDate() {
        return this.ExpiryDate;
    }

    public void setExpiryDate(String ExpiryDate) {
        this.ExpiryDate = ExpiryDate;
    }

    public int getTechnicianId() {
        return this.TechnicianId;
    }

    public void setTechnicianId(int TechnicianId) {
        this.TechnicianId = TechnicianId;
    }

}
