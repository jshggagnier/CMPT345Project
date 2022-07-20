package com.example;
public class Workorder
{
    private int OrderNum;
    private int CustomerNum;
    private String ClaimID;
    private String StartDate;
    private String EndDate;
    private String Description;
    private String CustomerName; // this will be removed shortly, once customers are added properly

    public String getCustomerName() {
        return this.CustomerName;
    }

    public void setCustomerName(String CustomerName) {
        this.CustomerName = CustomerName;
    }

    public String getClaimID() {
        return this.ClaimID;
    }

    public void setClaimID(String ClaimID) {
        this.ClaimID = ClaimID;
    }

    public int getOrderNum() {
        return this.OrderNum;
    }

    public void setOrderNum(int OrderNum) {
        this.OrderNum = OrderNum;
    }

    public int getCustomerNum() {
        return this.CustomerNum;
    }

    public void setCustomerNum(int CustomerNum) {
        this.CustomerNum = CustomerNum;
    }

    public String getStartDate() {
        return this.StartDate;
    }

    public void setStartDate(String StartDate) {
        this.StartDate = StartDate;
    }

    public String getEndDate() {
        return this.EndDate;
    }

    public void setEndDate(String EndDate) {
        this.EndDate = EndDate;
    }
    public String getDescription() {
        return this.Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }
}