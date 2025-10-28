package com.honeywell.stevents;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class Users {
    @SerializedName("strUserName")
    private String strUserName;

    public Users(String strUserName, String name, String domain) {
        this.strUserName = strUserName;
        Name = name;
        Domain = domain;
    }

    public String getDomain() {
        return Domain;
    }

    public void setDomain(String domain) {
        Domain = domain;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @SerializedName("Name")
    private String Name;
    @SerializedName("Domain")
    private String Domain;

    public String getStrUserName() {
        return strUserName;
    }

    public void setStrUserName(String strUserName) {
        this.strUserName = strUserName;
    }

    @Override
    public String toString() {
        return "Users{" +
                "strUserName='" + strUserName + '\'' +
                ", Name='" + Name + '\'' +
                ", Domain='" + Domain + '\'' +
                '}';
    }

    public ArrayList<String> CreateArrayListForInsert()
    {
        DataTable_Users table =  new DataTable_Users();
        ArrayList<String> row = table.getEmptyDataRow();
        row.set(table.GetElementIndex(DataTable_Users.strUserName), strUserName);
        row.set(table.GetElementIndex(DataTable_Users.strName), Name);
        row.set(table.GetElementIndex(DataTable_Users.strDomain), Domain);
        row.set(table.GetElementIndex(DataTable_Users.nID), "-1");
        return   row;
    }

}
