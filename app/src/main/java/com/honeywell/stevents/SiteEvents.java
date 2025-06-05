package com.honeywell.stevents;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class SiteEvents implements Serializable, Cloneable {


    //public enum VALIDATION {VALID,ERROR,WARNING}
//#strd_loc_id	#strusername	#datse_date	#datse_time	#strs_loc_id	#strse_id	#strtofo_id	#streqid	#streqdesc	#strcomment	#strm_per_id	#datresdate	#ynresolved

    private Integer lngID = -1;
    private Integer facility_id = 1;
    private String strD_Loc_ID = null;
    private String strSE_ID = "";
    private String strUserName = "";

    public String getStrUserUploadName() {
        return strUserUploadName;
    }

    public void setStrUserUploadName(String strUserUploadName) {
        this.strUserUploadName = strUserUploadName;
    }

    public  String strUserUploadName="";
    private String datSE_Date = "01/01/2000";
    private String strS_Loc_ID = "99";
    private String strTOFO_ID = null;
    private String strEq_ID = "";
    private String strEqDesc = null;
    private String strComment = "";
    private String strm_Per_ID = null;
    private String datResDate = "01/01/2000";
    private String datResDate_NoSeconds = "01/01/2000";
    private String ynResolved = "false";
    //YYYY-MM-DD HH:MM:SS
    private String datetimedefault = "2000-01-01";
    private String Value = null;
    private String Unit = null;


    public MeasurementTypes.MEASUREMENT_TYPES getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(MeasurementTypes.MEASUREMENT_TYPES measurementType) {
        this.measurementType = measurementType;
    }

    private MeasurementTypes.MEASUREMENT_TYPES measurementType = MeasurementTypes.MEASUREMENT_TYPES.OTHER;

    public String getStrUserName() {
        return strUserName;
    }

    public void setStrUserName(String strUserName) {
        this.strUserName = strUserName;
    }

    public String getDatSE_Date() {
        return datSE_Date;
    }

    public void setDatSE_Date(String datSE_Date) {
        this.datSE_Date = datSE_Date;
        setDatSE_Time(datSE_Date);
        setDatSE_Date_NoSeconds(datSE_Date);
    }

    public String getDatSE_Time() {
        return datSE_Time;
    }

    public void setDatSE_Time(String datSE_Time) {
        this.datSE_Time = datSE_Time;
    }

    public String getDatSE_Date_NoSeconds() {
        return datSE_Date_NoSeconds;
    }

    public void setDatSE_Date_NoSeconds(String datSE_Date) {

        this.datSE_Date_NoSeconds = DataTable_SiteEvent.RemoveSecondsFromDateTime(datSE_Date);
        this.datetimedefault = DataTable_SiteEvent.ConvertDatetimeFormat(datSE_Date, DataTable_SiteEvent.Datetime_pattern_with_sec
                , DataTable_SiteEvent.Datetime_pattern_with_sec);
    }

    public String getStrS_Loc_ID() {
        return strS_Loc_ID;
    }

    public void setStrS_Loc_ID(String strS_Loc_ID) {
        this.strS_Loc_ID = strS_Loc_ID;
    }

    public String getStrSE_ID() {
        return strSE_ID;
    }

    public void setStrSE_ID(String strSE_ID) {
        this.strSE_ID = strSE_ID;
    }

    public String getStrTOFO_ID() {
        return strTOFO_ID;
    }

    public void setStrTOFO_ID(String strTOFO_ID) {
        this.strTOFO_ID = strTOFO_ID;
    }

    public String getStrEq_ID() {
        if (strEq_ID == null)
            return "NA";
        else
            return strEq_ID;
    }

    public void setStrEq_ID(String strEq_ID) {
        this.strEq_ID = strEq_ID;
    }

    public String getStrEqDesc() {
        if (strEqDesc == null)
            return "NA";
        else
            return strEqDesc;

    }

    public void setStrEqDesc(String strEqDesc) {
        this.strEqDesc = strEqDesc;
    }

    public String getStrm_Per_ID() {
        return strm_Per_ID;
    }

    public void setStrm_Per_ID(String strm_Per_ID) {
        this.strm_Per_ID = strm_Per_ID;
    }

    public String getDatResDate() {
        return datResDate;
    }

    public void setDatResDate(String datResDate) {
        this.datResDate = datResDate;
    }

    public String getDatResDate_NoSeconds() {
        return datResDate_NoSeconds;
    }

    public void setDatResDate_NoSeconds(String datResDate_NoSeconds) {
        this.datResDate_NoSeconds = datResDate_NoSeconds;
    }

    public String getYnResolved() {
        return ynResolved;
    }

    public void setYnResolved(String ynResolved) {
        this.ynResolved = ynResolved;
    }

    public String getDatetimedefault() {
        return datetimedefault;
    }

    public void setDatetimedefault(String datetimedefault) {
        this.datetimedefault = datetimedefault;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    private String datSE_Time = "01/01/2000";
    private String datSE_Date_NoSeconds = "01/01/2000";


    private double UNDEFINED = -99999.999;

    public SiteEvents() {
    }

    public static SiteEvents GetDefaultReading() {
        SiteEvents r = new SiteEvents();
        return r;
    }

//    public SiteEvents(Integer lngID,
//                      String strD_Loc_ID,
//                      String strUserName,
//                      String datSE_Date,
//                      String strS_Loc_ID,
//                      String strSE_ID,
//                      String strEq_ID,
//                      String strEqDesc,
//                      String strTOFO_ID,
//                      String strComment,
//                      String strm_Per_ID,
//                      String datRes_Date,
//                      String ynResolved,
//                      String Value, String Unit
//    ) {
//        this.lngID = lngID;
//        this.facility_id = 1;
//
//        this.strD_Loc_ID = strD_Loc_ID;
//        this.strUserName = strUserName;
//        this.datSE_Date = datSE_Date;
//        this.datSE_Time = datSE_Date;
//        this.strS_Loc_ID = strS_Loc_ID;
//
//        this.strSE_ID = strSE_ID;
//        this.strTOFO_ID = strTOFO_ID;
//        this.strEq_ID = strEq_ID;
//        this.strEqDesc = strEqDesc;
//        this.strComment = strComment;
//        this.strm_Per_ID = strm_Per_ID;
//        this.datResDate = datRes_Date;
//        this.ynResolved = ynResolved;
//
//        this.Value = Value;
//        this.Unit = Unit;
//
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteEvents reading = (SiteEvents) o;
        return
                facility_id.equals(reading.facility_id) &&

                        strUserName.equals(reading.strUserName) &&
                        strUserUploadName.equals(reading.strUserUploadName) &&
                        datSE_Date.equals(reading.datSE_Date) &&
                        strSE_ID.equals(reading.strSE_ID) &&
                        strEq_ID.equals(reading.strEq_ID) &&
                        strComment.equals(reading.strComment) &&
                        datResDate.equals(reading.datResDate) &&

                        ynResolved.equals(reading.ynResolved) &&
                        Value.equals(reading.Value);
    }

    @Override
    public String toString() {
        return "SiteEvent{" +
                "lngID=" + lngID +
                ", facility_id=" + facility_id +
                ", strEq_ID='" + strEq_ID + '\'' +
                ", strUserName='" + strUserName + '\'' +
                ", strSE_ID='" + strSE_ID + '\'' +
                ", datSE_Date=" + datSE_Date +
                ", datSE_Time=" + datSE_Time +
                ", datSE_Date_NoSeconds=" + datSE_Date_NoSeconds +

                ", strComment='" + strComment + '\'' +
                ", ynResolved=" + ynResolved +
                ", Value='" + Objects.toString(Value, "") + '\'' +
                ", Unit='" + Objects.toString(Unit, "") + '\'' +
                ", datResDate='" + datResDate + '\'' +
                ", datResDate_NoSeconds='" + datResDate_NoSeconds + '\'' +
                '}';
    }

    public Validation isReadingWithinRange(Double reading) {//}, String[] error_message) {

        Validation isValid = new Validation();
        isValid.setValidation(Validation.VALIDATION.VALID);


        return isValid;
    }

    public Validation isRecordValid() {//(String[] error_message, String[] whereToFocus) {
        String message = "";
        String whereToFocus1 = "";
        Validation isValid = new Validation();
        isValid.setValidation(Validation.VALIDATION.VALID);

        double dValue;

        try {
            dValue = Double.parseDouble(Value);
        } catch (Exception ex) {
            dValue = 0.0;
        }

        if (isNA(strEq_ID)) {
            isValid.addToValidationMessageError("Please select a Equipment Id. ");
            isValid.setFocus(Validation.FOCUS.EQUIPMENT);
            isValid.setValidation(Validation.VALIDATION.ERROR);

        } else if (isNA(strUserName)) {
            isValid.addToValidationMessageError("Please input a User Name ");
            isValid.setFocus(Validation.FOCUS.USER);
            isValid.setValidation(Validation.VALIDATION.ERROR);

        } else if (isNA(strSE_ID)) {
            isValid.addToValidationMessageError("Please input a Site Event Code. ");
            isValid.setFocus(Validation.FOCUS.SITEEVENT);
            isValid.setValidation(Validation.VALIDATION.ERROR);

        } else if (dValue == 0.0 && (measurementType == MeasurementTypes.MEASUREMENT_TYPES.NOISE
                || measurementType == MeasurementTypes.MEASUREMENT_TYPES.VOC) &&
        ynResolved == "true") {
            message += "A Reading value of 0 is detected!";
            System.out.println(message);
            isValid.setValidation(Validation.VALIDATION.WARNING);
            isValid.setFocus(Validation.FOCUS.READING);
        }


        return isValid;

    }

    private boolean isNA(String sValue) {
        boolean isna = sValue == null || sValue.equals("") || sValue.equalsIgnoreCase("NA");
        return isna;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lngID, facility_id);
    }

    public Integer getLngID() {
        return lngID;
    }

    public void setLngID(Integer lngID) {
        this.lngID = lngID;
    }

    public Integer getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(Integer facility_id) {
        this.facility_id = facility_id;
    }

    private void setDatIR_Date_NoSeconds(String datIR_Date) {

    }


    public String getStrD_Loc_ID() {
        return strD_Loc_ID;
    }

    public void setStrD_Loc_ID(String strD_Loc_ID) {

        this.strD_Loc_ID = strD_Loc_ID;
    }


    public String getStrComment() {
        return strComment;
    }

    public void setStrComment(String strComment) {
        this.strComment = strComment;
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
