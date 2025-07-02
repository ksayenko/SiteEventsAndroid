package com.honeywell.stevents;

public class DataTable_Maint extends AppDataTable {


    public static final String strM_Per_ID="strM_Per_ID";
    public static final String strM_Per_LName="strM_Per_LName";
    public static final String strM_Per_FName="strM_Per_FName";
    public static final String IsDefault="IsDefault";



    public DataTable_Maint(){
        super(HandHeld_SQLiteOpenHelper.MAINTENANCE);
        this.AddColumnToStructure(strM_Per_ID,"String",false);
        this.AddColumnToStructure(strM_Per_LName,"String",false);
        this.AddColumnToStructure(strM_Per_FName,"String",false);
        this.AddColumnToStructure(IsDefault,"Integer",false);

    }

}
