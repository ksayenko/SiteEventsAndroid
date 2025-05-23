package com.honeywell.stevents;

import java.util.ArrayList;

public class DataTable_LoginInfo extends AppDataTable {

    public static final String UserName="UserName";
    public static final String Password="Password";



    public DataTable_LoginInfo(){
        super(HandHeld_SQLiteOpenHelper.LOGININFO);

        this.setTableType(TABLE_TYPE.SYSTEM);
        this.AddColumnToStructure(UserName,"String",true);
        this.AddColumnToStructure(Password,"String",false);

    }



    public void AddToTable( String userName, String password) {
        ArrayList<String> record = new ArrayList<>();
        int n = this.getColumnsNumber();
        for (int j = 0; j < n; j++)
            record.add("");

        record.set(GetElementIndex(UserName), userName);
        record.set(GetElementIndex(Password), password);

        this.AddRowToData(record);

    }

}

