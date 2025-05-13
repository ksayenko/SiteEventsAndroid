package com.honeywell.stevents;

public class DataTable_Site_Event_Def extends AppDataTable {

    public static final String lngID="lngID";
    public static final String strSE_ID="strSE_ID";
    public static final String strSE_Desc="strSE_Desc";
    public static final String ynCurrent="ynCurrent";


    public DataTable_Site_Event_Def(){
        super(HandHeld_SQLiteOpenHelper.DATA_SITE_EVENT_DEF);

        this.AddColumnToStructure(lngID,"Integer",true);
        this.AddColumnToStructure(strSE_ID,"String",false);
        this.AddColumnToStructure(strSE_Desc,"String",false);
        this.AddColumnToStructure(ynCurrent,"String",false);

    }

}
