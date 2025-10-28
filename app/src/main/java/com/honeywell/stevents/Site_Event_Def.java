package com.honeywell.stevents;

import java.util.ArrayList;
import java.util.Date;

//[{"lngID":172,"ynCurrent":true,"strSE_ID":"Operate","strSE_Desc":"Operate"
// ,"strUserModifyName":"joe.orozco","dtLastModificationDate":"2025-03-19T14:50:28.487"},
public class Site_Event_Def {
    private int lngID;
    private  boolean ynCurrent;

    private String strSE_ID;

    private String strSE_Desc;

    public Site_Event_Def(int lngID, boolean ynCurrent, String strSE_ID, String strSE_Desc,
                          String strUserModifyName, String dtLastModificationDate) {
        this.lngID = lngID;
        this.ynCurrent = ynCurrent;
        this.strSE_ID = strSE_ID;
        this.strSE_Desc = strSE_Desc;
        this.strUserModifyName = strUserModifyName;
        this.dtLastModificationDate = dtLastModificationDate;
    }

    public int getLngID() {
        return lngID;
    }

    public void setLngID(int lngID) {
        this.lngID = lngID;
    }

    public boolean isYnCurrent() {
        return ynCurrent;
    }

    public void setYnCurrent(boolean ynCurrent) {
        this.ynCurrent = ynCurrent;
    }

    public String getStrSE_ID() {
        return strSE_ID;
    }

    public void setStrSE_ID(String strSE_ID) {
        this.strSE_ID = strSE_ID;
    }

    public String getStrSE_Desc() {
        return strSE_Desc;
    }

    public void setStrSE_Desc(String strSE_Desc) {
        this.strSE_Desc = strSE_Desc;
    }

    public String getStrUserModifyName() {
        return strUserModifyName;
    }

    public void setStrUserModifyName(String strUserModifyName) {
        this.strUserModifyName = strUserModifyName;
    }

    public String getDtLastModificationDate() {
        return dtLastModificationDate;
    }

    public void setDtLastModificationDate(String dtLastModificationDate) {
        this.dtLastModificationDate = dtLastModificationDate;
    }

    public Site_Event_Def() {
        this.lngID = -1;
        this.ynCurrent = false;
        this.strSE_ID = "NA";
        this.strSE_Desc = "NA";
        this.strUserModifyName = "NA";
        this.dtLastModificationDate ="1/1/2000";
    }

    private String strUserModifyName;

    private String dtLastModificationDate;

    public ArrayList<String> CreateArrayListForInsert()
    {
        DataTable_Site_Event_Def table =  new DataTable_Site_Event_Def();
        ArrayList<String> row = table.getEmptyDataRow();
        row.set(table.GetElementIndex(table.strSE_Desc), strSE_Desc);
        row.set(table.GetElementIndex(table.strSE_ID), strSE_ID);
        row.set(table.GetElementIndex(table.strSE_Desc), strSE_Desc);
        row.set(table.GetElementIndex(table.ynCurrent), String.valueOf(ynCurrent));
        return   row;
    }
}
