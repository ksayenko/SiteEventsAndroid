package com.honeywell.stevents;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class HandHeld_SQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HandHeldSE.sqlite3";
    private static final int DATABASE_VERSION = 3;//increased version as we bring new table tbl_MaintPersIdent.xml
    public static String DB_PATH;

    //default facility names:
    public static final String FACILITY_ID = "1";

    public static final String SITE_EVENT = "tbl_Site_Event";
    public static final String SITE_EVENT_DEF = "tbl_Site_Event_Def";
     public static final String EQUIP_IDENT = "tbl_Equip_Ident";
    public static final String USERS = "tbl_Users";
    public final static String MAINTENANCE  = "tbl_MaintPersIdent";
    public static final String LOGININFO = "LoginInfo";

    private AppDataTables tables;
    public static SQLiteDatabase db;


    // filename prefix for the Readings dataset xml file
    public static final String FILEPREFIX = "STE.SFDBSQL."; // Instrument Reading, Second File Mask, version 2
    // filename prefix for converted csv files


    public HandHeld_SQLiteOpenHelper(Context context, AppDataTables tbls) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 4.2) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";

        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }

        tables = tbls;
        getReadableDatabase(); // <-- add this, which triggers onCreate/onUpdate
        AlterDB(this.getReadableDatabase());
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        int n = tables.getDataTables().size();
        String create_table;
        for (int i = 0; i < n; i++) {
            try {
                create_table = tables.getDataTables().get(i).createTableSQL();
                System.out.println("onCreate DB 1" + create_table);
                String tableName = tables.getDataTables().get(i).getName();

                if (!tableName.equalsIgnoreCase("NA")) {
                    db.execSQL(create_table);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("onCreate DB " + ex);

            }
        }
    }

    public void AlterDB(SQLiteDatabase db) {
        try {
            if (!checkColumnExists(db, HandHeld_SQLiteOpenHelper.SITE_EVENT, DataTable_SiteEvent.strM_Per_FirstLastName)) {
                DataTable_SiteEvent ir = new DataTable_SiteEvent();
                String sql = ir.alterIRTableSQLAddColumn(DataTable_SiteEvent.strM_Per_FirstLastName);
                db.execSQL(sql);
            }
//            if (!checkColumnExists(db, HandHeld_SQLiteOpenHelper.INST_READINGS, DataTable_SiteEvent.device_name)) {
//                DataTable_SiteEvent ir = new DataTable_SiteEvent();
//                String sql = ir.alterIRTableSQLAddColumn(DataTable_SiteEvent.device_name);
//                db.execSQL(sql);
//            }
//            if (!checkColumnExists(db, HandHeld_SQLiteOpenHelper.INST_READINGS, DataTable_SiteEvent.datIR_Date_NoSeconds)) {
//                DataTable_SiteEvent ir = new DataTable_SiteEvent();
//                String sql = ir.alterIRTableSQLAddColumn(DataTable_SiteEvent.datIR_Date_NoSeconds);
//                db.execSQL(sql);
//            }
//            if (!checkColumnExists(db, HandHeld_SQLiteOpenHelper.INST_READINGS, DataTable_SiteEvent.default_datetimeformat)) {
//                DataTable_SiteEvent ir = new DataTable_SiteEvent();
//                String sql = ir.alterIRTableSQLAddColumn(DataTable_SiteEvent.default_datetimeformat);
//                db.execSQL(sql);
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("AlterDB SITE_EVENTS " + ex);

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            int n = tables.getDataTables().size();
            String drop_table;
            for (int i = 0; i < n; i++) {
                try {
                    drop_table = "DROP TABLE IF EXISTS " + tables.getDataTables().get(i).getName();
                    db.execSQL(drop_table);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("onUpgrade DB " + ex);

                }
            }
            onCreate(db);
        }
        AlterDB(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void getInsertFromTables(SQLiteDatabase db) {
        int n = tables.getDataTables().size();
        System.out.println("In getInsertFromTables ");

        for (int i = 0; i < n; i++) {


            if (tables != null && tables.getDataTables().get(i).getName() != null) {
                String tbName = tables.getDataTables().get(i).getName();
                System.out.println("In getInsertFromTables " + String.valueOf(i) + " " + tbName);
                if (!tbName.equalsIgnoreCase("NA")) {
                    getInsertFromTable(db, tables.getDataTables().get(i));
                }
            }
        }

    }

    public SiteEvents getSiteEvent(SQLiteDatabase db, String lngId) {
        SiteEvents r = null;
        String qry = "Select  " +
                DataTable_SiteEvent.strD_Loc_ID + ", " +
                DataTable_SiteEvent.strUserName + ", " +
                DataTable_SiteEvent.datSE_Date + ", " +
                DataTable_SiteEvent.strS_Loc_ID + ", " +
                DataTable_SiteEvent.strSE_ID + ", " +
                DataTable_SiteEvent.strTOFO_id + ", " +
                DataTable_SiteEvent.strEq_ID + ", " +
                DataTable_SiteEvent.strEqDesc + ", " +
                DataTable_SiteEvent.strComment + ", " +
                DataTable_SiteEvent.strM_Per_ID + ", " +
                DataTable_SiteEvent.datResDate + ", " +
                DataTable_SiteEvent.ynResolved + ", " +
                DataTable_SiteEvent.Value + ", " +
                DataTable_SiteEvent.Unit + ", " +
                DataTable_SiteEvent.Measurement_Type + ", " +
                DataTable_SiteEvent.strM_Per_FirstLastName + " " +
                " FROM " +
                HandHeld_SQLiteOpenHelper.SITE_EVENT + " Where " +
                DataTable_SiteEvent.lngID + " = " + lngId;

        Cursor c = db.rawQuery(qry, null);
        int index = 0;
        if (c.getCount() > 0) {
            r = new SiteEvents();
            c.moveToFirst();
            r.setLngID(Integer.parseInt(lngId));
            if (!c.isNull(index))//0
                r.setLngID(c.getInt(index));
            index++;
            if (!c.isNull(index)) {//1
                r.setStrUserName(c.getString(index));
                r.setStrUserUploadName(c.getString(index));
            }
            index++;//
            if (!c.isNull(index))//2
                r.setDatSE_Date(c.getString(index));
            index++;
            if (!c.isNull(index))//3
            {
                r.setDatSE_Time(c.getString(index));
            }
            index++;
            if (!c.isNull(index))//4
                r.setStrSE_ID(c.getString(index));
            index++;
            if (!c.isNull(index))//5
                r.setStrTOFO_ID(c.getString(index));
            index++;
            if (!c.isNull(index))//5
                r.setStrEq_ID(c.getString(index));
            index++;
            if (!c.isNull(index))//5
                r.setStrEqDesc(c.getString(index));
            index++;
            if (!c.isNull(index))//5
                r.setStrComment(c.getString(index));
            index++;
            if (!c.isNull(index))//5
                r.setStrM_Per_ID(c.getString(index));
            index++;
            if (!c.isNull(index))//5
                r.setDatResDate(c.getString(index));
            index++;
            if (!c.isNull(index))//5
                r.setYnResolved(c.getString(index));
            index++;

            //value
            if (!c.isNull(index)) {
                double dreading = c.getDouble(index);
                //the max digits after the dot 55.9897384643555
                DecimalFormat df = new DecimalFormat("#.################");
                r.setValue(df.format(dreading));
            }
            index++;
            if (!c.isNull(index))
                r.setUnit(c.getString(index));
            index++;
            //Measurement_Type
            if (!c.isNull(index)) {
                Integer n = c.getInt(index);
                r.setMeasurementType(MeasurementTypes.GetTypeFromNumber(n));
            }
            index++;
            //Maintenance Person FirstLastName
            if (!c.isNull(index))
                r.setStrM_Per_FirstLastName(c.getString(index));

        }
        c.close();

        return r;
    }

    public Boolean getInsertTable(SQLiteDatabase db, AppDataTable table) {

        int n = table.GetNumberOfRecords();
        String insert = "", delete;

        Boolean isInserted = true;
        try {
            String create = table.createTableSQL();
            String tablename = table.getName();
            Log.i("codedebug", "getInsertTable sql="+create);
            System.out.println("getInsertTable " + create);
            db.execSQL(create);
            delete = "Delete from " + tablename;

            if (!tablename.equalsIgnoreCase(HandHeld_SQLiteOpenHelper.SITE_EVENT)) {
                Log.i("codedebug", "delete sql="+delete);
                db.execSQL(delete);
            }

             /*
            How can I speed up my database inserts?

            You can use the following methods to speed up inserts:
            If you are inserting many rows from the same client at the same time,
            use INSERT statements with multiple
            VALUES lists to insert several rows at a time. T
            his is considerably faster (many times faster in some cases)
            than using separate single-row INSERT statements.
             */

            if (n > 100 && table.getTableType() == AppDataTable.TABLE_TYPE.LOOKUP) {
                int k = 0;
                int jump = 100;
                while (k < n) {
                    try {
                        int k_end = ((n - k) >= 100 ? k + jump - 1 : n - 1);
                        System.out.println(k + " - " + k_end);
                        insert = table.getInsertIntoDB(k, k_end);
                        SQLiteStatement statement = db.compileStatement(insert);
                        statement.execute();
                        statement.close();
                        System.out.println(k + " - " + k_end + " insert BIG " + insert);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("insert BIG " + insert + ex);
                        Log.i("codedebug", "getInsertTable error 1="+ex);
                        isInserted = false;
                    }
                    k = k + jump;
                }

            } else {
                String[] login = getLoginInfo(db);
                if (login !=null && login.length >0) {
                    this.updateUserNameInSiteEvents(db, login[0]);
                }
                for (int i = 0; i < n; i++) {
                    try {
                        insert = table.getInsertIntoDB(i);
                        System.out.println("insert " + insert);
                        Log.i("codedebug", "getInsertTable insert 1="+insert);
                        db.execSQL(insert);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("insert " + insert + ex);
                        Log.i("codedebug", "getInsertTable insert 1="+ex);
                        isInserted =  false;
                    }

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR getInsertFromTable " + ex);
            Log.i("codedebug", "getInsertTable error 1="+ex);
            return false;

        }

        return isInserted;
    }

    public void getInsertFromTable(SQLiteDatabase db, AppDataTable table) {
        int n = table.GetNumberOfRecords();

        String create = table.createTableSQL();
        String tablename = table.getName();
        System.out.println("getInsertFromTable " + create);
        Log.i("db", " getInsertFromTable " + create);
        db.execSQL(create);
        String insert = "", delete;
        try {

            delete = "Delete from " + table.getName();
            if (!tablename.equalsIgnoreCase(HandHeld_SQLiteOpenHelper.SITE_EVENT)
                    && !tablename.equalsIgnoreCase(HandHeld_SQLiteOpenHelper.LOGININFO)) {
                System.out.println("getInsertFromTable " + delete);
                db.execSQL(delete);
            }

 /*
            How can I speed up my database inserts?

            You can use the following methods to speed up inserts:
            If you are inserting many rows from the same client at the same time,
            use INSERT statements with multiple
            VALUES lists to insert several rows at a time. T
            his is considerably faster (many times faster in some cases)
            than using separate single-row INSERT statements.
             */

            if (n > 100 && table.getTableType() == AppDataTable.TABLE_TYPE.LOOKUP) {
                int k = 0;
                int jump = 100;
                while (k < n) {
                    try {
                        System.out.println(k);
                        int k_end = ((n - k) >= 100 ? k + jump - 1 : n - 1);
                        insert = table.getInsertIntoDB(k, k_end);
                        SQLiteStatement statement = db.compileStatement(insert);
                        statement.execute();
                        statement.close();
                        System.out.println(k + " - " + k_end + " insert BIG " + insert);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("insert BIG " + insert + ex);
                    }
                    k = k + jump;
                }
            } else {
                for (int i = 0; i < n; i++) {
                    try {
                        insert = table.getInsertIntoDB(i);
                        SQLiteStatement statement = db.compileStatement(insert);
                        statement.execute();
                        statement.close();
                        //db.execSQL(insert);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("insert " + insert + ex);
                    }

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("getInsertFromTable " + ex);

        }
    }


    public int getUpdateSiteEvent(SQLiteDatabase db, SiteEvents r) {
        String temp;
        ContentValues values = new ContentValues();
        values.put(DataTable_SiteEvent.Value, r.getValue());
       values.put(DataTable_SiteEvent.Unit, r.getUnit());

       temp = r.getStrComment();
       if (Objects.equals(temp, ""))
            values.putNull(DataTable_SiteEvent.strComment);
       else
            values.put(DataTable_SiteEvent.strComment, temp);

       Log.i("codedebug", "UPDATE "+ r.getStrM_Per_ID()+ r.toString());

        values.put(DataTable_SiteEvent.strSE_ID, r.getStrSE_ID());
        values.put(DataTable_SiteEvent.strM_Per_ID, r.getStrM_Per_ID());
        values.put(DataTable_SiteEvent.datSE_Date, r.getDatSE_Date());
        values.put(DataTable_SiteEvent.datSE_Time, r.getDatSE_Time());
        values.put(DataTable_SiteEvent.datResDate, r.getDatResDate());
        values.put(DataTable_SiteEvent.ynResolved, r.getYnResolved());
        values.put(DataTable_SiteEvent.strUserName, r.getStrUserName());
        values.put(DataTable_SiteEvent.strUserUploadName, r.getStrUserUploadName());
        values.put(DataTable_SiteEvent.Measurement_Type, r.getMeasurementType().value());
        values.put(DataTable_SiteEvent.strM_Per_FirstLastName, r.getStrM_Per_FirstLastName());

        int rowsUpdated = db.update(HandHeld_SQLiteOpenHelper.SITE_EVENT, values, DataTable_SiteEvent.lngID + "=" + r.getLngID(), null);

        return rowsUpdated;

    }



    public Cursor getSE_ShortList(SQLiteDatabase db) {
        return getSE_ShortList(db, "order by " +
                DataTable_SiteEvent.datSE_Date + " desc , "
                + DataTable_SiteEvent.datSE_Time + " DESC");


    }

    public Cursor getSE_ShortList(SQLiteDatabase db, String orderby) {
        String qry = "select  rowid as _id, " +
                DataTable_SiteEvent.strEq_ID + ", " +
                DataTable_SiteEvent.datSE_Time + ", " + "'Saved', " +
                DataTable_SiteEvent.lngID +", "+
                DataTable_SiteEvent.Measurement_Type + " from " +
                HandHeld_SQLiteOpenHelper.SITE_EVENT +
                " where (uploaded is null or uploaded =0) and (" + DataTable_SiteEvent.recordToUpload + " = 1 or " + DataTable_SiteEvent.recordToUpload + " is null) " + orderby;
        System.out.println(qry);
        return db.rawQuery(qry, null);
    }

    public int deleteRecords(SQLiteDatabase db, String lngid) {
        // String qry = " delete from "+   HandHeld_SQLiteOpenHelper.INST_READINGS +
        //        " where lngid = " + lngid;
        return db.delete(HandHeld_SQLiteOpenHelper.SITE_EVENT, DataTable_SiteEvent.lngID + "=?", new String[]{lngid});
    }


    public Cursor getSiteEventRecords(SQLiteDatabase db) {
        return getSiteEventRecords(db, "order by " + DataTable_SiteEvent.datSE_Date + " desc");
    }

    public Cursor getSiteEventRecords(SQLiteDatabase db, String orderby) {
        String qry = "select  rowid as _id, " + DataTable_SiteEvent.strD_Loc_ID + ", " +
                DataTable_SiteEvent.strUserUploadName + ", " +
                DataTable_SiteEvent.datSE_Date + ", " +
                DataTable_SiteEvent.datSE_Time + ", " +
                DataTable_SiteEvent.strS_Loc_ID + ", " +
                DataTable_SiteEvent.strSE_ID + ", " +
                DataTable_SiteEvent.strTOFO_id + ", " +
                DataTable_SiteEvent.strEq_ID + ", " +
                DataTable_SiteEvent.strEqDesc + ", " +
                DataTable_SiteEvent.strComment + ", " +
                DataTable_SiteEvent.strM_Per_ID + ", " +
                DataTable_SiteEvent.datResDate + ", " +
                DataTable_SiteEvent.ynResolved + ", " +
                DataTable_SiteEvent.Value + ", " +
                DataTable_SiteEvent.Unit + ", " +

                DataTable_SiteEvent.device_name +","+
                DataTable_SiteEvent.Measurement_Type +
                " from " +
                HandHeld_SQLiteOpenHelper.SITE_EVENT +
                " where (uploaded is null or uploaded =0) and (" + DataTable_SiteEvent.recordToUpload + " = 1 or " + DataTable_SiteEvent.recordToUpload + " is null) " + orderby;
        return db.rawQuery(qry, null);
    }

    public int getRowsInLookupTables(SQLiteDatabase db) {
        AppDataTables tabels = new AppDataTables();
        int count = 0;
        for (int i = 0; i < tables.getDataTables().size(); i++) {
            //--tbl_Equip_Oper_Def
            if (tables.getDataTables().get(i).getTableType() == AppDataTable.TABLE_TYPE.LOOKUP) {
                String create = tables.getDataTables().get(i).createTableSQL();
                String countrowssql = tables.getDataTables().get(i).getRowCountSQL();

                db.execSQL(create);
                Cursor c = db.rawQuery(countrowssql, null);
                c.moveToFirst();
                count += c.getInt(0);
                c.close();
            }

        }
        return count;

    }

    public String[] getLoginInfo(SQLiteDatabase db) {
        DataTable_LoginInfo t = new DataTable_LoginInfo();
        db.execSQL(t.createTableSQL());// in case it doesn't exixts yet

        String qry = "select  rowid as _id, " + DataTable_LoginInfo.UserName + ", " +
                DataTable_LoginInfo.Password + " from " +
                HandHeld_SQLiteOpenHelper.LOGININFO;
        Cursor c = db.rawQuery(qry, null);
        String[] credentials = new String[]{"", ""};
        if (c.getCount() > 0) {
            c.moveToFirst();
            credentials[0] = c.getString(1);
            credentials[1] = c.getString(2);
        }
        return credentials;
    }

    public void updateLoginInformationInDB(SQLiteDatabase db, String name, String enPwd) {
        DataTable_LoginInfo login = new DataTable_LoginInfo();
        login.AddToTable(name, enPwd);
        String create = login.createTableSQL();
        db.execSQL(create);
        getInsertTable(db, login);

    }
    public void updateUserNameInSiteEvents(SQLiteDatabase db, String name) {

        String update = "UPDATE " + HandHeld_SQLiteOpenHelper.SITE_EVENT +
                " SET " + DataTable_SiteEvent.strUserUploadName + " = '" + name + "', " +
                DataTable_SiteEvent.strUserName + " = '" + name + "' " +
                " where ( " + DataTable_SiteEvent.uploaded + " is null  or " + DataTable_SiteEvent.uploaded + " = 0) " +
                "  and  (" + DataTable_SiteEvent.recordToUpload + " = 1 or " + DataTable_SiteEvent.recordToUpload + " is null) " +
                "  and  (" + DataTable_SiteEvent.strUserName + "  is null or  " +  DataTable_SiteEvent.strUserName + "  ='')";
        db.execSQL(update);

    }



    public Integer getMaxID_FromSiteEventsTable(SQLiteDatabase db) {
        int rv = 0;
        String qry = "select  max (lngId) from tbl_site_event";
        try {
            Cursor c = db.rawQuery(qry, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                rv = c.getInt(0);
            }
            c.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return rv;
    }



    public Cursor GetCursorEquipment(SQLiteDatabase db) {
       
        //	strEqID
        //	strEqMfg
        //	strEqModel
        //	strEqSerialNum
        //	strEqDesc
        //	strEqTypeID
        //	ynCurrent
        return db.rawQuery("Select rowid _id, strEqID, strEqTypeID, '1' as ord from tbl_Equip_Ident   " +
                " UNION ALL SELECT -1,'NA','NA', '0' order by ord, strEqID, strEqTypeID", null);
    }


    public Cursor GetCursorMaintenancePerson(SQLiteDatabase db) {

        String sql = "Select rowid _id, " +
                "IFNULL("+ DataTable_Maint.strM_Per_FName +",'')"+  "|| ', ' ||+ " +
                 "IFNULL("+ DataTable_Maint.strM_Per_LName +",'')"+   " as Name, "
                + DataTable_Maint.strM_Per_ID +","
                + DataTable_Maint.strM_Per_LName +","
                + DataTable_Maint.strM_Per_FName +","
                + DataTable_Maint.IsDefault + ",'1' as ord from "
                + HandHeld_SQLiteOpenHelper.MAINTENANCE+
                " UNION ALL SELECT -1,'NA', 'NA', 'NA', 'NA', '-1', '0' order by ord, "
                + DataTable_Maint.IsDefault + " desc, "
                + DataTable_Maint.strM_Per_LName + ", "
                + DataTable_Maint.strM_Per_FName;
        Log.i("codedebug", "GetCursorMaintenancePerson " + sql);

        return db.rawQuery(sql, null);
    }


    public Cursor GetCursorSECode(SQLiteDatabase db) {
    //lngID
    //ynCurrent
    //strSE_ID
    //strSE_Desc
    //strUserModifyName
    //dtLastModificationDate
        return db.rawQuery("Select rowid _id, strSE_ID, strSE_Desc, '1' as ord from tbl_Site_Event_Def   " +
                " UNION ALL SELECT -1,'NA','NA', '0' order by ord, strSE_ID, strSE_Desc", null);
    }

    public ArrayList<String[]> CursorToArrayList(Cursor cursor) {
        ArrayList<String[]> arrayList = new ArrayList<>();
        int nCol = cursor.getColumnCount();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // The Cursor is now set to the right position
            String[] strs = new String[nCol];
            for (int i = 0; i < nCol; i++) {
                strs[i] = (String) cursor.getString(i);
            }
            arrayList.add(strs);
        }
        return arrayList;
    }

    public String GetMaintenanceInitialsByFirstLastName(SQLiteDatabase db, String spinUserName)
    {
        String[] flname = spinUserName.split(",");
        if (flname.length <2) {
            flname = new String[2];
            flname[0] = spinUserName;
            flname[1] = spinUserName;
        }


        String sql = "Select "+ DataTable_Maint.strM_Per_ID + " from " + MAINTENANCE
                + "  where "+ DataTable_Maint.strM_Per_FName + " = '" + flname[0].trim() +"' and "
                + DataTable_Maint.strM_Per_LName + " = '" + flname[1].trim() +"'";

        Log.i("codedebug","GetMaintenanceInitialsByFirstLastName " +  sql );
        
        return GeneralQueryFirstValue(db, sql);
    }

    public String GetEqDescDB(SQLiteDatabase db, String strEqId)
    {
        String sql = "Select "+ DataTable_Equip_Ident.strEqDesc + " from " + EQUIP_IDENT
                + "  where "+ DataTable_Equip_Ident.strEqID + " = '" + strEqId+"'";

        String eq = GeneralQueryFirstValue(db, sql);
        if (eq == null)
            eq = "";
        Log.i("codedebug", "GetEqDescDB " + sql + eq);
        return eq;
    }
    public String GetDefaultSiteEventByEquipment(SQLiteDatabase db, String strEqId) {
        String defaultSe = GeneralQueryFirstValue(db, "select " + DataTable_Site_Event_Def.strSE_Desc +
                " from " + SITE_EVENT_DEF + " where " + DataTable_Site_Event_Def.strSE_ID + " like 'misc%'");
        String strEqType = GeneralQueryFirstValue(db, "select " + DataTable_Equip_Ident.strEqTypeID +
                " from " + EQUIP_IDENT + " where " + DataTable_Equip_Ident.strEqID + " = '" + strEqId + "'");
        if (strEqType == null)
            strEqType = "";
        MeasurementTypes.MEASUREMENT_TYPES m = MeasurementTypes.GetFrom_SE_ID(strEqId, strEqType);

        String sql = "Select " + DataTable_Equip_Ident.strEqDesc + " from " + EQUIP_IDENT
                + "  where " + DataTable_Equip_Ident.strEqID + " = '" + strEqId + "'";

        if (m == MeasurementTypes.MEASUREMENT_TYPES.GENERAL_BARCODE) {
            defaultSe = "Operate";
        } else if (m == MeasurementTypes.MEASUREMENT_TYPES.NOISE) {
            defaultSe = "Monitor";
        } else if (m == MeasurementTypes.MEASUREMENT_TYPES.PH) {
            defaultSe = "Maintain";
        } else if (m == MeasurementTypes.MEASUREMENT_TYPES.VOC) {
            defaultSe = "Monitor";
        } else if (m == MeasurementTypes.MEASUREMENT_TYPES.OTHER
                && (strEqId.equalsIgnoreCase("F-933-01")
                || strEqId.equalsIgnoreCase("F-933-02")
                || strEqId.equalsIgnoreCase("F-600-04")
                || strEqId.equalsIgnoreCase("V-971-01")
                || strEqId.equalsIgnoreCase("V-971-02"))) {
            defaultSe = "Media";
        }
          else if (m == MeasurementTypes.MEASUREMENT_TYPES.OTHER
                    && (strEqId.equalsIgnoreCase("NA"))) {
                defaultSe = "NA";
            }

        Log.i("codedebug", "GetDefaultSiteEventByEquipment " + defaultSe);
        return defaultSe;
    }

    public String GetDefaultMaintenancePerson(SQLiteDatabase db) {
        String default_user = "NA";
        String sql = "Select  IFNULL(" + DataTable_Maint.strM_Per_FName + ",'')|| ', ' || IFNULL(" + DataTable_Maint.strM_Per_LName + ",'')  from " + MAINTENANCE
                + "  order by " + DataTable_Maint.IsDefault + " desc LIMIT 1";

        Log.i("codedebug", "GetDefaultMaintenancePerson: sql " + sql);

        try {
            default_user = GeneralQueryFirstValue(db, sql);
        } catch (Exception ex) {
            Log.i("codedebug", "GetDefaultMaintenancePerson " + ex.toString());


        }
        Log.i("codedebug", "GetDefaultMaintenancePerson : " + default_user);
        return default_user;
    }

    public static void cursorToStringArray(Cursor c,
                                           ArrayList<String> arrayList,
                                           String columnName) {
        int columnIndex = c.getColumnIndex(columnName);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            arrayList.add(c.getString(columnIndex));
        }
    }

    public void Update_SETable_IfNeeded(SQLiteDatabase db) {

        String device_name = HandHeld_SQLiteOpenHelper.getDeviceName();
        //update device
        String update1 = "update " + HandHeld_SQLiteOpenHelper.SITE_EVENT
                + " set " + DataTable_SiteEvent.device_name + " = "
                + getStringQuotedValue(device_name)
                + " where (uploaded is null  or uploaded = 0) and  (recordToUpload = 1 or recordToUpload is null)=1 "
                + " and " + DataTable_SiteEvent.device_name + " is null";
        db.execSQL(update1);
    }


    public String GeneralQueryFirstValue(SQLiteDatabase db, String sql) {
        String rv = "";
        Cursor records = db.rawQuery(sql, null);
        int nRecords = records.getCount();
        if (nRecords > 0) {
            records.moveToFirst();
            rv = records.getString(0);
            return rv;
        }
        return "";
    }

    public String PotentialDuplicatesMesssage(SQLiteDatabase db) {
        StringBuilder returnMessage = new StringBuilder();
        String sql = DataTable_SiteEvent.FindPotentialDuplicates();
        try {
            Cursor records = db.rawQuery(sql, null);
            int nRecords = records.getCount();
            Integer nCol = records.getColumnCount();

            if (nRecords == 0)
                return returnMessage.toString();

            Integer i_strD_Loc_ID = records.getColumnIndex(DataTable_SiteEvent.strD_Loc_ID);
            if (i_strD_Loc_ID < 0)
                i_strD_Loc_ID = 0;
            Integer i_count_dup = records.getColumnIndex("count_dup");
            if (i_count_dup < 0)
                i_count_dup = 0;

            returnMessage.append("Potential Duplicates Found For Locations :");
            for (records.moveToFirst(); !records.isAfterLast(); records.moveToNext()) {
                // The Cursor is now set to the right position
                returnMessage.append(records.getString(i_strD_Loc_ID)).append("(").append(records.getString(i_count_dup)).append(" records), ");

            }
            returnMessage = new StringBuilder(replaceLast(returnMessage.toString(), ",", "."));

        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return returnMessage.toString();
    }

    public String MarkNotToUploadCompleteDuplicates(SQLiteDatabase db) throws ParseException {
        // update NumberOfDuplicates - 1 not to upload if complete duplicates found
        String sql = DataTable_SiteEvent.FindCompleteDuplicates();
        StringBuilder returnMessage = new StringBuilder();
        Cursor records = db.rawQuery(sql, null);
        Integer nRecordsUpdated = 0;

        int nRecords = records.getCount();
        Integer nCol = records.getColumnCount();

        if (nRecords == 0)
            return returnMessage.toString();

//
//        returnMessage = new StringBuilder("Records for Location - Date were marked as not to upload : ");
//
//        String s_facility_id;
//        String s_datIR_Date;
//        String s_datIR_Time;
//        String s_strD_Col_ID;
//        String s_strD_Loc_ID;
//        String s_strFO_StatusID;
//        String s_dblIR_Value;
//        String s_strIR_Units;
//        String s_strComment;
//        String s_strEqO_StatusID;
//        String s_strDataModComment;
//        String s_elev_code = "";
//
//
//        Integer i_facility_id = records.getColumnIndex(DataTable_SiteEvent.facility_id);
//        if (i_facility_id < 0)
//            i_facility_id = 0;
//
//        Integer i_datIR_Date = records.getColumnIndex(DataTable_SiteEvent.DateSE_NoSeconds);
//        if (i_datIR_Date < 0)
//            i_datIR_Date = 0;
//
//        Integer i_strD_Col_ID = records.getColumnIndex(DataTable_SiteEvent.strUsername);
//        if (i_strD_Col_ID < 0)
//            i_strD_Col_ID = 0;
//        Integer i_strD_Loc_ID = records.getColumnIndex(DataTable_SiteEvent.strD_Loc_ID);
//        if (i_strD_Loc_ID < 0)
//            i_strD_Loc_ID = 0;
//        Integer i_strFO_StatusID = records.getColumnIndex(DataTable_SiteEvent.strFO_StatusID);
//        if (i_strFO_StatusID < 0)
//            i_strFO_StatusID = 0;
//
//        Integer i_dblIR_Value = records.getColumnIndex(DataTable_SiteEvent.dblIR_Value);
//        if (i_dblIR_Value < 0)
//            i_dblIR_Value = 0;
//        Integer i_strIR_Units = records.getColumnIndex(DataTable_SiteEvent.strIR_Units);
//        if (i_strIR_Units < 0)
//            i_strIR_Units = 0;
//        Integer i_strComment = records.getColumnIndex(DataTable_SiteEvent.strComment);
//        if (i_strComment < 0)
//            i_strComment = 0;
//        Integer i_strEqO_StatusID = records.getColumnIndex(DataTable_SiteEvent.strEqO_StatusID);
//        if (i_strEqO_StatusID < 0)
//            i_strEqO_StatusID = 0;
//
//        Integer i_strDataModComment = records.getColumnIndex(DataTable_SiteEvent.strDataModComment);
//        if (i_strDataModComment < 0)
//            i_strDataModComment = 0;
//
//        Integer i_elev_code = records.getColumnIndex(DataTable_SiteEvent.elev_code);
//        if (i_elev_code < 0)
//            i_elev_code = 0;
//
//        Integer i_count_dup = records.getColumnIndex("count_dup");
//        if (i_count_dup < 0)
//            i_count_dup = 0;
//
//
//        for (records.moveToFirst(); !records.isAfterLast(); records.moveToNext()) {
//            // The Cursor is now set to the right position
//
//
//            s_facility_id = getStringQuotedValue(records, i_facility_id);
//            s_datIR_Date = getStringQuotedValue(records, i_datIR_Date);
//            s_dblIR_Value = getStringQuotedValueFromDouble(records, i_dblIR_Value, null);
//            s_strD_Loc_ID = getStringQuotedValue(records, i_strD_Loc_ID);
//            s_strEqO_StatusID = getStringQuotedValue(records, i_strEqO_StatusID);
//            s_strComment = getStringQuotedValueWithNULL(records, i_strComment);
//            s_strDataModComment = getStringQuotedValueWithNULL(records, i_strDataModComment);
//            s_elev_code = getStringQuotedValueWithNULL(records, i_elev_code);
//
//            s_strFO_StatusID = getStringQuotedValueWithNULL(records, i_strFO_StatusID);
//            s_strD_Col_ID = getStringQuotedValue(records, i_strD_Col_ID);
//            s_strIR_Units = getStringQuotedValueWithNULL(records, i_strIR_Units);
//
//            Integer iDup = records.getInt(i_count_dup);
//
//            String sqlUpdate = " UPDATE " + HandHeld_SQLiteOpenHelper.INST_READINGS
//                    + " SET " + DataTable_SiteEvent.recordToUpload + " = 0"
//                    + " WHERE " + DataTable_SiteEvent.lngID + " IN ("
//                    + " SELECT lngid from " + HandHeld_SQLiteOpenHelper.INST_READINGS + " where "
//                    + DataTable_SiteEvent.strD_Col_ID + " = " + s_strD_Col_ID + " and "
//                    + DataTable_SiteEvent.datIR_Date_NoSeconds + " = " + s_datIR_Date + " and "
//                    + DataTable_SiteEvent.facility_id + " = " + s_facility_id + " and "
//                    + DataTable_SiteEvent.strD_Loc_ID + " = " + s_strD_Loc_ID + " and ";
//
//            if (s_strFO_StatusID.equalsIgnoreCase("NULL"))
//                sqlUpdate += " " + DataTable_SiteEvent.strFO_StatusID + " IS NULL and";
//            else
//                sqlUpdate += " " + DataTable_SiteEvent.strFO_StatusID + " = " + s_strFO_StatusID + " and ";
//
//            if (s_strEqO_StatusID.equalsIgnoreCase("NULL"))
//                sqlUpdate += " " + DataTable_SiteEvent.strEqO_StatusID + " IS NULL and";
//            else
//                sqlUpdate += " " + DataTable_SiteEvent.strEqO_StatusID + " = " + s_strEqO_StatusID + " and ";
//
//            sqlUpdate += " " + DataTable_SiteEvent.dblIR_Value + " = " + s_dblIR_Value + " and ";
//
//            if (s_strIR_Units.equalsIgnoreCase("NULL"))
//                sqlUpdate += " " + DataTable_SiteEvent.strIR_Units + " IS NULL and";
//            else
//                sqlUpdate += " " + DataTable_SiteEvent.strIR_Units + " = " + s_strIR_Units + " and ";
//
//            if (s_strComment.equalsIgnoreCase("NULL"))
//                sqlUpdate += " " + DataTable_SiteEvent.strComment + " IS NULL and";
//            else
//                sqlUpdate += " " + DataTable_SiteEvent.strComment + " = " + s_strComment + " and ";
//
//            if (s_strDataModComment.equalsIgnoreCase("NULL"))
//                sqlUpdate += " " + DataTable_SiteEvent.strDataModComment + " IS NULL and";
//            else
//                sqlUpdate += " " + DataTable_SiteEvent.strDataModComment + " = " + s_strDataModComment + " and ";
//
//
//            if (s_elev_code.equalsIgnoreCase("NULL"))
//                sqlUpdate += " " + DataTable_SiteEvent.elev_code + " IS NULL and";
//            else
//
//                sqlUpdate += " " + DataTable_SiteEvent.elev_code + " = " + s_elev_code;
//
//            sqlUpdate += "  order by  " + DataTable_SiteEvent.lngID + " desc LIMIT " + String.valueOf(iDup - 1) + " );";
//            nRecordsUpdated = nRecordsUpdated + (iDup - 1);
//            returnMessage.append(records.getString(i_strD_Loc_ID)).append(" - ").append(records.getString(i_datIR_Date)).append(", ");
//
//            try {
//                db.execSQL(sqlUpdate);
//
//            } catch (Exception exception) {
//                exception.printStackTrace();
//                System.out.println(exception);
//            }
//        }
//
//         */
//        returnMessage = new StringBuilder(replaceLast(returnMessage.toString(), ",", "."));
//        returnMessage.append(" Total ").append(nRecordsUpdated.toString()).append(" records marked.");
        return returnMessage.toString();
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }


    public String CreateFileToUpload(SQLiteDatabase db, File directoryApp, Integer[] nRecords, Context context) throws ParseException {
        File newCSV = null;

        Calendar c = Calendar.getInstance();
        String[] login = getLoginInfo(db);
        if (login !=null && login.length >0) {
            this.updateUserNameInSiteEvents(db, login[0]);
        }
        try {
            CallSoapWS ws = new CallSoapWS(directoryApp);
            String datetimeserver = ws.WS_GetServerDate(false);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            c.setTime(sdf.parse(datetimeserver));
        } catch (Exception ex) {
            c = Calendar.getInstance();
        }

// in java in Calendar Months are indexed from 0 not 1 so 10 is November and 11 will be December.
        int y = c.get(Calendar.YEAR);
        String sy = Integer.toString(y).substring(2);
        java.text.SimpleDateFormat sfMonth = new java.text.SimpleDateFormat("MM");
        int m = c.get(Calendar.MONTH);
        String sm = sfMonth.format(c.getTime());
        //String sm = Integer.toString(m);
        int d = c.get(Calendar.DAY_OF_MONTH);
        String sd = Integer.toString(d);
        int h = c.get(Calendar.HOUR_OF_DAY);
        String sh = Integer.toString(h);
        int mm = c.get(Calendar.MINUTE);
        String smm = Integer.toString(mm);
        int sec = c.get(Calendar.SECOND);
        String ssec = Integer.toString(sec);
        if (d < 10)
            sd = "0" + sd;
        if (Integer.parseInt(sm) < 10 && !sm.startsWith("0"))
            sm = "0" + sm;
        if (h < 10)
            sh = "0" + sh;
        if (mm < 10)
            smm = "0" + smm;
        if (sec < 10)
            ssec = "0" + ssec;

        String dattime_addon = sy + sm + sd + "_" + sh + smm + ssec;
        String filename =
                HandHeld_SQLiteOpenHelper.FILEPREFIX + "" + dattime_addon + ".csv";
        newCSV = new File(directoryApp + "/" + filename);
        FileOutputStream fos;
        String fullfilename = newCSV.getAbsolutePath();
        try {
            fos = new FileOutputStream(fullfilename);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);

            //update IR table with device name and or no seconds if needed
            String message1 = MarkNotToUploadCompleteDuplicates(db);
            if (message1 != "")
                Toast.makeText(context, message1, Toast.LENGTH_SHORT).show();
            //update IR table with device name and or no seconds if needed
            Update_SETable_IfNeeded(db);

            Cursor records = this.getSiteEventRecords(db);
            nRecords[0] = records.getCount();
            Integer nCol = records.getColumnCount();
            message1 = nRecords[0].toString()+ "  records getting ready to upload";
            Toast.makeText(context, message1, Toast.LENGTH_SHORT).show();

            String s_strD_Loc_ID;
            String s_strUserName;
            String s_datSE_Date;
            String s_datSE_Time;
            String s_strS_Loc_ID;
            String s_strSE_ID;
            String s_strTOFO_id;
            String s_strEqID;
            String s_strEqDesc;
            String s_strComment;
            String s_strM_Per_ID;
            String s_datResDate;
            String s_ynResolved;
            String s_device_name = "";
            Integer iMeasurementType=-1;
            MeasurementTypes.MEASUREMENT_TYPES type = MeasurementTypes.MEASUREMENT_TYPES.OTHER;
            Integer iResolved;

//#strd_loc_id
// #strusername
// #datse_date
// #datse_time
// #strs_loc_id
// #strse_id
// #strtofo_id
// #streqid
// #streqdesc
// #strcomment
// #strm_per_id
// #datresdate
// #ynresolved

            Integer i_strD_Loc_ID = records.getColumnIndex(DataTable_SiteEvent.strD_Loc_ID);
            if (i_strD_Loc_ID < 0)
                i_strD_Loc_ID = 0;
            Integer i_strUserName = records.getColumnIndex(DataTable_SiteEvent.strUserUploadName);
            if (i_strUserName < 0)
                i_strUserName = 0;
            Integer i_datSE_Date = records.getColumnIndex(DataTable_SiteEvent.datSE_Date);
            if (i_datSE_Date < 0)
                i_datSE_Date = 0;
            Integer i_datSE_Time = records.getColumnIndex(DataTable_SiteEvent.datSE_Time);
            if (i_datSE_Time < 0)
                i_datSE_Time = 0;
            Integer i_strS_Loc_ID = records.getColumnIndex(DataTable_SiteEvent.strS_Loc_ID);
            if (i_strS_Loc_ID < 0)
                i_strS_Loc_ID = 0;
            Integer i_strSE_ID = records.getColumnIndex(DataTable_SiteEvent.strSE_ID);
            if (i_strSE_ID < 0)
                i_strSE_ID = 0;
            Integer i_strTOFO_id = records.getColumnIndex(DataTable_SiteEvent.strTOFO_id);
            if (i_strTOFO_id < 0)
                i_strTOFO_id = 0;
            Integer i_strEqID = records.getColumnIndex(DataTable_SiteEvent.strEq_ID);
            if (i_strEqID < 0)
                i_strEqID = 0;
            Integer i_strEqDesc = records.getColumnIndex(DataTable_SiteEvent.strEqDesc);
            if (i_strEqDesc < 0)
                i_strEqDesc = 0;

            Integer i_strComment = records.getColumnIndex(DataTable_SiteEvent.strComment);
            if (i_strComment < 0)
                i_strComment = 0;
            Integer i_strM_Per_ID = records.getColumnIndex(DataTable_SiteEvent.strM_Per_ID);
            if (i_strM_Per_ID < 0)
                i_strM_Per_ID = 0;
            Integer i_datResDate = records.getColumnIndex(DataTable_SiteEvent.datResDate);
            if (i_datResDate < 0)
                i_datResDate = 0;
            Integer i_ynResolved = records.getColumnIndex(DataTable_SiteEvent.ynResolved);
            if (i_ynResolved < 0)
                i_ynResolved = 0;

            int i_device_name = records.getColumnIndex(DataTable_SiteEvent.device_name);
            if (i_device_name < 0)
                i_device_name = 0;

            //some difference because of the type
            int i_meas = records.getColumnIndex(DataTable_SiteEvent.Measurement_Type);
            if (i_meas < 0)
                i_meas = 0;

            String header = DataTable_SiteEvent.CSVHeader();


            myOutWriter.write(header);
            myOutWriter.write(10);//decimal value 10 represents newline in ASCII

            for (records.moveToFirst(); !records.isAfterLast(); records.moveToNext()) {
                // The Cursor is now set to the right position
                String row = "";
                s_strD_Loc_ID = getStringQuotedValue(records, i_strD_Loc_ID);


                s_strUserName = getStringQuotedValue(records, i_strUserName);
                s_datSE_Date = getStringQuotedValue(records, i_datSE_Date);
                s_datSE_Time = getStringQuotedValue(records, i_datSE_Time);
                s_strS_Loc_ID = getStringQuotedValue(records, i_strS_Loc_ID);
                s_strSE_ID = getStringQuotedValue(records, i_strSE_ID);
                s_strTOFO_id = getStringQuotedValue(records, i_strTOFO_id);
                s_strEqID = getStringQuotedValue(records, i_strEqID);
                s_strEqDesc = getStringQuotedValue(records, i_strEqDesc);
                s_strComment = getStringQuotedValue(records, i_strComment);
                s_strM_Per_ID = getStringQuotedValue(records, i_strM_Per_ID);
                s_datResDate = getStringQuotedValue(records, i_datResDate);
                s_ynResolved = getStringQuotedValue(records, i_ynResolved);
                iResolved = records.getInt(i_ynResolved);
                s_device_name = getStringQuotedValue(records, i_device_name);
                iMeasurementType = records.getInt(i_meas);
                type = MeasurementTypes.GetTypeFromNumber(iMeasurementType);

                row = s_strD_Loc_ID + "," +
                        s_strUserName + "," +
                        s_datSE_Date + "," +
                        s_datSE_Time + "," +
                        s_strS_Loc_ID + "," +
                        s_strSE_ID + "," +
                        s_strTOFO_id + "," +
                        s_strEqID + "," +
                        s_strEqDesc + "," +
                        s_strComment + "," +
                        s_strM_Per_ID + ",";

                if ((type == MeasurementTypes.MEASUREMENT_TYPES.PH  &&
                        (s_ynResolved.equalsIgnoreCase("false") || iResolved == 0))
                    || (type == MeasurementTypes.MEASUREMENT_TYPES.GENERAL_BARCODE))
                    row += ",";
                else
                    row += s_datResDate + ",";

                row += s_ynResolved ;//+ "," +
                      //  s_device_name;

                System.out.println(row);

                myOutWriter.write(row);
                myOutWriter.write(10);//decimal value 10 represents newline in ASCII
            }
            myOutWriter.close();
            fos.flush();
            fos.close();

        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception);

            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception);
            return "";
        }
        return fullfilename;

    }

    private String getStringQuotedValueFromDouble(Cursor records, Integer i, DecimalFormat df) {
        String e = "\"";
        String s = "";
        if (df == null)
            df = new DecimalFormat("#.################");
        if (records.getString(i) != null && df == null)
            s = (String) records.getString(i);
        if (records.getString(i) != null && df != null) {
            double dValue = records.getDouble(i);
            s = df.format(dValue);
        }
        return e + s.trim() + e;
    }

    private String getStringQuotedValueAndRemoveSecondsFromDatetime(Cursor records, Integer i) {
        String e = "\"";
        String s = "";
        Date dt = null;
        String timeStamp = "";
        SimpleDateFormat sdf = null;
        try {
            if (records.getString(i) != null) {
                s = (String) records.getString(i);
                sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
                dt = sdf.parse(s);
                timeStamp = new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(dt);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return e + timeStamp.trim() + e;
    }

    private String getStringQuotedValue(Cursor records, Integer i) {
        String e = "\"";
        String s = "";
        if (records.getString(i) != null)
            s = (String) records.getString(i);
        return e + s.trim() + e;
    }


    private String getStringQuotedValue(String s) {
        String e = "\"";
        return e + s.trim() + e;
    }

    private String getStringQuotedValueWithNULL(Cursor records, Integer i) {
        String e = "\"";
        String s = "";
        if (records.getString(i) != null)
            s = (String) records.getString(i);
        else
            return "NULL";
        return e + s.trim() + e;
    }

    private String getStringQuotedValueFromBooleanYesNo(Cursor records, Integer i) {
        String e = "\"";
        String s;
        Integer i1 = records.getInt(i);
        if (records.getInt(i) == 1)
            s = "Yes";
        else
            s = "No";

        //e + ((Integer) records.getInt(i_fSuspect) == 1 ? "Yes" : "No") + e;
        return e + s + e;
    }

    public String sqlCheckColumnExists(String tablename, String columnname) {
        String sql = "SELECT COUNT(*) AS CNTREC FROM pragma_table_info('" + tablename + "') WHERE name='" + columnname + "'";
        return sql;
    }

    public boolean checkColumnExists(SQLiteDatabase db, String tablename, String columnname) {
        boolean rv = false;
        String sql = sqlCheckColumnExists(tablename, columnname);
        Cursor c = db.rawQuery(sql, null);
        try {
            c.moveToFirst();
            if (!c.isNull(0)) {
                int i = c.getInt(0);
                if (i > 0)
                    rv = true;
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return rv;
    }

    public String addNewColumnIfNotExists() {
        return "";
    }

    /** Returns the consumer friendly device name */

    static String getDeviceName1() {
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method getMethod = systemPropertiesClass.getMethod("get", String.class);
            Object object = new Object();
            Object obj = getMethod.invoke(object, "ro.product.device");
            return (obj == null ? "" : (String) obj);
        } catch (Exception e) {
            e.printStackTrace();
            return "NA";
        }
    }


    @SuppressLint("MissingPermission")
    public static String getDeviceName2() {
        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBluetoothAdapter==null) {
                System.out.println("----Device does not support bluetooth---");
                return "NA";
            }
            return mBluetoothAdapter.getName();
        } catch (Exception e) {
            e.printStackTrace();
            return "NA";
        }
    }


    public static String getDeviceName() {
        String device = "NA";
        device = getDeviceName2();

        if (Objects.equals(device, "NA"))
            device = getDeviceName1();

        try {
            if (Objects.equals(device, "NA"))
                device = Build.DEVICE;
        } catch (Exception ex) {
            System.out.println(ex);
        }

        String hardware = "hardware NA";
        try {
            hardware = Build.HARDWARE;
        } catch (Exception ex) {
            System.out.println(ex);
        }
        String id = "ID NA";
        try {
            id = Build.ID;
        } catch (Exception ex) {
            System.out.println(ex);
        }

        String manufacturer = "MANUFACTURER NA";
        try {
            manufacturer = Build.MANUFACTURER;
        } catch (Exception ex) {
            System.out.println(ex);
        }

        String model = "MODEL NA";
        try {
            model = Build.MODEL;
        } catch (Exception ex) {
            System.out.println(ex);
        }

        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        String rv = device + " - " + id + " " + hardware + "' "
                + capitalize(manufacturer) + " " + model;

        String versionName = BuildConfig.VERSION_NAME;
        rv = rv + " Version " + versionName;
        return rv.replace("'", "_");
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }
}

  


