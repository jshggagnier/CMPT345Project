package com.example;

public class WorkReport {
    private int OrderNum;
    private int StaffID;
    private String Message;
    private String Date;
    private boolean CloseWorkorder;
    
    public int getStaffID() {
        return this.StaffID;
    }

    public void setStaffID(int StaffID) {
        this.StaffID = StaffID;
    }

    public int getOrderNum() {
        return this.OrderNum;
    }

    public void setOrderNum(int OrderNum) {
        this.OrderNum = OrderNum;
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

    public boolean getCloseWorkorder() {
        return this.CloseWorkorder;
    }

    public void setCloseWorkorder(boolean CloseWorkorder) {
        this.CloseWorkorder = CloseWorkorder;
    }

    public boolean isCloseWorkorder() {
        return this.CloseWorkorder;
    }

}
