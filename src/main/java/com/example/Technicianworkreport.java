package com.example;

public class Technicianworkreport {
    
    private int StaffID;
    private int OrderNumber;
    private String Message;
    private String Date;
    private int CustID;

    public int getStaffID() {
        return this.StaffID;
    }

    public void setStaffID(int StaffID) {
        this.StaffID = StaffID;
    }

    public int getOrderNumber() {
        return this.OrderNumber;
    }

    public void setOrderNumber(int OrderNumber) {
        this.OrderNumber = OrderNumber;
    }

    public String getMessage() {
        return this.Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getDate() {
        return this.Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public int getCustID() {
        return this.CustID;
    }

    public void setCustID(int CustID) {
        this.CustID = CustID;
    }
}
