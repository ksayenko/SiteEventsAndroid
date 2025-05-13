    package com.honeywell.stevents;

    import android.app.Activity;
    import android.app.AlertDialog;
    import android.content.Context;
    import android.database.sqlite.SQLiteDatabase;
    import android.os.AsyncTask;
    import android.os.Handler;
    import android.util.Log;
    import android.widget.TextView;

    import java.io.File;

    //---------------
    //NOT USING ANY MORE AFTER MOVE TO ANDROID 11 AND SDK 30 !!
    //---------------
    public class ParseXMLAndUploadToDBAsyncTask extends AsyncTask<String, Integer, Integer> {
        Context context;
        public Handler mHandler;
        TextView txtInfo;
        public Activity activity;

        public ParseXMLAndUploadToDBAsyncTask(Activity _activity) {

            activity = _activity;
            context = activity;
            directoryApp = context.getFilesDir();
            txtInfo =    (TextView) activity. findViewById(R.id.txtInfo);
            txtInfo.setText("In the ParseXMLAndUploadToDBAsyncTask");
            onPostExecute(1);
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
            dbHelper.getInsertFromTables(db);
        }

        @Override
        protected Integer doInBackground(String... strings) {
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

                    publishProgress(1);
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.EQUIP_IDENT + ".xml"));
                    publishProgress(2);
                    tables.AddStdetDataTable(f.ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.DATA_SITE_EVENT_DEF + ".xml"));

                } catch (Exception exception) {
                    exception.printStackTrace();
                    System.out.println(exception.toString());
                    return null;
                }
                dbHelper = new HandHeld_SQLiteOpenHelper(context, tables);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                dbHelper.getInsertFromTables(db);

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

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            txtInfo.setText(" Table #  "+ progress[0].toString() + " Uploaded");
            Log.i("------------onProgressUpdate", progress[0].toString());

        }

        protected void onPostExecute() {
            super.onPostExecute(1);
            txtInfo.setText(" Done");
        }

        protected void onPostExecute(Integer... result) {
            super.onPostExecute(result[0]);
            txtInfo.setText(" Done");
            Log.i("------------onPostExecute", String.valueOf(result[0]));
            final AlertDialog ad = new AlertDialog.Builder(context).create();
            if (result[0] < 0) {
                ad.setTitle("Error!");
                ad.setMessage("The data has been uploaded with errors");
            } else {
                ad.setTitle("Sucsess!");
                ad.setMessage("The data has been uploaded correctly");
            }
            ad.show();


        }


    }




