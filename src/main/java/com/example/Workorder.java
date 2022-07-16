package com.example;
public class Workorder
{
    private int OrderNum;
    private int CustomerNum;
    private int ClaimNum;
    private String StartDate;
    private String EndDate;
    private String Description;

    public int getClaimNum() {
        return this.ClaimNum;
    }

    public void setClaimNum(int ClaimNum) {
        this.ClaimNum = ClaimNum;
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