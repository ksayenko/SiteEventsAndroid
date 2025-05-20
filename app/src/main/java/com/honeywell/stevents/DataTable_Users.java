package com.honeywell.stevents;

public class DataTable_Users extends AppDataTable {

    public static final String nID="nID";
    public static final String strUserName="strUserName";
    public static final String strEmailAddress="strFO_StatusDesc";



    public DataTable_Users(){
        super(HandHeld_SQLiteOpenHelper.USERS);

        this.AddColumnToStructure(nID,"Integer",true);
        this.AddColumnToStructure(strUserName,"String",false);
        this.AddColumnToStructure(strEmailAddress,"String",false);

    }

}
