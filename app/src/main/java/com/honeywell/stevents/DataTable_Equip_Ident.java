package com.honeywell.stevents;

public class DataTable_Equip_Ident extends AppDataTable {

    /*
    <xs:element name="lngID" type="xs:int" minOccurs="0"/>
<xs:element name="ynCurrent" type="xs:boolean" minOccurs="0"/>
<xs:element name="strEqO_StatusID" type="xs:string" minOccurs="0"/>
<xs:element name="strEqO_StatusDesc" type="xs:string" minOccurs="0"/>
<xs:element name="strComment" type="xs:string" minOccurs="0"/>
<xs:element name="strUserModifyName" type="xs:string" minOccurs="0"/>
<xs:element name="dtLastModificationDate" type="xs:dateTime" minOccurs="0"/>
     */
    public static final String lngID="lngID";
    public static final String strEqID="strEqID";
    public static final String strEqO_StatusDesc="strEqO_StatusDesc";

    public static final String strEqMfg="strEqMfg";

    public static final String strEqModel="strEqModel";

    public static final String strEqSerialNum="strEqSerialNum";

    public static final String strEqDesc="strEqDesc";

    public static final String strEqTypeID="strEqTypeID";

    public static final String ynCurrent="ynCurrent";
    public static final String facility_id="facility_id";




//
//    lngID int  IDENTITY(1,1)  NOT NULL
//     , strEqID nvarchar (25)  NOT NULL
//     , strEqMfg nvarchar (50)
//     , strEqModel nvarchar (50)
//     , strEqSerialNum nvarchar (50)
//     , strEqDesc nvarchar (75)
//     , strEqTypeID nvarchar (15)
//     , ynCurrent bit
//     , strUserModifyName varchar (100)
//     , dtLastModificationDate datetime
//     , facility_id int  NOT NULL


    public DataTable_Equip_Ident(){
        super(HandHeld_SQLiteOpenHelper.EQUIP_IDENT);

        this.AddColumnToStructure(lngID,"Integer",true);
        this.AddColumnToStructure(strEqID,"String",false);
        this.AddColumnToStructure(strEqMfg,"String",false);
        this.AddColumnToStructure(strEqModel,"String",false);
        this.AddColumnToStructure(strEqSerialNum,"String",false);
        this.AddColumnToStructure(strEqDesc,"String",false);
        this.AddColumnToStructure(strEqTypeID,"String",false);
        this.AddColumnToStructure(ynCurrent,"String",false);
        this.AddColumnToStructure(facility_id,"String",false);

    }

}
