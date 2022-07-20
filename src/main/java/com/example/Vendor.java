package com.example;

public class Vendor {

    private String VendorName;
    private String EmailContact;
    private String BillingShippingAddress;

    public String getVendorName() {
        return this.VendorName;
    }

    public void setVendorName(String VendorName) {
        this.VendorName = VendorName;
    }

    public String getEmailContact() {
        return this.EmailContact;
    }

    public void setEmailContact(String EmailContact) {
        this.EmailContact = EmailContact;
    }

    public String getBillingShippingAddress() {
        return this.BillingShippingAddress;
    }

    public void setBillingShippingAddress(String BillingShippingAddress) {
        this.BillingShippingAddress = BillingShippingAddress;
    }
}
