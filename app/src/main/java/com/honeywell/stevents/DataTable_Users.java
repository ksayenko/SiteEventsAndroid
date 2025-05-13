package com.honeywell.stevents;

public class DataTable_Users extends AppDataTable {

    public static final String nID="nID";
    public static final String strUserName="strUserName";
    public static final String strEmailAddress="strFO_StatusDesc";


    /*
    <xs:element name="lngID" type="xs:int" minOccurs="0"/>
<xs:element name="ynCurrent" type="xs:boolean" minOccurs="0"/>
<xs:element name="strFO_StatusID" type="xs:string" minOccurs="0"/>
<xs:element name="strFO_StatusDesc" type="xs:string" minOccurs="0"/>
<xs:element name="strComment" type="xs:string" minOccurs="0"/>
<xs:element name="strUserModifyName" type="xs:string" minOccurs="0"/>
<xs:element name="dtLastModificationDate" type="xs:dateTime" minOccurs="0"/>
     */

    public DataTable_Users(){
        super(HandHeld_SQLiteOpenHelper.USERS);

        this.AddColumnToStructure(nID,"Integer",true);
        this.AddColumnToStructure(strUserName,"String",false);
        this.AddColumnToStructure(strEmailAddress,"String",false);

    }

}
