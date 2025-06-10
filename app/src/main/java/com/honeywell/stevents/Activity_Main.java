package com.honeywell.stevents;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//import java.util.concurrent;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.InvalidScannerNameException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;

public class Activity_Main extends AppCompatActivity  implements BarcodeReader.BarcodeListener,
        BarcodeReader.TriggerListener {


    private static final int WRITE_REQUEST_CODE =1 ;
    private ListView barcodeList;
    private static BarcodeReader barcodeReader;
    private static final int REQUEST_CODE_GETMESSAGE =1014 ;
    ArrayList<String[]> array_Eq = null;
    private AidcManager manager;
    Context ct = this;
    private boolean bAcceptWarningDuplicate = false;


    @SuppressLint("StaticFieldLeak")
    private static Activity_Main instance;

    public static Activity_Main getInstance() {
        return instance;
    }


    public HandHeld_SQLiteOpenHelper dbHelper;
    public SQLiteDatabase db;
    private Button btnInputForms;
    private Button btnReviewForms;
    private Button btnUploadDataToServer;
    private TextView  txtInfo;
    private TextView txtAppInfo;
    private ProgressBar progressBar;

    Context context;

    private SiteEvents default_reading;
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;

    private File directoryApp;
Cursor Cursor_Eq = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_se_main);

        directoryApp = getFilesDir();
        context = this;
        default_reading = SiteEvents.GetDefaultReading();
        AppDataTables tables = new AppDataTables();
        tables.SetSiteEventsTablesStructure();

        dbHelper = new HandHeld_SQLiteOpenHelper(context, tables);
        db = dbHelper.getReadableDatabase();
        Cursor_Eq = dbHelper.GetCursorEquipment(db);
        array_Eq = transferCursorToArrayList(Cursor_Eq);

        // set lock the orientation
        // otherwise, the onDestory will trigger
        // when orientation changes
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // create the AidcManager providing a Context and a
        // CreatedCallback implementation.
        AidcManager.create(this, new CreatedCallback() {

            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                try {
                    barcodeReader = manager.createBarcodeReader();
                } catch (
                        InvalidScannerNameException e) {
                    Toast.makeText(context, "Invalid Scanner Name Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ActivitySetting();
        db.close();
        //barcodeReader = this.getBarcodeObject();

        if (barcodeReader != null) {

            // register bar code event listener
            barcodeReader.addBarcodeListener(this);
            Log.i("onCreate", "=====barcodeReader !=null");

            // set the trigger mode to client control
            try {
                barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                        BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);
            } catch (UnsupportedPropertyException e) {
                Toast.makeText(this, "Failed to apply properties", Toast.LENGTH_SHORT).show();
            }
            // register trigger state change listener
            barcodeReader.addTriggerListener(this);

            Map<String, Object> properties = new HashMap<>();
            // Set Symbologies On/Off
            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
            //properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
            properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, true);
            // Set Max Code 39 barcode length
            properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
            // Turn on center decoding
            properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
            // Disable bad read response, handle in onFailureEvent
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, false);
            // Apply the settings
            barcodeReader.setProperties(properties);
        }


        // get initial list
       // barcodeList = (ListView) findViewById(R.id.listViewBarcodeData);
        //Log.i("barcodeList", barcodeList.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_CheckScanner) {
            Intent barcodeIntent = new Intent("android.intent.action.CLIENTBARCODEACTIVITY");
            startActivity(barcodeIntent);
            return true;
        }

        if (id == R.id.menu_LoginInfo) {
            Intent barcodeIntent = new Intent("android.intent.action.SELOGINACTIVITY");
            startActivity(barcodeIntent);
            return true;
        }

        // a potentially time consuming task
        if (id == R.id.menu_DownloadData) {
            progressBar.setVisibility(View.VISIBLE);
            new ParseXMLAndUploadToDBThread(this, true);
            return true;
        }

        if (id == R.id.menu_workUploadDB) {
            //put in the thread since the file is big and without was the
            // Input dispatching timed out ANR exception
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Date currentDateTime = Calendar.getInstance().getTime();
                    File pathToDB = new File(directoryApp.getParentFile() + "//databases");
                    ArrayList<File> sqlFiles = getListFiles(pathToDB, "sqlite3");

                    CallSoapWS ws1 = new CallSoapWS(null);
                    String response = ws1.CheckConnection();
                    boolean bConnection = true;
                    if (response.startsWith("ERROR")) {
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                        AlertDialogShow(response, "Error", "OK", "error");
                        txtInfo.setText(response);
                        bConnection = false;
                    }

                    //adding seconds April 2023. KS
                    //default sqlllite format YYYY-MM-DD HH:MM:SS
                    String timeStamp = new SimpleDateFormat(DataTable_SiteEvent.Datetime_pattern_with_sec).format(currentDateTime);
                    timeStamp = timeStamp.replace(" ", "");
                    timeStamp = timeStamp.replace("/", "");
                    timeStamp = timeStamp.replace(":", "");
                    timeStamp = "_" + timeStamp;
                    HandHeld_SQLiteOpenHelper dbHelper =
                            new HandHeld_SQLiteOpenHelper(context, new AppDataTables());
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    if (bConnection) {
                        try {

                            CallSoapWS ws = new CallSoapWS(directoryApp);
                            Path pathtodb1 = Paths.get(sqlFiles.get(0).getPath());

                            byte[] dataUpload = Files.readAllBytes(pathtodb1);
                            String[] credentials = dbHelper.getLoginInfo(db);

                            String name = credentials[0];
                            String encryptedPassword = credentials[1];
                            // For decryption not ise null or empty string
                            if (encryptedPassword == null || encryptedPassword.equals(""))
                                encryptedPassword = "NA";
                            String pwd = Application_Encrypt.decrypt(encryptedPassword);
                            String[] errormessage = new String[]{""};

                            Boolean bCanUpload = ws.WS_GetLogin(name, pwd, errormessage);
                            Boolean bUploaded;
                            if (bCanUpload) {
                                String filename = sqlFiles.get(0).getName();
                                String extension = "sqlite3";
                                String filenamenoext = filename;
                                // Extract the extension from the file name
                                int index = filename.lastIndexOf('.');

                                if (index > 0) {
                                    extension = filename.substring(index + 1);
                                    filenamenoext = filename.substring(0, index);
                                }
                                filename = filenamenoext + timeStamp + '.' + extension;

                                bUploaded = ws.WS_UploadFile2(dataUpload, filename, name, pwd);


                                if (bUploaded) {
                                    AlertDialogShow(" Db Has Been Uploaded to the Server",
                                            "Info", "OK", "default");
                                } else {
                                    AlertDialogShow("db hasn't been uploaded. Try one more time.", "ERROR!", "OK", "warning");
                                }

                            } else {
                                AlertDialogShow("Your Credentials aren't working. Go to Main Page | Menu | Check Login Credentials. : " + errormessage[0], "ERROR!", "OK", "warning");
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            });
        }
        if (id == R.id.menu_workWithSDCard) {

            boolean isSdcard = false;
            int iCopied = 0;
            int iMoved = 0;
            ArrayList<File> csvFiles = getListFiles(directoryApp, "csv");
            File pathToDB = new File(directoryApp.getParentFile() + "//databases");
            ArrayList<File> sqlFiles = getListFiles(pathToDB, "sqlite3");

            File folderStorage = null;
            File sdcard1 =new File("/storage/sdcard1");

            File[] folders_sdcard1 = (new File("/storage/sdcard1")).listFiles();
            // Choose folders_sdcard1
            File sdcard_dtsc =new File("/storage/sdcard1/Documents/DTSC Files");
            if (!sdcard_dtsc.exists())
                sdcard_dtsc.mkdir();
            File[] folders_sdcard_dtsc = (new File("/storage/sdcard1/Documents/DTSC Files")).listFiles();


             if (folders_sdcard_dtsc != null) {
                folderStorage = sdcard_dtsc;
            }
             else if (folders_sdcard1 != null) {
                 folderStorage = folders_sdcard1[0];
             }

            if (folderStorage != null && sqlFiles != null) {
                try {

                    for (int i = 0; i < sqlFiles.size(); i++) {
                        iCopied += copyFile(sqlFiles.get(i).getParentFile().getPath(), sqlFiles.get(i).getName(), folderStorage.getPath());

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(ex.toString());
                }
            }

            if (folderStorage != null && csvFiles != null) {
                try {

                    for (int i = 0; i < csvFiles.size(); i++) {
                        iMoved += moveFile(csvFiles.get(i).getParentFile().getPath(), csvFiles.get(i).getName(), folderStorage.getPath());

                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(ex.toString());
                }
                Toast.makeText(context, String.valueOf(iMoved) + " files have been moved and " +
                        String.valueOf(iCopied) + " files have been copied ", Toast.LENGTH_SHORT).show();
            }
        }


        return super.onOptionsItemSelected(item);
    }
    public ArrayList<String[]> transferCursorToArrayList(Cursor cursor) {
        ArrayList<String[]> arrayList = new ArrayList<String[]>();
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

    private int moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        int rv = 0;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + "/" + inputFile);
            out = new FileOutputStream(outputPath + "/" + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;
            rv = 1;
            // delete the original file
            boolean bdeleted  = new File(inputPath + "/" + inputFile).delete();
            if (!bdeleted)
                rv = 0;
        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
            rv = 0;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
            rv = 0;
        }
        return rv;

    }

    private int copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        int rv = 0;
        try{
            boolean bDeleted  = new File(outputPath + "/" + inputFile).delete();
        }
        catch(Exception ex){
            Log.e("Deleted old sqllite file", ex.getMessage());
        }

        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            File file = new File(outputPath);
            String timeStamp="";
            if(file.exists()){
                //add datetime
                Date currentDateTime = Calendar.getInstance().getTime();
                //adding seconds April 2023. KS
                //default sqlllite format YYYY-MM-DD HH:MM:SS
                timeStamp = new SimpleDateFormat(DataTable_SiteEvent.Datetime_pattern_with_sec).format(currentDateTime);
                timeStamp = timeStamp.replace(" ","");
                timeStamp = timeStamp.replace("/","");
                timeStamp = timeStamp.replace(":","");
                timeStamp = "._"+timeStamp;
            }


            in = new FileInputStream(inputPath +"/"+inputFile);

            out = new FileOutputStream(outputPath + "/" + inputFile+timeStamp);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;
            rv = 1;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("Exception", fnfe1.getMessage());
            rv = 0;
        }
        catch (Exception e) {
            Log.e("Exception", e.getMessage());
            rv = 0;
        }
        return rv;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Granted.


                } else {
                    //Denied.
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isExternalStorageAvailable() {

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, WRITE_REQUEST_CODE);

        String state = Environment.getExternalStorageState();
        System.out.println(" Environment.getExternalStorageState();" + state);
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        if (mExternalStorageAvailable == true
                && mExternalStorageWriteable == true) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Bundle extras = getIntent().getExtras();
            if (requestCode == REQUEST_CODE_GETMESSAGE  && resultCode  == RESULT_OK && extras != null) {

                default_reading = (SiteEvents) getIntent().getSerializableExtra("GENERAL_BARCODE");
                if (default_reading == null)
                    default_reading = SiteEvents.GetDefaultReading();
               // Toast.makeText(context, default_reading.getStrSE_ID(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private ArrayList<File> getListFiles(File parentDir, String extension) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        if (files != null)
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getListFiles(file, extension));
                } else {
                    if (file.getName().endsWith(extension)) {
                        inFiles.add(file);
                    }
                }
            }
        return inFiles;
    }

    static BarcodeReader getBarcodeObject() {
        return barcodeReader;
    }

    private void SetAndStartIntent(Intent seintent) {
        Log.i("SetAndStartIntent", "SetAndStartIntent - main");
        seintent.putExtra("SE", default_reading);
        seintent.putExtra("SE_TABLE", new DataTable_SiteEvent());
        seintent.putExtra("USER", "NA");
        startActivity(seintent);
    }
    /**
     * Create buttons to launch demo activities.
     */
    public void ActivitySetting() {

        this.txtInfo= findViewById(R.id.txtInfo);
        txtInfo.setText("Additional options available under the menu (three dots) above");

        this.btnInputForms = findViewById(R.id.btnInputSEs);
        btnInputForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("------------onClick SiteEventActivity", "12");
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent("android.intent.action.SE_MAIN_INPUT_BARCODEACTIVITY");
                SetAndStartIntent(barcodeIntent);
                startActivityForResult(barcodeIntent,REQUEST_CODE_GETMESSAGE);

                bAcceptWarningDuplicate = false;

            }
        });

        this.btnReviewForms = findViewById(R.id.btnEditSEs);
        btnReviewForms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bAcceptWarningDuplicate = false;
                Log.i("------------onClick Activity_EditListSE", "12");
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent("android.intent.action.EDITLISTACTIVITY");
                startActivity(barcodeIntent);
                System.out.println("In MAIN btnReviewForms.setOnClickListener " + default_reading.getStrEq_ID());
            }
        });


        this.btnUploadDataToServer = findViewById(R.id.btnUploadSEs);
        btnUploadDataToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CHECK CONNECTION
                Validation isDuplicate = new Validation();
                HandHeld_SQLiteOpenHelper dbHelper =
                        new HandHeld_SQLiteOpenHelper(context, new AppDataTables());
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                String messageDup = dbHelper.PotentialDuplicatesMesssage(db);
                System.out.println("Possible Duplicates Message " + messageDup + "bAcceptWarningDuplicate " + Boolean.toString(bAcceptWarningDuplicate));

                if (messageDup != "" && !bAcceptWarningDuplicate) {
                    isDuplicate.setValidation(Validation.VALIDATION.WARNING_DUPLICATE);
                    messageDup = "Please check:\n" + messageDup + "\nPress 'Upload Data' one more time to confirm the data as VALID or go the Edit Readings Screen.";
                    isDuplicate.setValidationMessageWarning(messageDup);
                    AlertDialogShow(messageDup, "Warning", "OK", "warning");
                    bAcceptWarningDuplicate = true;
                    db.close();
                } else {
                    CallSoapWS ws1 = new CallSoapWS(null);
                    String response = ws1.CheckConnection();
                    boolean bConnection = true;
                    if (response.startsWith("ERROR")) {
                        Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                        AlertDialogShow(response, "Error", "OK", "error");
                        txtInfo.setText(response);
                        bConnection = false;
                    }

                    if (bConnection) {

                        Integer[] nrecords = new Integer[]{0};

                        String message = "The data (" + nrecords[0] + " records) is ready to be uplaoded to the server.";

                        String s = null;

                        try {
                            s = dbHelper.CreateFileToUpload(db, directoryApp, nrecords, context);

                        } catch (ParseException e) {
                            e.printStackTrace();
                            nrecords[0] = 0;
                        }
                        if (nrecords[0] > 0) {

                            try {
                                Path path = Paths.get(s);
                                CallSoapWS ws = new CallSoapWS(directoryApp);
                                byte[] dataUpload = Files.readAllBytes(path);
                                String[] credentials = dbHelper.getLoginInfo(db);

                                String name = credentials[0];
                                String encryptedPassword = credentials[1];
                                // For decryption not ise null or empty string
                                if (encryptedPassword == null || encryptedPassword == "")
                                    encryptedPassword = "NA";
                                String pwd = Application_Encrypt.decrypt(encryptedPassword);
                                String[] errormessage = new String[]{""};

                                Boolean bCanUpload = ws.WS_GetLogin(name, pwd, errormessage);
                                Boolean bUploaded;
                                if (bCanUpload) {
//                                    bUploaded = ws.WS_UploadFile2(dataUpload, s, name, pwd);
//                                    if (!bUploaded)
                                    bUploaded=  ws.WS_UploadFile2(dataUpload, path.getFileName().toString(), name, pwd);
                                    if (bUploaded) {
                                        db.execSQL(DataTable_SiteEvent.UpdateUploadedData());
                                        AlertDialogShow(nrecords[0] + " Records Has Been Uploaded to the Server",
                                                "Info", "OK", "default");
                                    } else {
                                        AlertDialogShow("Data hasn't been uploaded. Try one more time.", "ERROR!", "OK", "warning");
                                    }

                                } else {
                                    AlertDialogShow("Your Credentials aren't working. Go to Main Page | Menu | Check Login Credentials. : " + errormessage[0], "ERROR!", "OK", "warning");
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                        db.close();
                    }
                }
            }
        });

        txtAppInfo = findViewById(R.id.txtAppInfo);
        txtAppInfo.setText("Version :" + versionName);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

    }

    private void AlertDialogShow(String message, String title, String button,  String theme) {
        int themeResId = R.style.AlertDialogTheme;
        try {
            if (theme.toLowerCase().equals("warning")) {
                themeResId = R.style.AlertDialogWarning;
            }
            if (theme.toLowerCase().equals("error")) {
                themeResId = R.style.AlertDialogError;
            }

            AlertDialog ad = new AlertDialog.Builder(this,themeResId)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
            ad.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            try {
                wait(10);
            } catch (Exception ignored) {
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            finish();
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (barcodeReader != null) {
            // close BarcodeReader to clean up resources.
            barcodeReader.close();
            barcodeReader = null;
        }

        if (manager != null) {
            // close AidcManager to disconnect from the scanner service.
            // once closed, the object can no longer be used.
            manager.close();
        }
    }

    public String GetSpinnerValue(Spinner spinner) {
        TextView textView = (TextView)spinner.getSelectedView();
        String text = textView.getText().toString();

        return text;

    }
    public void SetSpinnerValue(Spinner spinner, ArrayList<String[]> strValues, String strValue) {
        int index = GetIndexFromArraylist(strValues, strValue, 1);
        spinner.setSelection(index);
    }
    public int GetIndexFromArraylist(ArrayList<String[]> list, String myString, Integer column) {

        int n = list.size();


        for (int i = 0; i < n; i++) {
            String[] sValues = list.get(i);
            String sValue = sValues[column];
            String sId = sValues[0];

            if (sValue.equalsIgnoreCase(myString)) {
                return i;//Integer.valueOf(sId);
            }
        }

        return 0;
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String current_equipment = "";
                // update UI to reflect the data
                Log.i("onBarcodeEvent", "onBarcodeEvent!!!!");
                List<String> list = new ArrayList<>();
                SiteEvents se = new SiteEvents();
                DataTable_SiteEvent table = new DataTable_SiteEvent();
                String tempcurrent = event.getBarcodeData();
                Log.i("onBarcodeEvent", "onBarcodeEvent!!!! +" + tempcurrent);

                int id = GetIndexFromArraylist(array_Eq, current_equipment, 1);
                String current_equipment_type = ((String[]) array_Eq.get(id))[2];
                if (tempcurrent != null || tempcurrent != "")
                    se.setStrEq_ID(tempcurrent);

                Log.i("onBarcodeEvent", "onBarcodeEvent :" + current_equipment);

              if (id > 0) {
                  MeasurementTypes.MEASUREMENT_TYPES type = MeasurementTypes.GetFrom_SE_ID(current_equipment, current_equipment_type);
                  Intent seintent = null;
                  if (type == MeasurementTypes.MEASUREMENT_TYPES.GENERAL_BARCODE) {
                    seintent = new Intent("android.intent.action.INPUT_GENERAL_EQ_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
                  }
                  if (type == MeasurementTypes.MEASUREMENT_TYPES.PH) {
                      seintent = new Intent("android.intent.action.INPUT_PH_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
                  }
                  if (type == MeasurementTypes.MEASUREMENT_TYPES.NOISE) {
                      seintent = new Intent("android.intent.action.INPUT_NOISE_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
                  }
                  if (type == MeasurementTypes.MEASUREMENT_TYPES.VOC) {
                      Log.i("isLastRecordSavedToTable", " go to ->voc");

                      seintent = new Intent("android.intent.action.INPUT_VOC_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
                  }
                  if (type == MeasurementTypes.MEASUREMENT_TYPES.OTHER) {
                      seintent = new Intent("android.intent.action.SE_MAIN_INPUT_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
                  }
                  if (seintent != null)
                      SetAndStartIntent(seintent);
              }
            }
        });
    }


    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {

    }

    @Override
    public void onTriggerEvent(TriggerStateChangeEvent triggerStateChangeEvent) {

    }
}
