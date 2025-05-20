package com.honeywell.stevents;




import androidx.core.app.NotificationCompatSideChannelService;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeHelper {

     private static final String  DefaultFormat ="MM/dd/yyyy hh:mm:ss a";
    private static final String  DefaultJustDateFormat ="MM/dd/yyyy";
    private static final String DefaultJustTimeFormat = "hh:mm:ss a";

    public static String GetDateTimeNow()
    {
        final Calendar currentDateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DefaultFormat);
        String selectedDate = sdf.format(currentDateTime.getTime());
        return selectedDate;
    }

    public static String GetDateFromDateTime(String strDateTime)
    {
        return GetDateFromDateTime(strDateTime, DefaultFormat);
    }
    public static String GetDateFromDateTime(String strDateTime, String aFormat)
    {
        if (aFormat == "")
            aFormat =DefaultFormat;
        SimpleDateFormat sdf = new SimpleDateFormat();
        SimpleDateFormat sdfJustDate = new SimpleDateFormat(DefaultJustDateFormat);
        String selectedDate = "";
        if (strDateTime == "")
        {
            final Calendar currentDateTime = Calendar.getInstance();
             selectedDate = sdfJustDate.format(currentDateTime.getTime());
        }
        else {
            ParsePosition pos = new ParsePosition(0);
            SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
            Date stringDate = simpledateformat.parse(strDateTime, pos);
            selectedDate = sdfJustDate.format(stringDate.getTime());

        }
        return selectedDate;
    }
    public static String GetTimeFromDateTime(String strDateTime)
    {
        return GetTimeFromDateTime(strDateTime, DefaultFormat);
    }
    public static String GetTimeFromDateTime(String strDateTime, String aFormat)
    {

        if (aFormat == "")
            aFormat =DefaultFormat;
        SimpleDateFormat sdfJustTime = new SimpleDateFormat(DefaultJustTimeFormat);
        String selectedDate = "";
        if (strDateTime == "")
        {
            final Calendar currentDateTime = Calendar.getInstance();
            selectedDate = sdfJustTime.format(currentDateTime.getTime());
        }
        else {
            ParsePosition pos = new ParsePosition(0);
            SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
            Date stringDate = simpledateformat.parse(strDateTime, pos);
            selectedDate = sdfJustTime.format(stringDate.getTime());

        }
        return selectedDate;
    }

    public static String GetDateTimeFromDateAndTime(String strJustDate, String strJustTime)
    {
        final Calendar currentDateTime= Calendar.getInstance();
        if (strJustDate == "")
        {
            SimpleDateFormat sdfJustDate= new SimpleDateFormat(DefaultJustDateFormat);
            strJustDate = sdfJustDate.format(currentDateTime.getTime());
        }
        if (strJustTime == "")
        {
            SimpleDateFormat sdfJustTime = new SimpleDateFormat(DefaultJustTimeFormat);
            strJustTime = sdfJustTime.format(currentDateTime.getTime());
        }
        return strJustDate + " " + strJustTime;
    }
}
