package com.honeywell.stevents;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

//[{"lngID":13073,"strEqID":"0000AE-TEST","strEqMfg":null,"strEqModel":null,
// "strEqSerialNum":null,"strEqDesc":"PH-TEST","strEqTypeID":"pHI",
// "ynCurrent":true,"facility_id":1,
// "strUserModifyName":"kateryna.sayenko",
// "dtLastModificationDate":"2025-05-28T10:29:24.257"},
public class Equip_Ident {
    private int lngID;
    private String strEqID;

    private String strEqMfg;

    private String strEqModel;

    private String strEqSerialNum;

    private String strEqDesc;

    private String strEqTypeID;

    private  boolean ynCurrent;

    private int facility_id;

    private String strUserModifyName;

    private String dtLastModificationDate;

    public int getLngID() {
        return lngID;
    }

    public Equip_Ident() {
        this.lngID = -1;
        this.strEqID = "NA";
        this.strEqMfg = "NA";
        this.strEqModel ="NA";
        this.strEqSerialNum = "NA";
        this.strEqDesc = "NA";
        this.strEqTypeID = "NA";
        this.ynCurrent = false;
        this.facility_id = -1;
        this.strUserModifyName = "NA";
        this.dtLastModificationDate = "1/1/2000";
    }

    public Equip_Ident(int lngID, String strEqID, String strEqMfg, String strEqModel,
                       String strEqSerialNum, String strEqDesc,
                       String strEqTypeID, boolean ynCurrent,
                       int facility_id, String strUserModifyName, String dtLastModificationDate) {
        this.lngID = lngID;
        this.strEqID = strEqID;
        this.strEqMfg = strEqMfg;
        this.strEqModel = strEqModel;
        this.strEqSerialNum = strEqSerialNum;
        this.strEqDesc = strEqDesc;
        this.strEqTypeID = strEqTypeID;
        this.ynCurrent = ynCurrent;
        this.facility_id = facility_id;
        this.strUserModifyName = strUserModifyName;
        this.dtLastModificationDate = dtLastModificationDate;
    }

    public void setLngID(int lngID) {
        this.lngID = lngID;
    }

    public String getStrEqID() {
        return strEqID;
    }

    public void setStrEqID(String strEqID) {
        this.strEqID = strEqID;
    }

    public String getStrEqMfg() {
        return strEqMfg;
    }

    public void setStrEqMfg(String strEqMfg) {
        this.strEqMfg = strEqMfg;
    }

    public String getStrEqModel() {
        return strEqModel;
    }

    public void setStrEqModel(String strEqModel) {
        this.strEqModel = strEqModel;
    }

    public String getStrEqSerialNum() {
        return strEqSerialNum;
    }

    public void setStrEqSerialNum(String strEqSerialNum) {
        this.strEqSerialNum = strEqSerialNum;
    }

    public String getStrEqDesc() {
        return strEqDesc;
    }

    public void setStrEqDesc(String strEqDesc) {
        this.strEqDesc = strEqDesc;
    }

    public String getStrEqTypeID() {
        return strEqTypeID;
    }

    public void setStrEqTypeID(String strEqTypeID) {
        this.strEqTypeID = strEqTypeID;
    }

    public boolean isYnCurrent() {
        return ynCurrent;
    }

    public void setYnCurrent(boolean ynCurrent) {
        this.ynCurrent = ynCurrent;
    }

    public int getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(int facility_id) {
        this.facility_id = facility_id;
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

    @Override
    public String toString() {
        return "Equip_ident{" +
                "strEqID='" + strEqID + '\'' +
                ", strEqDesc='" + strEqDesc + '\'' +
                ", strEqTypeID='" + strEqTypeID + '\'' +
                '}';
    }

    public ArrayList<String> CreateArrayListForInsert()
    {
        DataTable_Equip_Ident table =  new DataTable_Equip_Ident();
        ArrayList<String> row = table.getEmptyDataRow();
        row.set(table.GetElementIndex(DataTable_Equip_Ident.strEqID), strEqID);
        row.set(table.GetElementIndex(DataTable_Equip_Ident.strEqDesc), strEqDesc);
        row.set(table.GetElementIndex(DataTable_Equip_Ident.strEqTypeID), strEqTypeID);
        return   row;
    }

    public static ArrayList<Equip_Ident> LoadFromJsonFile(String filename) throws FileNotFoundException {
        Gson gson = new Gson();
        InputStream inputStream =  new FileInputStream(filename);
        InputStreamReader reader = new InputStreamReader(inputStream);
        Type listType = new TypeToken<ArrayList<Equip_Ident>>() {}.getType();
        ArrayList<Equip_Ident> myObjects = gson.fromJson(reader, listType);
        Log.i("populateDB LoadFromJsonFile",filename+"SIZE "+String.valueOf(myObjects.size()));
        return myObjects;

    }
}
