package com.example;

public class Customer {
    private int CustIdentifier;
    private String Name;
    private String Email;
    private int PhoneNumber;
    private String Address;

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

    public int getPhoneNumber() {
        return this.PhoneNumber;
    }

    public void setPhoneNumber(int PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public String getAddress() {
        return this.Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

}
