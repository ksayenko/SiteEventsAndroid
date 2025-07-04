package com.honeywell.stevents;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BooleanSupplier;

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
    private String strM_Per_ID = "NA";
    private String datResDate = "01/01/2000";
    private String datResDate_NoSeconds = "01/01/2000";
    private String ynResolved = "false";

    public String getStrM_Per_FirstLastName() {
        return strM_Per_FirstLastName;
    }

    public void setStrM_Per_FirstLastName(String strM_Per_FirstLastName) {
        this.strM_Per_FirstLastName = strM_Per_FirstLastName;
    }

    public String strM_Per_FirstLastName="na";

    public String getYnResolved3() {
        return ynResolved3;
    }

    public void setYnResolved3(String ynResolved3) {
        this.ynResolved3 = ynResolved3;
    }

    private String ynResolved3 = "NA";
    //YYYY-MM-DD HH:MM:SS
    private String datetimedefault = "2000-01-01";
    private String Value = "";
    private String Unit = "";


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

    public String getStrM_Per_ID() {
        return strM_Per_ID;
    }

    public void setStrM_Per_ID(String strM_Per_ID) {
        this.strM_Per_ID = strM_Per_ID;
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

    public boolean getBoolResolved() {
        boolean rv = false;
        rv = Objects.equals(getYnResolved().toLowerCase(), "true")
                || Objects.equals(getYnResolved(), "1");

        return rv;
    }

    public int getIntResolved() {
        int rv = -1;
        if (Objects.equals(getYnResolved3().toLowerCase(), "true")
                || Objects.equals(getYnResolved3(), "1"))
            rv = 1;
        else if (Objects.equals(getYnResolved3().toLowerCase(), "false")
                || Objects.equals(getYnResolved3(), "0"))
            rv = 0;

        return rv;
    }

    public void setYnResolved(String ynResolved) {
        this.ynResolved = ynResolved;
        this.ynResolved3 = this.ynResolved;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteEvents reading = (SiteEvents) o;
        return
                        strM_Per_FirstLastName.equals(reading.strM_Per_FirstLastName) &&
                        strM_Per_ID.equals(reading.strM_Per_ID) &&
                        datSE_Date.equals(reading.datSE_Date) &&
                        strSE_ID.equals(reading.strSE_ID) &&
                        strEq_ID.equals(reading.strEq_ID) &&
                        strComment.equals(reading.strComment) &&
                        datResDate.equals(reading.datResDate) &&
                        ynResolved.equals(reading.ynResolved) &&
                        Value.equals(reading.Value);
    }

    public boolean equalAllExceptEquipmentOrEquipmentNA(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteEvents reading = (SiteEvents) o;
        if (Objects.equals(reading.strEq_ID, "NA"))
            return true;
        else
            return equalAllExceptEquipment(o);
    }

    public boolean equalAllExceptEquipment(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteEvents reading = (SiteEvents) o;

        boolean rv=
                strM_Per_FirstLastName.equals(reading.strM_Per_FirstLastName) &&
                        //datSE_Date.equals(reading.datSE_Date) &&
                        strSE_ID.equals(reading.strSE_ID) &&
                        strComment.equals(reading.strComment) &&
                        //datResDate.equals(reading.datResDate) &&
                        ynResolved.equals(reading.ynResolved) &&
                        Value.equals(reading.Value);
//        if(!rv)
//            Log.i("CodeDebug", "!EqualAllExceptEquipment : 1: " +this.toString() + " 2: "+ reading.toString());
        return rv;
    }

    @Override
    public String toString() {
        return "SiteEvent{" +
                "lngID=" + lngID +
                ", strEq_ID='" + strEq_ID + '\'' +
                ", strUserName='" + strUserName + '\'' +
                ", strM_Per_FirstLastName='" + strM_Per_FirstLastName + '\'' +
                ", strM_Per_ID='" + strM_Per_ID + '\'' +
                ", strSE_ID='" + strSE_ID + '\'' +
                ", datSE_Date=" + datSE_Date +
                ", strComment='" + strComment + '\'' +
                ", ynResolved=" + ynResolved +
                ", ynResolved3=" + ynResolved3 +
                ", Value='" + Objects.toString(Value, "") + '\'' +
                ", Unit='" + Objects.toString(Unit, "") + '\'' +
                ", datResDate='" + datResDate + '\'' +
                ", meastype='" + measurementType + '\'' +
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

//        Log.i("codedebug", "isRecordValid strEq_ID =" + strEq_ID);
//        Log.i("codedebug", "isRecordValid record =" + this.toString());


        if (isNA(strEq_ID)) {
            isValid.addToValidationMessageError("Please select a Equipment Id. ");
            isValid.setFocus(Validation.FOCUS.EQUIPMENT);
            isValid.setValidation(Validation.VALIDATION.ERROR);

        } else if (isNA(strM_Per_FirstLastName)) {
            isValid.addToValidationMessageError("Please input a User Name ");
            isValid.setFocus(Validation.FOCUS.USER);
            isValid.setValidation(Validation.VALIDATION.ERROR);

        } else if (isNA(strSE_ID)) {
            isValid.addToValidationMessageError("Please input a Site Event Code. ");
            isValid.setFocus(Validation.FOCUS.SITEEVENT);
            isValid.setValidation(Validation.VALIDATION.ERROR);

        } else if (Objects.equals(strComment, "") && measurementType == MeasurementTypes.MEASUREMENT_TYPES.PH
                && getBoolResolved()) {
            message += "Empty Comment";
            isValid.addToValidationMessageWarning("Please Populate a comment.");
            System.out.println(message);
            isValid.setValidation(Validation.VALIDATION.WARNING);
            isValid.setFocus(Validation.FOCUS.SITEEVENT);
        } else if (dValue == 0.0 && measurementType == MeasurementTypes.MEASUREMENT_TYPES.VOC
                && getBoolResolved()) {
            message += "A Reading value of 0 is detected!";
            isValid.addToValidationMessageError("Please enter a Value >0");
            System.out.println(message);
            isValid.setValidation(Validation.VALIDATION.ERROR);
            isValid.setFocus(Validation.FOCUS.READING);
        } else if (dValue == 0.0 && measurementType == MeasurementTypes.MEASUREMENT_TYPES.NOISE) {
            message += "A Reading value of 0 is detected!";
            isValid.addToValidationMessageError("Please enter a Value >0");
            isValid.addToValidationMessageWarning("Please enter a Value>0");
            System.out.println(message);
            isValid.setValidation(Validation.VALIDATION.ERROR);
            isValid.setFocus(Validation.FOCUS.READING);
        } else if ((measurementType == MeasurementTypes.MEASUREMENT_TYPES.OTHER
                || measurementType == MeasurementTypes.MEASUREMENT_TYPES.PH)
                &&                getIntResolved() == -1) {
            message += "Didn't pick resoved it not";
            isValid.addToValidationMessageError("Please choose Resolved Y/N ");
            isValid.addToValidationMessageWarning("Please choose Resolved Y/N ");
            System.out.println(message);
            isValid.setValidation(Validation.VALIDATION.ERROR);
            isValid.setFocus(Validation.FOCUS.READING);
        }else if (measurementType == MeasurementTypes.MEASUREMENT_TYPES.VOC
                &&                getIntResolved() == -1) {
            message += "Didn't pick resoved it not";
            isValid.addToValidationMessageError("Please choose Detected Y/N ");
            isValid.addToValidationMessageWarning("Please choose Detected Y/N ");
            System.out.println(message);
            isValid.setValidation(Validation.VALIDATION.ERROR);
            isValid.setFocus(Validation.FOCUS.READING);
        }else if (measurementType == MeasurementTypes.MEASUREMENT_TYPES.GENERAL_BARCODE
                &&                getIntResolved() == -1) {
            message += "Didn't pick resoved it not";
            isValid.addToValidationMessageError("Please choose Startup or Shutdown.");
            isValid.addToValidationMessageWarning("Please choose Startup or Shutdown");
            System.out.println(message);
            isValid.setValidation(Validation.VALIDATION.ERROR);
            isValid.setFocus(Validation.FOCUS.READING);
        }

        Log.i("codedebug","isRecordValid isValid ="+isValid.getValidation().toString() +  message );
        return isValid;

    }

    public SiteEvents ResetValues()
    {
        //reset values except eqcode and username, use datetime now
        SiteEvents se =  new SiteEvents();
        se.setStrEq_ID(this.getStrEq_ID());
        se.setDatSE_Date(DateTimeHelper.GetDateTimeNow());
        se.setDatResDate(se.getDatSE_Date());
        se.setStrUserName(this.getStrUserName());
        se.setStrUserUploadName(this.getStrUserUploadName());
        se.setStrM_Per_ID(this.getStrM_Per_ID());
        se.setStrM_Per_FirstLastName(this.getStrM_Per_FirstLastName());
        return se;
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
