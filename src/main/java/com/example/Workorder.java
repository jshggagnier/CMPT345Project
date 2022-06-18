package com.example;
public class Workorder
{
    public String Name;
    public int serialID;

    public int getSerialID() {
        return this.serialID;
    }

    public void setSerialID(int serialID) {
        this.serialID = serialID;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }
}