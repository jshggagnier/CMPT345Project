package com.example;

public class Usagereport {
    
    private int ToolID;
    private int RepairID;
    private String Message;

    public int getToolID() {
        return this.ToolID;
    }

    public void setToolID(int ToolID) {
        this.ToolID = ToolID;
    }

    public int getRepairID() {
        return this.RepairID;
    }

    public void setRepairID(int RepairID) {
        this.RepairID = RepairID;
    }

    public String getMessage() {
        return this.Message;
    }

    public void setClaimID(String Message) {
        this.Message = Message;
    }

}