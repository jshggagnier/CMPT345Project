package com.example;

public class Usagereport {
    
    private int ToolID;
    private int PartID;
    private int RepairID;
    private String Message;
    private String Date;

    public int getPartID() {
        return this.PartID;
    }

    public void setPartID(int PartID) {
        this.PartID = PartID;
    }

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

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getMessage() {
        return this.Message;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public String getDate() {
        return this.Date;
    }

}
