package com.honeywell.stevents;
//MaintPersIdent
//[{"strM_Per_ID":"JAC","strM_Per_LName":"Caldera",
// "strM_Per_FName":"Andy","IsDefault":1},

import java.util.ArrayList;

public class MaintPersIdent {
    String strM_Per_ID;
    String strM_Per_LName;
    String strM_Per_FName;

    int IsDefault;

    public MaintPersIdent(String strM_Per_ID, String strM_Per_LName, String strM_Per_FName, int isDefault) {
        this.strM_Per_ID = strM_Per_ID;
        this.strM_Per_LName = strM_Per_LName;
        this.strM_Per_FName = strM_Per_FName;
        IsDefault = isDefault;
    }

    public MaintPersIdent() {
        this.strM_Per_ID = "NA";
        this.strM_Per_LName  = "NA";
        this.strM_Per_FName = "NA";
        IsDefault = -1;
    }


    public String getStrM_Per_ID() {
        return strM_Per_ID;
    }

    public void setStrM_Per_ID(String strM_Per_ID) {
        this.strM_Per_ID = strM_Per_ID;
    }

    public String getStrM_Per_LName() {
        return strM_Per_LName;
    }

    public void setStrM_Per_LName(String strM_Per_LName) {
        this.strM_Per_LName = strM_Per_LName;
    }

    public String getStrM_Per_FName() {
        return strM_Per_FName;
    }

    public void setStrM_Per_FName(String strM_Per_FName) {
        this.strM_Per_FName = strM_Per_FName;
    }

    public int getIsDefault() {
        return IsDefault;
    }

    public void setIsDefault(int isDefault) {
        IsDefault = isDefault;
    }

    public ArrayList<String> CreateArrayListForInsert()
    {
        DataTable_Maint table =  new DataTable_Maint();
        ArrayList<String> row = table.getEmptyDataRow();
        row.set(table.GetElementIndex(table.strM_Per_FName), strM_Per_FName);
        row.set(table.GetElementIndex(table.strM_Per_LName), strM_Per_LName);
        row.set(table.GetElementIndex(table.strM_Per_ID), strM_Per_ID);
        row.set(table.GetElementIndex(table.IsDefault), String.valueOf(IsDefault));
        return   row;
    }

    @Override
    public String toString() {
        return "MaintPersIdent{" +
                "strM_Per_ID='" + strM_Per_ID + '\'' +
                ", strM_Per_LName='" + strM_Per_LName + '\'' +
                ", strM_Per_FName='" + strM_Per_FName + '\'' +
                ", IsDefault=" + IsDefault +
                '}';
    }
}
