package com.honeywell.stevents;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.File;


public class ParseXMLAndUploadToDBThread{
    Context context;
    public Handler mHandler;
    TextView txtInfo;
    ProgressBar progressBar;
    public AppCompatActivity activity;
    boolean bDownloadFromWS;
    private Button btnInputForms;
    private Button btnReviewForms;
    private Button btnUploadDataToServer;

    public ParseXMLAndUploadToDBThread(AppCompatActivity _activity, boolean _bDownloadFromWS) {

        activity = _activity;
        context = activity;
        directoryApp = context.getFilesDir();
        txtInfo =    (TextView) activity. findViewById(R.id.txtInfo);
        progressBar = (ProgressBar) activity. findViewById(R.id.progressBar);
        btnInputForms = (Button) activity.findViewById(R.id.btnInputSEs);
        btnUploadDataToServer = (Button) activity.findViewById(R.id.btnUploadSEs);
        btnReviewForms = (Button) activity.findViewById(R.id.btnEditSEs);
        txtInfo.setText(" Start");
        bDownloadFromWS = _bDownloadFromWS;

        populateDB();

    }

    private File directoryApp;

    public File GetDirectory() {
        return directoryApp;
    }

    public static String rslt = "";
    /**
     * Called when the activity is first created.
     */
    public HandHeld_SQLiteOpenHelper dbHelper;


    private void populateDB() {

        btnInputForms.setEnabled(false);
        btnUploadDataToServer.setEnabled(false);
        btnReviewForms.setEnabled(false);

        ExecutorService executor = Executors.newFixedThreadPool(5);//.newSingleThreadScheduledExecutor();
        progressBar.setVisibility(View.VISIBLE);
        final Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {

            int count = 0;

            @Override
            public void run() {

                //Background work here
                try {
                    System.out.println("KS :: Start run()");
                    doInBackground();
                    System.out.println("KS :: After  doInBackground();");
                    // onPostExecute(11);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //UI Thread work here
                            onPostExecute(11);
                        }

                        private void onPostExecute(Integer result) {
                            System.out.println("KS :: INSIDE THIS ONPOSTEXECUTE");
                            btnInputForms.setEnabled(true);
                            btnUploadDataToServer.setEnabled(true);
                            btnReviewForms.setEnabled(true);
                        }
                    }, 6000);
                } catch (Exception e) {
                    System.out.println("KS :: Error FROM THE APP 1 : " + e.fillInStackTrace());

                }
            }
        });

        // Display message only for better readability
        System.out.println("KS :: Done");
        progressBar.setVisibility(View.INVISIBLE);
    }


    private void onPostExecute(int result) {
        System.out.println("KS :: INSIDE THAT ONPOSTEXECUTE");
        btnInputForms.setEnabled(true);
        btnUploadDataToServer.setEnabled(true);

        //txtInfo.setText(" Done");
        Log.i("------------onPostExecute", String.valueOf(result));

        final AlertDialog ad = new AlertDialog.Builder(context).create();
        ad.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        if (result < 0) {
            AlertDialogShow("The data has been uploaded with errors",
                    "Error", "OK", "warning");
        } else {
            AlertDialogShow("The data has been uploaded with errors",
                    "Success", "OK","warning");

        }


    }


    private void AlertDialogShow(String message, String title, String button, String theme) {
        int themeResId =R.style.AlertDialogTheme;
        if (theme.toLowerCase().equals("warning"))
        {
            themeResId = R.style.AlertDialogWarning;
        }
        if (theme.toLowerCase().equals("error"))
        {
            themeResId = R.style.AlertDialogError;
        }
        AlertDialog ad = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
        ad.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
    }



    private int doInBackground() {

        if (!directoryApp.exists())
            directoryApp.mkdir();
        boolean bConnection = true;

        System.out.println("KS:: Starting doInBackground : DOWNLOADING DATE FROM WEBSERVICES");
        StdetFiles f = new StdetFiles(directoryApp);
        System.out.println("KS:: Starting new StdetFiles(directoryApp);");
        AppDataTables tables;// = f.ReadXMLToSTDETables();


        //final AlertDialog ad = new AlertDialog.Builder(context).create();
        try {
            System.out.println("KS:: doInBackground");
            String resp = "LookUp Tables Loadeding";

            if (bDownloadFromWS) {
                //CHECK CONNECTION
                CallSoapWS ws1 = new CallSoapWS(null);
                String response = ws1.CheckConnection();
                bConnection= true;

                if (response.startsWith("ERROR")) {
                    //can't toast have an exception: java.lang.NullPointerException: Can't toast on a thread that has not called Looper.prepare()
                    //Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                    txtInfo.setText(response);
                    bConnection = false;
                    //pause
                    Executor handler = ContextCompat.getMainExecutor(activity);
                    handler.execute(new Runnable() {
                        public void run() {
                            // yourMethod();
                        }
                    });
                    //btnInputForms.setEnabled(true);
                    //btnUploadDataToServer.setEnabled(true);
                }
                if (bConnection) {
                    CallSoapWS cs = new CallSoapWS(directoryApp);
                    try {
                        publishProgressTextView(" Starting bringing data from the webservice");
                        publishProgressBar(1);
                        resp = cs.WS_GetServerDate(true);
                        tables = cs.WS_GetALLDatasets();
                        publishProgressTextView(resp);
                    } catch (Exception ex) {
                        publishProgressTextView(ex.toString());
                        publishProgressBar(1);
                    }
                }
            }
            dbHelper = new HandHeld_SQLiteOpenHelper(context, new AppDataTables());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            tables = new AppDataTables();//f.ReadXMLToSTDETables();
            boolean bTableRead = true;
            try {

                tables.AddStdetDataTable(new DataTable_SiteEvent());
                AppDataTable dtUsers =f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.USERS + ".xml");
                if(dtUsers!=null) {
                    tables.AddStdetDataTable(dtUsers);
                    publishProgressTextView(" Table  " + HandHeld_SQLiteOpenHelper.USERS + " is reading to memory ");
                }
                else
                {
                    publishProgressTextView("!!! Table  " + HandHeld_SQLiteOpenHelper.USERS + " is NOT populating ");
                }
                publishProgressBar(1);

                AppDataTable dtEqIdent =f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.EQUIP_IDENT + ".xml");
                if(dtEqIdent!=null) {
                    tables.AddStdetDataTable(dtEqIdent);
                    publishProgressTextView("   Table  " + HandHeld_SQLiteOpenHelper.EQUIP_IDENT + " is reading to memory ");
                }
                else
                    publishProgressTextView("!!! Table  " + HandHeld_SQLiteOpenHelper.EQUIP_IDENT + " is NOT populating ");

                publishProgressBar(2);

                AppDataTable dtSiteEventDef =f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.DATA_SITE_EVENT_DEF + ".xml");
                if(dtSiteEventDef!=null){
                    tables.AddStdetDataTable(dtSiteEventDef);
                publishProgressTextView("  Table  " + HandHeld_SQLiteOpenHelper.DATA_SITE_EVENT_DEF + " is reading to memory ");
                }
                else
                    publishProgressTextView("!!! Table  " + HandHeld_SQLiteOpenHelper.DATA_SITE_EVENT_DEF + " is NOT populating ");
                publishProgressBar(3);


            } catch (Exception exception) {
                exception.printStackTrace();
                System.out.println("KS :: "+exception.toString());
                return -1;
            }
            publishProgressTextView(" Start Uploading to DB");
            publishProgressBar(11);

            //dbHelper.getInsertFromTables(db);

            int n = tables.getDataTables().size();
            System.out.println("KS :: !!!!!!!In getInsertFromTables : " + String.valueOf(n));

            for (int i = 0; i < n; i++) {


                if (tables != null && tables.getDataTables().get(i).getName() != null) {
                    String tbName = tables.getDataTables().get(i).getName();
                    publishProgressBar(11 + i);
                    publishProgressTextView("Inserting Data for table " + String.valueOf(i + 1) + ": " + tbName);
                    System.out.println("KS ::In getInsertFromTables " + String.valueOf(i) + " " + tbName);
                    if (!tbName.equalsIgnoreCase("NA")
                            && tables.getDataTables().get(i).getTableType() == AppDataTable.TABLE_TYPE.LOOKUP) {
                        dbHelper.getInsertFromTable(db, tables.getDataTables().get(i));

                    }
                }
            }
            publishProgressBar(20);
            publishProgressTextView(" Download and Upload Task Completed");

            //ad.setMessage(resp);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("KS ::"+ex.toString());
            return -1;
        }
        //ad.show();

        return 0;
    }

    private void publishProgressBar(Integer progress) {
        progressBar.setProgress(progress*5);
        Log.i("------------onProgressUpdate", progress.toString());
    }
    private void publishProgressTextView(String progress) {
        txtInfo.setText(progress);
        Log.i("------------onProgressUpdate", progress);
    }
}




