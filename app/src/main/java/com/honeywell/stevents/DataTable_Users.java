package com.honeywell.stevents;

public class DataTable_Users extends AppDataTable {

    public static final String nID="nID";
    public static final String strUserName="strUserName";
    public static final String strName="Name";
    public static final String strDomain="Domain";



    public DataTable_Users(){
        super(HandHeld_SQLiteOpenHelper.USERS);

        this.AddColumnToStructure(nID,"Integer",true);
        this.AddColumnToStructure(strUserName,"String",false);
        this.AddColumnToStructure(strName,"String",false);
        this.AddColumnToStructure(strDomain,"String",false);

    }

}
