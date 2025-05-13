package com.honeywell.stevents;
import java.io.Serializable;
import java.util.Objects;

public class SiteEvents implements Serializable {

    public String getLocMin() {
        return locMin;
    }

    public void setLocMin(String locMin) {
        this.locMin = locMin;
    }

    public String getLocMax() {
        return locMax;
    }

    public void setLocMax(String locMax) {
        this.locMax = locMax;
    }

    //public enum VALIDATION {VALID,ERROR,WARNING}
//#strd_loc_id	#strusername	#datse_date	#datse_time	#strs_loc_id	#strse_id	#strtofo_id	#streqid	#streqdesc	#strcomment	#strm_per_id	#datresdate	#ynresolved

    private Integer lngID = -1;
    private Integer facility_id = 1;
    private String strD_Loc_ID = "NA";

    private String strUserName = "NA";
    private String datSE_Date = "01/01/2000";

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
        return strEq_ID;
    }

    public void setStrEq_ID(String strEq_ID) {
        this.strEq_ID = strEq_ID;
    }

    public String getStrEqDesc() {
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

    private String strS_Loc_ID = "99";
    private String strSE_ID = "NA";
    private String strTOFO_ID = "NA";
    private String strEq_ID = "NA";
    private String strEqDesc = "NA";

    private String strComment = "";
    private String strm_Per_ID = "NA";

    private String datResDate = "01/01/2000";
    private String datResDate_NoSeconds = "01/01/2000";

    private String ynResolved = "false";
    //YYYY-MM-DD HH:MM:SS
    private String datetimedefault = "2000-01-01";
    private String Value = null;
    private String Unit = "NA";
    private String locMin = null;
    private String locMax = null;
    private double UNDEFINED = -99999.999;
    public SiteEvents() {
    }

    public static SiteEvents GetDefaultReading() {
        SiteEvents r = new SiteEvents();
        return r;
    }

    public SiteEvents(Integer lngID,
                      String strD_Loc_ID,
                      String strUserName,
                      String datSE_Date,
                      String strS_Loc_ID,
                      String strSE_ID,
                      String strEq_ID,
                      String strEqDesc,
                      String strTOFO_ID,
                      String strComment,
                      String strm_Per_ID,
                      String datRes_Date,
                      String ynResolved,
                      String Value, String Unit
                     ) {
        this.lngID = lngID;
        this.facility_id = 1;

        this.strD_Loc_ID = strD_Loc_ID;
        this.strUserName = strUserName;
        this.datSE_Date = datSE_Date;
        this.datSE_Time = datSE_Date;
        this.strS_Loc_ID = strS_Loc_ID;

        this.strSE_ID = strSE_ID;
        this.strTOFO_ID = strTOFO_ID;
        this.strEq_ID = strEq_ID;
        this.strEqDesc = strEqDesc;
        this.strComment = strComment;
        this.strm_Per_ID = strm_Per_ID;
        this.datResDate = datRes_Date;
        this.ynResolved = ynResolved;

        this.Value = Value;
        this.Unit = Unit;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteEvents reading = (SiteEvents) o;
        return
                facility_id.equals(reading.facility_id) &&
                        strD_Loc_ID.equals(reading.strD_Loc_ID) &&
                        strUserName.equals(reading.strUserName) &&
                        datSE_Date.equals(reading.datSE_Date) &&
                        strD_Loc_ID.equals(reading.strD_Loc_ID) &&
                        strSE_ID.equals(reading.strSE_ID) &&
                        strEq_ID.equals(reading.strEq_ID) &&
                        strComment.equals(reading.strComment) &&
                        datResDate.equals(reading.datResDate) &&
                        strComment.equals(reading.strComment) &&
                        ynResolved.equals(reading.ynResolved) &&
                        Value.equals(reading.Value);
    }

    @Override
    public String toString() {
        return "SiteEvent{" +
                "lngID=" + lngID +
                ", facility_id=" + facility_id +
                ", strUserName='" + strUserName    + '\'' +
                ", datSE_Date=" + datSE_Date    +
                ", datSE_Time=" + datSE_Time +
                ", datSE_Date_NoSeconds=" + datSE_Date_NoSeconds +
                ", strSE_ID='" + strD_Loc_ID + '\'' +
                ", strEq_ID='" + strEq_ID + '\'' +
                ", strComment='" + strComment + '\'' +
                ", ynResolved=" + ynResolved +
                ", Value='" + Value + '\'' +
                ", Unit='" + Unit + '\'' +
                ", datResDate='" + datResDate + '\'' +
                ", datResDate_NoSeconds='" + datResDate_NoSeconds + '\'' +
                '}';
    }

    public Validation isReadingWithinRange(Double reading) {//}, String[] error_message) {

        Validation isValid = new Validation();
        isValid.setValidation(Validation.VALIDATION.VALID);


        // returning the record is valid if the value in the database for loc_min or loc_max is wrong or empty string

        if (locMin == "" || locMax == "") {
            isValid.setValidationMessageWarning("No valid records for loc_min or loc_max in the database");
            isValid.setValidation(Validation.VALIDATION.VALID);
            return isValid;//VALIDATION.VALID;

        }
        double min = 0.0, max = 0.0, val = 0.0;
        try {
            min = Double.parseDouble(locMin);
        } catch (Exception ignored) {
            isValid.setValidationMessageWarning("No valid records for loc_min or loc_max in the database");
            isValid.setValidation(Validation.VALIDATION.VALID);
            return isValid;//VALIDATION.VALID;

        }
        try {
            max = Double.parseDouble(locMax);
        } catch (Exception ignored) {
            isValid.setValidationMessageWarning("No valid records for loc_min or loc_max in the database");
            isValid.setValidation(Validation.VALIDATION.VALID);
            return isValid;//VALIDATION.VALID;

        }

        //Cursor.Current = Cursors.WaitCursor;
        try {
            //NOTE: We can no longer range check flow totalizers now that we switched to location characteristics
            if (strD_Loc_ID.startsWith("FT"))    //if this is a water level location
            {
                if (reading < 0) {
                    isValid.addToValidationMessageError("The Reading value is not a positive number!");
                    isValid.setValidation(Validation.VALIDATION.ERROR);
                } //else
                //isValid = VALIDDATION.VALID;
            }
            //not an FT location 12/2022 KS
            else {
                if (min == UNDEFINED || max == UNDEFINED) {
                    // no defined range
                    isValid.addToValidationMessageError(" no loc_min and loc_max defined range");
                    isValid.setValidation(Validation.VALIDATION.ERROR);
                } else if (reading >= min && reading <= max) {
                    isValid.setValidationMessageValid("OK");// within bounds
                    isValid.setValidation(Validation.VALIDATION.VALID);
                } else {
                    isValid.setValidationMessageWarning("The Reading value falls outside the defined range: " + locMin + ".." + locMax);
                    isValid.setValidation(Validation.VALIDATION.WARNING);
                    System.out.println(isValid.getValidationMessage());
                }
            }
            //Cursor.Current = Cursors.Default;
        } catch (Exception ex) {
            System.out.println("isReadingWithinRange exception " + ex.toString());
        }
        System.out.println("Within range message " + isValid.getValidationMessage());

        return isValid;
    }

    public Validation isRecordValid() {//(String[] error_message, String[] whereToFocus) {
        // String message = "";
        // String whereToFocus1="";
        Validation isValid = new Validation();
        isValid.setValidation(Validation.VALIDATION.VALID);

        double reading;

//        try {
//            reading = Double.parseDouble(dblIR_Value);
//        } catch (Exception ex) {
//            reading = 0.0;
//        }
//
//        if (isNA(strD_Col_ID)) {
//            isValid.addToValidationMessageError("Please select a Data Collector Id. ");
//            isValid.setFocus(Validation.FOCUS.COLLECTOR);
//            isValid.setValidation(Validation.VALIDATION.ERROR);
//        } else if (isNA(strD_Loc_ID)) {
//            isValid.addToValidationMessageError("Please input a Location Id. ");
//            isValid.setFocus(Validation.FOCUS.LOCATION);
//            isValid.setValidation(Validation.VALIDATION.ERROR);
//        } /*else if (isNA(strFO_StatusID)) {
//            //message += "Please select a Facility Oper Status. ";
//            //spin_FAC_OP.requestFocus();
//            //isValid =VALIDDATION.ERROR;
//        } else if (isNA(strEqO_StatusID)) {
//            //message += "Please select an Equipment Oper Status. ";
//            //spin_FAC_OP.requestFocus();
//            //isValid = VALIDDATION.ERROR;
//        } */ else if (strD_Loc_ID.startsWith("WL") && isNA(elev_code)) {
//            isValid.addToValidationMessageError("Water level values require an elevation code. Please select a Elevation Code designator manually. ");
//            isValid.setFocus(Validation.FOCUS.ELEVATION);
//            isValid.setValidation(Validation.VALIDATION.ERROR);
//        }/* else if (reading == 0.0 && strEqO_StatusID.equalsIgnoreCase("NotOper")) {
//            String im1 = "A Reading value of 0, together with a 'NotOper' Equip Oper Status indicates a non-valid reading.";
//            message += im1;
//            isValid = VALIDATION.WARNING;
//            whereToFocus1 = "READING";
//
//        }   else if (reading == 0.0 && !strEqO_StatusID.equalsIgnoreCase("NotOper")) {
//            message += "A Reading value of 0 is detected!";
//            whereToFocus1 = "READING";
//            //to do not valid reeading confirm
//            String[] innermessage = new String[]{""};
//            isValid = VALIDATION.ERROR;
//            message += innermessage[0];
//
//        }*/
//        //Now allow all locations top have a 0 as a possible value with a warning 12082022 KS
//        else if (reading == 0.0) {// && (strD_Loc_ID.startsWith("WL") || strD_Loc_ID.startsWith("FT"))) {
//            String im1 = "A Reading value of 0, for location " + strD_Loc_ID + " is Detected.";
//            isValid.addToValidationMessageWarning(im1);
//            isValid.setValidation(Validation.VALIDATION.WARNING);
//            isValid.setFocus(Validation.FOCUS.READING);
//
//        } else {
//            Validation isValidRange = isReadingWithinRange(reading);
//            if (isValid.getValidation() == Validation.VALIDATION.VALID || isValid.getValidation().value() < isValidRange.getValidation().value()) {
//                isValid = isValidRange;
//            } else if ((isValid.getValidation().value() == isValidRange.getValidation().value()) && isValid.getValidation().value() > 0) {
//                isValid.setFocus(isValidRange.getFocus());
//
//                if (isValidRange.getValidation() == Validation.VALIDATION.WARNING) {
//                    isValid.addToValidationMessageWarning(isValidRange.getValidationMessage());
//
//                }
//                if (isValidRange.getValidation() == Validation.VALIDATION.ERROR) {
//                    isValid.addToValidationMessageError(isValidRange.getValidationMessage());
//                }
//            }
//
//        }

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

}
