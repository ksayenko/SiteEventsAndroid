package com.honeywell.stevents;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

    public class DownloadAndParseToDBThread extends Activity  {
        Context context;
        //public Handler mHandler;
        TextView txtInfo;
        public Activity activity;

        public DownloadAndParseToDBThread (Activity _activity) {

            activity = _activity;
            context = activity;
            directoryApp = context.getFilesDir();
            txtInfo = (TextView) activity.findViewById(R.id.txtInfo);
            //txtInfo.setText("In the DownloadAndParseToDBThread");
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
            //Looper.prepare();
            StdetFiles f = new StdetFiles(directoryApp);
            //Looper.loop();

            AppDataTables tables = f.ReadXMLToSTDETables();
            dbHelper = new HandHeld_SQLiteOpenHelper(context, tables);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ExecutorService es
                    = Executors.newFixedThreadPool(11);
            // Display message only for better readability
            System.out.println("Starting");

            ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            final Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(new Runnable() {

                int count;

                @Override
                public void run() {
                    //Background work here
                    try {
                        doInBackground();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //UI Thread work here
                                onPostExecute(11);
                            }


                            private void onPostExecute(Integer result) {
                                txtInfo.setText("Done");
                                Log.i("------------onPostExecute", String.valueOf(result));
                                final AlertDialog ad = new AlertDialog.Builder(context).create();
                                if (result < 0) {
                                    ad.setTitle("Error!");
                                    ad.setMessage("The data has been uploaded with errors");
                                } else {
                                    ad.setTitle("Success!");
                                    ad.setMessage("The data has been uploaded correctly");
                                }
                                ad.show();
                            }
                        });
                    } catch (Exception ignored) {

                    }
                }
            });

            // Display message only for better readability
            System.out.println("Done");
            db.close();
        }


        private void onPostExecute(int[] result) {
            txtInfo.setText("Done");
            Log.i("------------onPostExecute", String.valueOf(result[0]));

            final AlertDialog ad = new AlertDialog.Builder(context).create();

            if (result[0] < 0) {
                AlertDialogShow("The data has been uploaded with errors",
                        "Error", "OK","warning");
            } else {
                AlertDialogShow("The data has been uploaded with errors",
                        "Success", "OK", "warning");

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
            AlertDialog ad = new AlertDialog.Builder(context, themeResId)
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
            //final AlertDialog ad = new AlertDialog.Builder(context).create();
            try {
                System.out.println("do in backgroind");
                String resp = "LookUp Tables Loaded";

                //Looper.prepare();
                StdetFiles f = new StdetFiles(directoryApp);
                //Looper.loop();

                AppDataTables tables = new AppDataTables();
                try {

                    if (!directoryApp.exists())
                        directoryApp.mkdir();


                    tables.AddStdetDataTable(new DataTable_SiteEvent());
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.USERS + ".xml"));
                    publishProgress(new Integer[]{1});
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.SITE_EVENT_DEF + ".xml"));                    publishProgress(new Integer[]{2});
                    publishProgress(new Integer[]{2});
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.EQUIP_IDENT + ".xml"));
                    publishProgress(new Integer[]{3});
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.MAINTENANCE + ".xml"));
                    publishProgress(new Integer[]{4});



                } catch (Exception exception) {
                    exception.printStackTrace();
                    System.out.println(exception);
                    return -1;
                }
                dbHelper = new HandHeld_SQLiteOpenHelper(context, tables);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                dbHelper.getInsertFromTables(db);
                db.close();
                //Looper.loop();

                //ad.setMessage(resp);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.toString());
                return -1;
            }
            //ad.show();

            return 0;
        }

        private void publishProgress(Integer[] progress) {
            txtInfo.setText(String.format(" Table #  %s Start Uploading", progress[0].toString()));
            Log.i("------------onProgressUpdate", progress[0].toString());
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_se_main);
            directoryApp =getFilesDir();

            //Button btnDownloadData=(Button)findViewById(R.id.btnDownloadData);
            final  AlertDialog ad=new AlertDialog.Builder(this).create();

            //btnDownloadData.setOnClickListener(new OnClickListener() {
/*
@Override public void onClick(View arg0) {
        // TODO Auto-generated method stub
        */

            //View v = findViewById(R.id.btnDownloadData);
            CallSoapWS cs=new CallSoapWS(directoryApp);


            try
            {
                String resp=cs.WS_GetServerDate(true);
                AppDataTables tables= cs.WS_GetALLDatasets();
                dbHelper =  new HandHeld_SQLiteOpenHelper(context,tables);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                dbHelper.getInsertFromTables(db);
                ad.setMessage(resp);
                db.close();

                // p =  new ParseXMLAndUploadToDBThread((Activity) context);

            }catch(Exception ex)
            {
                ad.setTitle("Error!");
                ad.setMessage(ex.toString());
            }
            ad.show();
        }

    }






