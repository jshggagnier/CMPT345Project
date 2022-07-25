package com.example;

public class Customer {
    private int CustIdentifier;
    private String Name;
    private String Email;
    private String PhoneNumber;
    private String Address;
    private String PostalCode;

    public int getCustIdentifier() {
        return this.CustIdentifier;
    }

    public void setCustIdentifier(int CustIdentifier) {
        this.CustIdentifier = CustIdentifier;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getEmail() {
        return this.Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPhoneNumber() {
        return this.PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public String getAddress() {
        return this.Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getPostalCode() {
        return this.PostalCode;
    }

    public void setPostalCode(String PostalCode) {
        this.PostalCode = PostalCode;
    }

}
