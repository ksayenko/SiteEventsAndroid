package com.honeywell.stevents;




import androidx.core.app.NotificationCompatSideChannelService;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeHelper {

    private static final String AM_LOWERCASE = "am";
    private static final String AM_UPPERCASE = "AM";

    public static final String  DefaultFormat ="MM/dd/yyyy HH:mm:ss";
    private static final String  DefaultJustDateFormat ="MM/dd/yyyy";
    private static final String DefaultJustTimeFormat = "hh:mm a";

    private static final String DefaultJustTimeFormatWithSeconds = "hh:mm:ss a";
    public static String GetDateTimeNow()
    {
        final Calendar currentDateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DefaultFormat);
        String selectedDate = sdf.format(currentDateTime.getTime());
        return selectedDate;
    }

    public static Date GetDateFromDateTime(String strDateTime)
    {
        return GetDateFromDateTime(strDateTime, DefaultFormat);
    }

    public static Date GetDateFromDateTime(String strDateTime, String aFormat)
    {
        Calendar cal = GetCalendarFromDateTime(strDateTime,aFormat);
        return cal.getTime();
    }
        public static Calendar GetCalendarFromDateTime(String strDateTime)
        {
            return GetCalendarFromDateTime(strDateTime,DefaultFormat);
        }

    public static Calendar GetCalendarFromDateTime(String strDateTime, String aFormat)
    {
        if (aFormat == "")
            aFormat =DefaultFormat;

        Calendar theDateTime;

        if (strDateTime == "")
        {
            theDateTime = Calendar.getInstance();
        }
        else {
            ParsePosition pos = new ParsePosition(0);
            SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
            Date theDate = simpledateformat.parse(strDateTime, pos);
            theDateTime = Calendar.getInstance();
            if (theDate != null)
                theDateTime.setTime(theDate);
            if (strDateTime.indexOf(AM_LOWERCASE) != -1 || strDateTime.indexOf(AM_UPPERCASE) != -1) {
                theDateTime.set(Calendar.AM_PM, Calendar.AM);
            } else {
                theDateTime.set(Calendar.AM_PM, Calendar.PM);
            }

        }
        return theDateTime;
    }
    public static int GetYearFromDateTime(String strDateTime, String aFormat)
    {
        Calendar theDateTime = GetCalendarFromDateTime(strDateTime,aFormat);
        return theDateTime.get(Calendar.YEAR);
    }
    public static int GetMonthFromDateTime(String strDateTime, String aFormat)
    {
        Calendar theDateTime = GetCalendarFromDateTime(strDateTime,aFormat);
        return theDateTime.get(Calendar.YEAR);
    }
    public static int GetDayFromDateTime(String strDateTime, String aFormat)
    {
        Calendar theDateTime = GetCalendarFromDateTime(strDateTime,aFormat);
        return theDateTime.get(Calendar.DAY_OF_MONTH);
    }
    public static int GetHourFromDateTime(String strDateTime, String aFormat)
    {
        Calendar theDateTime = GetCalendarFromDateTime(strDateTime,aFormat);
        return theDateTime.get(Calendar.HOUR_OF_DAY);
    }
    public static int GetMinuteFromDateTime(String strDateTime, String aFormat)
    {
        Calendar theDateTime = GetCalendarFromDateTime(strDateTime,aFormat);
        return theDateTime.get(Calendar.MINUTE);
    }
    public static int GetSecFromDateTime(String strDateTime, String aFormat)
    {
        Calendar theDateTime = GetCalendarFromDateTime(strDateTime,aFormat);
        return theDateTime.get(Calendar.SECOND);
    }

    public static String GetStringDateFromDateTime(String strDateTime, String aFormat)
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
    public static String GetStringTimeFromDateTime(String strDateTime)
    {
        return GetStringTimeFromDateTime(strDateTime, DefaultFormat);
    }
    public static String GetStringTimeFromDateTime(String strDateTime, String aFormat)
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

    public static String GetStringDateTimeFromDateAndTime(String strJustDate, String strJustTime)
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

    public static String UpdateTime(String strDateTime, int hour, int minute, int sec) {

        Calendar cal = GetCalendarFromDateTime(strDateTime);
        if (hour < 12)
            cal.set(Calendar.AM_PM, Calendar.AM);
        else
            cal.set(Calendar.AM_PM, Calendar.PM);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, sec);

        SimpleDateFormat sdf = new SimpleDateFormat(DefaultFormat);
        Date dt = cal.getTime();
        String selectedDate = sdf.format(dt);
        return selectedDate;
    }

    public static String UpdateDate(String strDateTime, int year, int month, int day_of_month)
    {

        Calendar cal = GetCalendarFromDateTime(strDateTime);
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH,day_of_month);
        SimpleDateFormat sdf = new SimpleDateFormat(DefaultFormat);
        String selectedDate = sdf.format(cal.getTime());
        return selectedDate;
    }
}
