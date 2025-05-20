package com.honeywell.stevents;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataTable_SiteEvent extends AppDataTable {

     public static final String default_strS_Loc_ID = "99";
    public static final String default_strUnit= "NA";
    public static final String lngID="lngID";
    public static final String facility_id="facility_id";
    public static final String strD_Loc_ID="strD_Loc_ID";
    public static final String strUserName="strUserName";
    public static final String datSE_Date  ="datSE_Date";
    public static final String datSE_Time="datSE_Time";
    public static final String strS_Loc_ID  ="strS_Loc_ID";
    public static final String strSE_ID  ="strSE_ID";

    public static final String strTOFO_id  ="strTOFO_id";
    public static final String strEq_ID="strEqID";

    public static final String strEqDesc="strEqDesc";
    public static final String strComment="strComment";

    public static final String strM_Per_ID="strM_Per_ID";
    public static final String datResDate  ="datResDate";
    public static final String ynResolved    ="ynResolved";
   
    public static final String Value="Value";
    public static final String Unit="Unit";

    public static final String device_name="device_name";

    public static final String uploaded="uploaded";
    public static final String uploadedDatetime="uploadedDatetime";
    public static final String recordToUpload="recordToUpload";

    public static final String DateSE_NoSeconds="DateSE_NoSeconds";
    public static final String DateRes_NoSeconds="DateRes_NoSeconds";

    //adding
    public static final String DateMeasurement_NoSeconds="DateMeasurement_NoSeconds";
    public static final String datMesDate  ="datMesDate";
    public static final String ynStartup    ="ynStartup";
    public static final String default_datetimeformat="default_datetimeformat";

    //datetime formats
    public static final String Datetime_pattern_default="YYYY-MM-dd HH:mm:SS";
    public static final String Datetime_pattern_with_sec="MM/dd/yyyy hh:mm:ss a";
    public static final String Datetime_pattern_no_sec="MM/dd/yyyy hh:mm a";
    public static final String Datetime_pattern_dateonly="MM/dd/yyyy";

    public DataTable_SiteEvent(){
        super(HandHeld_SQLiteOpenHelper.SITE_EVENT);
        this.setTableType(TABLE_TYPE.READING);

        this.AddColumnToStructure(lngID,"Integer",true);
        this.AddColumnToStructure(facility_id,"Integer",false);

        this.AddColumnToStructure(strD_Loc_ID,"String",false);
        this.AddColumnToStructure(strUserName ,"String",false);
        this.AddColumnToStructure(datSE_Date   ,"Datetime",false);
        this.AddColumnToStructure(datSE_Time   ,"Datetime",false);

        this.AddColumnToStructure(strS_Loc_ID   ,"String",false);
        this.AddColumnToStructure(strTOFO_id  ,"String",false);
        this.AddColumnToStructure(strSE_ID  ,"String",false);
        this.AddColumnToStructure(strEq_ID,"String",false);
        this.AddColumnToStructure(strEqDesc,"String",false);
        this.AddColumnToStructure(strComment    ,"String",false);
        this.AddColumnToStructure(strM_Per_ID,"String",false);
        this.AddColumnToStructure(datResDate    ,"Datetime",false);
        this.AddColumnToStructure(ynResolved   ,"Boolean",false);

        this.AddColumnToStructure(Value,"Double",false);
        this.AddColumnToStructure(Unit,"String",false);

        this.AddColumnToStructure(uploaded,"Boolean",false);
        this.AddColumnToStructure(uploadedDatetime,"Datetime",false);
        this.AddColumnToStructure(recordToUpload,"Boolean",false);

        this.AddColumnToStructure(device_name,"String",false);
        this.AddColumnToStructure(DateSE_NoSeconds,"String",false);
        this.AddColumnToStructure(DateRes_NoSeconds,"String",false);
        this.AddColumnToStructure(default_datetimeformat,"String",false);
        this.AddColumnToStructure( DateMeasurement_NoSeconds,"String",false);
        this.AddColumnToStructure(datMesDate   ,"Datetime",false);
        this.AddColumnToStructure(ynStartup ,"Boolean",false);

    }

    @Override
    public String getInsertIntoDB(int element){
        String r = super.getInsertIntoDB(element);

        System.out.println(r);

        return r;
    }

    public void AddToTable( ) {

        Integer maxID = -1;
        String facility_id1 = "1";
        String loc1 = "NA";

        String user = "user";
        String reading_value1 = "-9999";
        String datetime1 = "01/01/2000";
        String datetime_nosec1 = "01/01/2000";
        String datetime2 = "01/01/2000";
        String datetime_nosec2 = "01/01/2000";
        String resolved = "true";
        String se1 = "NA";
        String tofo1 = "NA";
        String eq1 = "NA";
        String eqdesc = "NA";
        String el_code1 = "NA";
        String comment1 = "";
        String perid1 = "";
        String recordToUpload1 = "1";
        String sDeviceName = HandHeld_SQLiteOpenHelper.getDeviceName();

        ArrayList<String> reading = new ArrayList<>();
        int n = this.getColumnsNumber();
        for (int j = 0; j < n; j++)
            reading.add("");

        reading.set(GetElementIndex(lngID), String.valueOf(maxID));
        reading.set(GetElementIndex(facility_id), facility_id1);
        reading.set(GetElementIndex(strD_Loc_ID), loc1);
        reading.set(GetElementIndex(strUserName), user);
        reading.set(GetElementIndex(datSE_Date), datetime1);
        reading.set(GetElementIndex(datSE_Date), datetime1);
        reading.set(GetElementIndex(DateSE_NoSeconds),
                DataTable_SiteEvent.RemoveSecondsFromDateTime(datetime1));
        reading.set(GetElementIndex(default_datetimeformat),
                DataTable_SiteEvent.ConvertDatetimeFormat(datetime1,
                        DataTable_SiteEvent.Datetime_pattern_with_sec,
                        DataTable_SiteEvent.Datetime_pattern_default ));

        reading.set(GetElementIndex(strS_Loc_ID ), default_strS_Loc_ID);
        reading.set(GetElementIndex(strSE_ID), se1);
        reading.set(GetElementIndex(strTOFO_id), tofo1);
        reading.set(GetElementIndex(strEq_ID), eq1);
        reading.set(GetElementIndex(strEqDesc), eqdesc);
        reading.set(GetElementIndex(strComment), comment1);
        reading.set(GetElementIndex(strM_Per_ID), perid1);

        reading.set(GetElementIndex(datResDate), datetime1);
        reading.set(GetElementIndex(DateRes_NoSeconds),
                DataTable_SiteEvent.RemoveSecondsFromDateTime(datetime1));
        reading.set(GetElementIndex(ynResolved), resolved);


        reading.set(GetElementIndex(Value), reading_value1);
        reading.set(GetElementIndex(Unit), "NA");

        reading.set(GetElementIndex(recordToUpload), "1");
        reading.set(GetElementIndex(device_name), sDeviceName);

        this.AddRowToData(reading);
     }

    public Integer AddToTable(SiteEvents theReading) {
        ArrayList<String> reading = new ArrayList<>();
        int n = this.getColumnsNumber();
        int nRecords = this.GetNumberOfRecords();
        String sDeviceName = HandHeld_SQLiteOpenHelper.getDeviceName();

        for (int j = 0; j < n; j++)
            reading.add("");

        reading.set(GetElementIndex(lngID), theReading.getLngID().toString());
        reading.set(GetElementIndex(facility_id), theReading.getFacility_id().toString());
        reading.set(GetElementIndex(strD_Loc_ID), theReading.getStrD_Loc_ID());


        reading.set(GetElementIndex(strUserName),theReading.getStrUserName());// user);
        reading.set(GetElementIndex(datSE_Date), theReading.getDatSE_Date().toString());//datetime1);
        reading.set(GetElementIndex(datSE_Date), theReading.getDatSE_Date().toString());
        reading.set(GetElementIndex(DateSE_NoSeconds),
                DataTable_SiteEvent.RemoveSecondsFromDateTime(theReading.getDatSE_Date().toString()));
        reading.set(GetElementIndex(default_datetimeformat),
                DataTable_SiteEvent.ConvertDatetimeFormat(theReading.getDatSE_Date(),
                        DataTable_SiteEvent.Datetime_pattern_with_sec,
                        DataTable_SiteEvent.Datetime_pattern_default ));

        reading.set(GetElementIndex(strS_Loc_ID ), theReading.getStrS_Loc_ID());
        reading.set(GetElementIndex(strSE_ID), theReading.getStrSE_ID());
        reading.set(GetElementIndex(strTOFO_id), theReading.getStrTOFO_ID());
        reading.set(GetElementIndex(strEq_ID), theReading.getStrEq_ID());
        reading.set(GetElementIndex(strEqDesc), theReading.getStrEqDesc());
        reading.set(GetElementIndex(strComment), theReading.getStrEqDesc());
        reading.set(GetElementIndex(strM_Per_ID), theReading.getStrm_Per_ID());

        reading.set(GetElementIndex(datResDate), theReading.getDatResDate());
        reading.set(GetElementIndex(DateRes_NoSeconds),
                DataTable_SiteEvent.RemoveSecondsFromDateTime(theReading.getDatResDate()));
        reading.set(GetElementIndex(ynResolved), theReading.getYnResolved());


        reading.set(GetElementIndex(Value), theReading.getValue());
        reading.set(GetElementIndex(Unit),theReading.getUnit());


        reading.set(GetElementIndex(recordToUpload),"1");
        reading.set(GetElementIndex(device_name), sDeviceName);

        this.AddRowToData(reading);

        return theReading.getLngID() +1;
    }

    public Integer AddToTable(String strUsername1
            ,String datSE_Date1
            , String strSE_ID1
            , String strTOFO_ID1
            , String strEq_ID1
            , String strComment1
            ,String strm_Per_ID1
            ,String datResDate1
            ,String ynResolved1
            ,String Value1
            ,String Unit1
    ) {
        ArrayList<String> reading = new ArrayList<>();
        int n = this.getColumnsNumber();
        int previous_maxid = -1;
        int nRecords = this.GetNumberOfRecords();
        if(nRecords  > 0) {
            previous_maxid = Integer.parseInt(this.getValueFromData(nRecords - 1, lngID));
        }
        Integer maxID =  (int) (new Date().getTime()/1000);
        String sDeviceName = HandHeld_SQLiteOpenHelper.getDeviceName();

        for (int j = 0; j < n; j++)
            reading.add("");

        reading.set(GetElementIndex(lngID), String.valueOf(maxID));
        reading.set(GetElementIndex(facility_id), "1");
        reading.set(GetElementIndex(strD_Loc_ID), "NA");
        reading.set(GetElementIndex(strUserName), strUsername1);
        reading.set(GetElementIndex(datSE_Date), datSE_Date1);
        reading.set(GetElementIndex(datSE_Time), datSE_Date1);
        reading.set(GetElementIndex(DateSE_NoSeconds),
                DataTable_SiteEvent.RemoveSecondsFromDateTime(datSE_Date1));
        reading.set(GetElementIndex(default_datetimeformat),
                DataTable_SiteEvent.ConvertDatetimeFormat(datSE_Date1,
                        DataTable_SiteEvent.Datetime_pattern_with_sec,
                        DataTable_SiteEvent.Datetime_pattern_default ));

        reading.set(GetElementIndex(strS_Loc_ID ), default_strS_Loc_ID);
        reading.set(GetElementIndex(strSE_ID), strSE_ID1);
        reading.set(GetElementIndex(strTOFO_id), strTOFO_ID1);
        reading.set(GetElementIndex(strEq_ID), strEq_ID1);
        reading.set(GetElementIndex(strEqDesc), "na");
        reading.set(GetElementIndex(strComment), strComment1);
        reading.set(GetElementIndex(strM_Per_ID), strm_Per_ID1);

        reading.set(GetElementIndex(datResDate), datResDate1);
        reading.set(GetElementIndex(DateRes_NoSeconds),
                DataTable_SiteEvent.RemoveSecondsFromDateTime(datResDate1));
        reading.set(GetElementIndex(ynResolved), ynResolved1);

        reading.set(GetElementIndex(Value), Value1);
        reading.set(GetElementIndex(Unit), Unit1);

        reading.set(GetElementIndex(recordToUpload), "1");
        reading.set(GetElementIndex(device_name), sDeviceName);

        this.AddRowToData(reading);

        return maxID++;
    }

    public static String RemoveSecondsFromDateTime(String sDateTime) {
       return ConvertDatetimeFormat(sDateTime,
               DataTable_SiteEvent.Datetime_pattern_with_sec,
               DataTable_SiteEvent.Datetime_pattern_no_sec);

    }

    public static String ConvertDatetimeFormat(String sDateTime, String formatFrom, String formatTo) {
        SimpleDateFormat sdf = null;
        Date dt = null;
        String timeStamp = "";
        try {
            sdf = new SimpleDateFormat(formatFrom, Locale.US);

            dt = sdf.parse(sDateTime);
            timeStamp = new SimpleDateFormat(formatTo).format(dt);
        } catch (Exception ex) {
            System.out.println(ex);
            return sDateTime;
        }

        return timeStamp;
    }

    public static String CSVHeader() {
        String header = "#"+strD_Loc_ID
                + "," + strUserName
                + "," + datSE_Date + "," + datSE_Time
                + "," + strS_Loc_ID
                + "," + strSE_ID
                + "," + strTOFO_id
                + "," + strEq_ID
                + "," + strEqDesc
                + "," + strComment
                + "," + strM_Per_ID
                + "," + datResDate
                + ","+ ynResolved;
        return header;
    }




    public static String SelectDataToUpload()        {
        String select =  "Select strd_loc_id," +
                "strusername," +
                "datse_date," +
                "datse_time," +
                "strs_loc_id," +
                "strse_id," +
                "strtofo_id," +
                "streqid," +
                "streqdesc," +
                "strcomment," +
                "strM_Per_ID," +
                "datresdate," +
                "ynresolved," +
                "" +
                " value, unit, device_name  "
                + " from "+HandHeld_SQLiteOpenHelper.SITE_EVENT +
                " where uploaded is null and recordToUpload=1";
        return select;
    }

    public static String UpdateUploadedData() {
        Date currentTime = Calendar.getInstance().getTime();
        String timeStamp = new SimpleDateFormat(DataTable_SiteEvent.Datetime_pattern_default).format(Calendar.getInstance().getTime());

        String update = "UPDATE " + HandHeld_SQLiteOpenHelper.SITE_EVENT
                + " SET uploaded = 1 , uploadedDatetime = '" + timeStamp + "'" +
                 " where ( " + DataTable_SiteEvent.uploaded + " is null  or " + DataTable_SiteEvent.uploaded + " = 0) " +
                "  and  (" + DataTable_SiteEvent.recordToUpload + " = 1 or " + DataTable_SiteEvent.recordToUpload + " is null) ";
        return update;
    }


    public static String PotentialNewDups(String Location, String TheDate) {
        String select = "select count(*) count_dup "
                + " from " + HandHeld_SQLiteOpenHelper.SITE_EVENT
                + " where ( " + DataTable_SiteEvent.uploaded + " is null  or " + DataTable_SiteEvent.uploaded + " = 0) " +
                "  and  (" + DataTable_SiteEvent.recordToUpload + " = 1 or " + DataTable_SiteEvent.recordToUpload + " is null) " +
                " and  " + DataTable_SiteEvent.strEq_ID + " = '"+Location+"' and " + DataTable_SiteEvent.datSE_Date + " like '"+TheDate+"%'";

        return select;
    }

    public static String FindPotentialDuplicates() {
        String select = "select " + DataTable_SiteEvent.strSE_ID + ","
                + DataTable_SiteEvent.strEq_ID + ","
                + DataTable_SiteEvent.DateSE_NoSeconds + ","
                //+ Stdet_Inst_Readings.strD_Col_ID
                + " count(*) count_dup "
                + " from " + HandHeld_SQLiteOpenHelper.SITE_EVENT
                + " where ( " + DataTable_SiteEvent.uploaded + " is null  or " + DataTable_SiteEvent.uploaded + " = 0) " +
                "  and  (" + DataTable_SiteEvent.recordToUpload + " = 1 or " + DataTable_SiteEvent.recordToUpload + " is null) "
                + " group by "
                //+ Stdet_Inst_Readings.strD_Col_ID + ","
                + DataTable_SiteEvent.strSE_ID + ","
                + DataTable_SiteEvent.strEq_ID + ","
                + DataTable_SiteEvent.DateSE_NoSeconds + ","
                + " having count(*) > 1 ";
        return select;
    }


    public static String FindCompleteDuplicates() {

        String select = "select " + DataTable_SiteEvent.strSE_ID + ","
                + DataTable_SiteEvent.strEq_ID + ","
                + DataTable_SiteEvent.DateSE_NoSeconds + ","
                //+ Stdet_Inst_Readings.strD_Col_ID
                + " count(*) count_dup "
                + " from " + HandHeld_SQLiteOpenHelper.SITE_EVENT
                + " where ( " + DataTable_SiteEvent.uploaded + " is null  or " + DataTable_SiteEvent.uploaded + " = 0) " +
                "  and  (" + DataTable_SiteEvent.recordToUpload + " = 1 or " + DataTable_SiteEvent.recordToUpload + " is null) "
                + " group by "
                //+ Stdet_Inst_Readings.strD_Col_ID + ","
                + DataTable_SiteEvent.strSE_ID + ","
                + DataTable_SiteEvent.strEq_ID + ","
                + DataTable_SiteEvent.DateSE_NoSeconds + ","
                + " having count(*) > 1 ";


        return select;
    }

    public Boolean IsPotentialDuplicateInInnerTable(String Location, String TheDate){
        for (int i = 0; i < GetNumberOfRecords(); i++ ) {
            String loc = getValueFromData(i, DataTable_SiteEvent.strSE_ID);
            String dt = getValueFromData(i, DataTable_SiteEvent.datSE_Date).substring(0,10);
            if (loc.equals(Location) && dt.equals(TheDate))
                return true;
        }
        return false;
    }
}
