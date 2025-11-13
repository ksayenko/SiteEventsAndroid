package com.honeywell.stevents;

public class DataTable_Equip_Ident extends AppDataTable {

    public static final String lngID="lngID";
    public static final String strEqID="strEqID";

    public static final String strEqMfg="strEqMfg";
    public static final String strEqDesc="strEqDesc";

    public static final String strEqTypeID="strEqTypeID";

    public DataTable_Equip_Ident() {
        super(HandHeld_SQLiteOpenHelper.EQUIP_IDENT);
        this.AddColumnToStructure(strEqID, "String", true);
        this.AddColumnToStructure(strEqDesc, "String", false);
        this.AddColumnToStructure(strEqTypeID, "String", true);
    }

}
