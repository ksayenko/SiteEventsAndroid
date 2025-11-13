package com.honeywell.stevents;
//MaintPersIdent
//[{"strM_Per_ID":"JAC","strM_Per_LName":"Caldera",
// "strM_Per_FName":"Andy","IsDefault":1},

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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
        row.set(table.GetElementIndex(DataTable_Maint.strM_Per_FName), strM_Per_FName);
        row.set(table.GetElementIndex(DataTable_Maint.strM_Per_LName), strM_Per_LName);
        row.set(table.GetElementIndex(DataTable_Maint.strM_Per_ID), strM_Per_ID);
        row.set(table.GetElementIndex(DataTable_Maint.IsDefault), String.valueOf(IsDefault));
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

    public static ArrayList<MaintPersIdent> LoadFromJsonFile(String filename) throws FileNotFoundException {
        Gson gson = new Gson();
        InputStream inputStream =  new FileInputStream(filename);
        InputStreamReader reader = new InputStreamReader(inputStream);
        Type listType = new TypeToken<ArrayList<MaintPersIdent>>() {}.getType();
        ArrayList<MaintPersIdent> myObjects = gson.fromJson(reader, listType);
        Log.i("populateDB LoadFromJsonFile",filename+"SIZE "+String.valueOf(myObjects.size()));
        return myObjects;

    }
}
