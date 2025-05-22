package com.honeywell.stevents;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.TriggerStateChangeEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class Activity_GeneralEq_Input extends AppCompatActivity implements BarcodeReader.BarcodeListener,
        BarcodeReader.TriggerListener {
    private Context mContext;

    private String current_SEDateTime;
    private String current_ResDateTime;

    private Spinner spin_SE_Code;
    private Spinner spin_Equip_Code;
    private Spinner spin_User_name;

    private TextView text_event_date;
    private TextView text_event_time;
    private TextView text_resolve_date;
    private TextView text_resolve_time;

    private TextView txt_comment;

    private RadioGroup rbResloved;
    private RadioButton rbTrue;
    private RadioButton rbFalse;


    Cursor Cursor_SE_code = null;
    ArrayList<String[]> array_SE_code = null;
    private String current_se = "";

    Cursor Cursor_Users = null;
    ArrayList<String[]> array_Users = null;
    String curent_username = "";

    Cursor Cursor_Eq = null;
    ArrayList<String[]> array_Eq = null;
    String current_equipment = "";
    String current_equipment_type = "";


    boolean current_yn_resolve = true;

    String current_comment = "";
    String current_reading = "";
    String current_unit = "";
    Boolean bBarcodeEquip = false;
    Integer maxId = 0;
    Button btnInputForms;
    public HandHeld_SQLiteOpenHelper dbHelper;
    public SQLiteDatabase db;
    Button btnSave;
    Button btnClear;
    Button btnDelete;
    Button btnDone;

    Context ct = this;
    Boolean isRecordsSavedToDB = true;
    Boolean bAcceptWarningValid = false;
    Boolean bAcceptWarningDuplicate = false;


    private SiteEvents default_site_event_reading;
    private SiteEvents current_site_event_reading;
    private DataTable_SiteEvent se_table = new DataTable_SiteEvent();

    Boolean[] bDialogChoice = {false};

    private Boolean isLastRecordSavedToTable = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_general_eq);
        bAcceptWarningValid = false;
        bAcceptWarningDuplicate = false;
        mContext = this;

        default_site_event_reading = SiteEvents.GetDefaultReading();
        current_site_event_reading = SiteEvents.GetDefaultReading();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            System.out.println("we have default reading");
            default_site_event_reading = (SiteEvents) getIntent().getSerializableExtra("SE");
            se_table = (DataTable_SiteEvent) getIntent().getSerializableExtra("SE_TABLE");
            if (se_table == null)
                se_table = new DataTable_SiteEvent();
        } else {
            System.out.println("no default reading");
            default_site_event_reading = SiteEvents.GetDefaultReading();
            se_table = new DataTable_SiteEvent();
        }

        try {
            current_site_event_reading = (SiteEvents) default_site_event_reading.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        current_se = default_site_event_reading.getStrSE_ID();
        current_equipment = default_site_event_reading.getStrEq_ID();
        curent_username = default_site_event_reading.getStrUserName();
        current_comment = default_site_event_reading.getStrComment();
        current_SEDateTime = default_site_event_reading.getDatSE_Date();
        current_ResDateTime = default_site_event_reading.getDatResDate();
        if (default_site_event_reading.getYnResolved().toLowerCase() == "true")
            current_yn_resolve = true;
        else current_yn_resolve = false;

        Log.i("------------onCreate Activity_Main_Input", "10");
        //super.onCreate(savedInstanceState);
        Log.i("------------onCreate Activity_Main_Input", "1");

        AppDataTables tables = new AppDataTables();
        tables.SetSiteEventsTablesStructure();

        dbHelper = new HandHeld_SQLiteOpenHelper(ct, tables);
        db = dbHelper.getReadableDatabase();

        int rowsInDB = dbHelper.getRowsInLookupTables(db);
        if (rowsInDB < 1) {
            AlertDialogShow("The Lookup Tables aren't populated, go to Menu | Download and Populate Lookup DB", "ERROR!", "warning");
        }


        maxId = dbHelper.getMaxID_FromSiteEventsTable(db);

        ///Log.i("------------onCreate", Locs.getColumnName(1));
        spin_SE_Code = (Spinner) findViewById(R.id.txt_Site_Event_Code);
        spin_Equip_Code = (Spinner) findViewById(R.id.txt_equip_id);
        spin_User_name = (Spinner) findViewById(R.id.txt_User_name);

        rbTrue = (RadioButton) findViewById(R.id.radio_true);
        rbFalse = (RadioButton) findViewById(R.id.radio_false);
        rbResloved = (RadioGroup) findViewById(R.id.radio_group);
        rbResloved.clearCheck();
        if (current_yn_resolve)
            rbResloved.check(R.id.radio_true);
        else
            rbResloved.check(R.id.radio_false);


        //text_event_time
        text_event_time = (TextView) findViewById(R.id.text_event_time);
        text_event_time.setText(DateTimeHelper.GetStringTimeFromDateTime(current_SEDateTime, ""));
        text_event_time_picker();
        //text_event_date
        text_event_date = (TextView) findViewById(R.id.text_event_date);
        text_event_date.setText(DateTimeHelper.GetStringDateFromDateTime(current_SEDateTime, ""));
        text_event_date_picker();

        //////////////////////
        //resolve date
        text_resolve_date = (TextView) findViewById(R.id.text_resolve_date);
        text_resolve_date.setText(DateTimeHelper.GetStringDateFromDateTime(current_ResDateTime, ""));
        text_resolve_date_picker();
        //text_resolve_time
        text_resolve_time = (TextView) findViewById(R.id.text_resolve_time);
        text_resolve_time.setText(DateTimeHelper.GetStringTimeFromDateTime(current_ResDateTime, ""));
        text_resolve_time_picker();


        // needed for spinner
        int[] toL = new int[]{android.R.id.text1};
        //Populate Spinners (comboboxes)
        // equipment
        Cursor_Eq = dbHelper.GetCursorEquipment(db);
        array_Eq = transferCursorToArrayList(Cursor_Eq);
        String[] from_Eq = new String[]{DataTable_Equip_Ident.strEqID};
        SimpleCursorAdapter adapter_Eq =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                        Cursor_Eq, from_Eq, toL, 0);
        adapter_Eq.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_Equip_Code.setAdapter(adapter_Eq);
        setSpinnerValue(spin_Equip_Code, array_Eq, current_equipment);

        spin_Equip_Code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                spin_Equip_Code_Listener(parent, pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {

                Log.i("isLastRecordSavedToTable", "spin_loc_id.listener onNothingSelected isLastRecordSavedToTable:" + isLastRecordSavedToTable.toString());
            }
        });

        Cursor_Users = dbHelper.GetCursorUsers(db);
        array_Users = transferCursorToArrayList(Cursor_Users);

        String[] from_Users = new String[]{DataTable_Users.strUserName};

        SimpleCursorAdapter adapter_users =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                        Cursor_Users, from_Users, toL, 0);
        adapter_users.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_User_name.setAdapter(adapter_users);
        setSpinnerValue(spin_User_name, array_Users, curent_username);

        //Site event types
        Cursor_SE_code = dbHelper.GetCursorSECode(db);
        array_SE_code = transferCursorToArrayList(Cursor_SE_code);

        String[] from_SE_CODE = new String[]{DataTable_Site_Event_Def.strSE_Desc};

        SimpleCursorAdapter adapter_SE_code =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                        Cursor_SE_code, from_SE_CODE, toL, 0);
        adapter_SE_code.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_SE_Code.setAdapter(adapter_SE_code);
        setSpinnerValue(spin_SE_Code, array_SE_code, current_se);

        txt_comment = (EditText) findViewById(R.id.txt_comment);
        txt_comment.setText(current_comment);
        txt_comment.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                Log.i("isLastRecordSavedToTable", "txtComment.addTextChangedListener " + isLastRecordSavedToTable.toString());

                isLastRecordSavedToTable = false;
            }
        });


        btnClear = (Button) findViewById(R.id.btn_clear);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearForms();
            }
        });

        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(bAcceptWarningValid);

                Validation iChecked = saveForms(bAcceptWarningValid, bAcceptWarningDuplicate);
                if (iChecked.isValid() ||
                        (iChecked.isWarning() && bAcceptWarningValid) || (iChecked.isWarningDuplicate() && bAcceptWarningDuplicate))
                    isLastRecordSavedToTable = true;

                if (iChecked.isWarning())
                    bAcceptWarningValid = true;
                if (iChecked.isWarningDuplicate())
                    bAcceptWarningDuplicate = true;
                System.out.println(bAcceptWarningValid);
                System.out.println(bAcceptWarningDuplicate);
                System.out.println(iChecked);

                isRecordsSavedToDB = false;

            }
        });

        btnDone = (Button) findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isLastRecordSavedToTable) {
                    isRecordsSavedToDB = dbHelper.getInsertTable(db, se_table);
                    int records = se_table.GetNumberOfRecords();

                    if (isRecordsSavedToDB) {
                        String message = "The data (" + String.valueOf(records) + " records) is saved and ready to be uplaoded.";
                        Toast.makeText(ct, message, Toast.LENGTH_SHORT).show();
                        se_table = new DataTable_SiteEvent();
                    }
                } else
                    isRecordsSavedToDB = false;

                Log.i("isLastRecordSavedToTable", "btnDone.setOnClickListener ,isLastRecordSavedToTable " + isLastRecordSavedToTable.toString());
                if (isLastRecordSavedToTable && isRecordsSavedToDB) {
                    clearForms();
                }
                Log.i("isLastRecordSavedToTable", "btnDone.setOnClickListener ,isLastRecordSavedToTable " + isLastRecordSavedToTable.toString());

                onBackPressed();

            }
        });

        spin_SE_Code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);

                String desc = ((String[]) array_SE_code.get(pos))[2];
                current_se = ((String[]) array_SE_code.get(pos))[1];
            }

            public void onNothingSelected(AdapterView<?> parent) {

                Log.i("isLastRecordSavedToTable", "spin_loc_id.listener onNothingSelected isLastRecordSavedToTable:" + isLastRecordSavedToTable.toString());
            }
        });
        int idCol = getIndexFromArraylist(array_SE_code, current_se, 1);
        spin_SE_Code.setSelection(idCol);
        spin_User_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                TextView temp;
                temp = (TextView) spin_User_name.getSelectedView();
                curent_username = temp.getText().toString();

                if (!Objects.equals(curent_username, "NA"))
                    isLastRecordSavedToTable = false;

                Log.i("isLastRecordSavedToTable", "spin_col_id.listener " + isLastRecordSavedToTable.toString());
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Log.i("onCreate", "4");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }
    private void spin_Equip_Code_Listener(AdapterView<?> parent, int pos) {
        Object item = parent.getItemAtPosition(pos);

        current_equipment_type = ((String[]) array_Eq.get(pos))[2];
        current_equipment = ((String[]) array_Eq.get(pos))[1];

        if (!bBarcodeEquip) {
            //strDataModComment = "Manual";
            bBarcodeEquip = false;
        } else {
            //strDataModComment = "";
            bBarcodeEquip = false;
        }

        if(!current_equipment.startsWith("NA")) {
//collect dataIntent seintent
            Intent seintent = null;
            SaveReadingsToSiteEventRecord();

            MeasurementTypes.MEASUREMENT_TYPES type = MeasurementTypes.GetFrom_SE_ID(current_equipment, current_equipment_type);
            if (type == MeasurementTypes.MEASUREMENT_TYPES.SE) {
                seintent = new Intent("android.intent.action.INPUT_GENERAL_EQ_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
            }
            if (type == MeasurementTypes.MEASUREMENT_TYPES.VOC) {
                seintent = new Intent("android.intent.action.INPUT_VOC_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
            }
            if (seintent != null)
                SetAndStartIntent(seintent);
        }
        bAcceptWarningValid = false;
        bAcceptWarningDuplicate = false;

        if (!Objects.equals(current_equipment, "NA"))
            isLastRecordSavedToTable = false;

        Log.i("isLastRecordSavedToTable", "spin_Equip_Code.listener isLastRecordSavedToTable:" + isLastRecordSavedToTable.toString());
        Log.i("isLastRecordSavedToTable", "spin_Equip_Code.listener isRecordsSavedToDB:" + isRecordsSavedToDB.toString());
        Log.i("current_equipment", "spin_Equip_Code.listener current_equipment" + current_equipment);
    }

    private void SetAndStartIntent(Intent seintent) {
        seintent.putExtra("SE", current_site_event_reading);
        seintent.putExtra("SE_TABLE", se_table );
        startActivity(seintent);
    }

//    @Override
//    public void onBarcodeEvent(final BarcodeReadEvent event) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // update UI to reflect the data
//                Log.i("onBarcodeEvent", "onBarcodeEvent!!!!");
//                List<String> list = new ArrayList<>();
//                String tempcurrent_loc = event.getBarcodeData();
//                current_SEDateTime = Calendar.getInstance().getTime(); //
//                if (tempcurrent_loc != null || tempcurrent_loc != "")
//                    current_se = tempcurrent_loc;
//                //current_se = event.getBarcodeData();
//                Log.i("onBarcodeEvent", getCurrent_loc());
//
//                isLastRecordSavedToTable = false;
//
//                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
//                        Activity_GeneralEq_Input.this, android.R.layout.simple_list_item_1, list);
//
//                int id = getIndexFromArraylist(alLocs, getCurrent_loc(), 1);
//
//                Log.i("onBarcodeEvent", "id: " + Integer.toString(id));
//                if (id > 0) {
//                    bBarcodeLocation = true;
//                }
//                //spin_Loc_id.setSelection(id);
//                barcodeList.setAdapter(dataAdapter);
//                isRecordsSavedToDB = false;
//                bAcceptWarningDuplicate = false;
//                bAcceptWarningValid = false;
//
//                Log.i("isLastRecordSavedToTable", "on barcode event isLastRecordSavedToTable " + isLastRecordSavedToTable.toString());
//                Log.i("isLastRecordSavedToTable", "on barcode event isRecordsSavedToDB " + isRecordsSavedToDB.toString());
//            }
//        });
//    }

    //private method of your class

    private int getIndex(Spinner spinner, String myString) {
        System.out.println("getIndex spinner " + spinner.toString());
        System.out.println("getIndex myString " + myString);
        int n = spinner.getCount();

        SimpleCursorAdapter adapt = (SimpleCursorAdapter) spinner.getAdapter();

        for (int i = 0; i < n; i++) {
            String sValue = spinner.getItemAtPosition(i).toString();
            String sValue1 = adapt.getItem(i).toString();
            System.out.println("getIndex sValue " + sValue + "--" + sValue1);
            if (sValue.equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }


    // When using Automatic Trigger control do not need to implement the
    // onTriggerEvent function
//    @Override
//    public void onTriggerEvent(TriggerStateChangeEvent event) {
//        try {
//            // only handle trigger presses
//            // turn on/off aimer, illumination and decoding
//            Log.i("onTriggerEvent", "no Data");
//            /*
//            To get the "CR" in the barcode to be processed two settings needs to be changed:
//"Settings - Honeywell Settings - Scanning - Internal Scanner - Default profile - Data Processing Settings. Set:
//Wedge Method" to 'keyboard'
//Wedge as keys to empty
//             */
//            barcodeReader.aim(event.getState());
//            barcodeReader.light(event.getState());
//            barcodeReader.decode(event.getState());
//
//        } catch (ScannerNotClaimedException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Scanner is not claimed", Toast.LENGTH_SHORT).show();
//        } catch (ScannerUnavailableException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void SaveReadingsToSiteEventRecord() {
        //note that dates and times saved in the events
        String comm = (String) txt_comment.getText().toString();
        current_site_event_reading.setStrComment(comm);
        current_site_event_reading.setStrUserName(curent_username);
        current_site_event_reading.setStrEq_ID(current_equipment);
        current_site_event_reading.setStrSE_ID(current_se);
        if (rbTrue.isChecked()) {
            current_yn_resolve = true;
            current_site_event_reading.setYnResolved("true");
        } else {
            current_yn_resolve = false;
            current_site_event_reading.setYnResolved("false");
        }

    }

    @Override
    public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {

    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent arg0) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //List<String> list = new ArrayList<String>();
                //final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                //         StDetInputActivity.this, android.R.layout.simple_list_item_1, list);
                Log.i("no data", "no Data");
                if (Objects.equals(current_se, "NA")) {
                    //int id = getIndexFromArraylist(all, "NA", 1);

                    //Log.i("onFailureEvent", "Id = "+Integer.toString(id));

                    //bBarcodeLocation = false;
                    //spin_Loc_id.setSelection(id);
                    //barcodeList.setAdapter(dataAdapter);
                    Toast.makeText(Activity_GeneralEq_Input.this, "No data yet", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Activity_GeneralEq_Input.this, current_se, Toast.LENGTH_SHORT).show();

                }


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (barcodeReader != null) {
//            try {
//                barcodeReader.claim();
//            } catch (ScannerUnavailableException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (barcodeReader != null) {
//            // release the scanner claim so we don't get any scanner
//            // notifications while paused.
//            barcodeReader.release();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (barcodeReader != null) {
//            // unregister barcode event listener
//            barcodeReader.removeBarcodeListener(this);

        // unregister trigger state change listener
        //   barcodeReader.removeTriggerListener(this);
        db.close();
        //}
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

    private int getIndexFromArraylist(ArrayList<String[]> list, String myString, Integer column) {

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

    public String getCurrent_loc() {
        return current_se;
    }

    public void setCurrent_loc(String current_loc) {
        this.current_se = current_loc;
    }

    public void clearForms() {

        txt_comment.setText("");

        int id = 0;

        isLastRecordSavedToTable = true;
        Log.i("isLastRecordSavedToTable", " clear forms " + isLastRecordSavedToTable.toString());

        //txt_Reading.requestFocus();
    }

    public Validation saveForms(boolean bAcceptWarning, boolean bAcceptWarningDuplicate) {

        current_site_event_reading.setLngID((int) (new Date().getTime() / 1000));

          Validation isTheRecordValid = Activity_Main_Input.IsRecordValid(current_site_event_reading,
                spin_Equip_Code,
                spin_SE_Code, spin_User_name, null);
        Validation isTheRecordDup = Activity_Main_Input.IsRecordDup(db, dbHelper,current_site_event_reading, se_table);

        boolean bAcceptDup = isTheRecordDup.isValid() || (isTheRecordDup.isWarningDuplicate() && bAcceptWarningDuplicate);
        boolean bAcceptRecord = isTheRecordValid.isValid() || (isTheRecordValid.isWarning() && bAcceptWarning);

        if (isTheRecordValid.isError()) {
            AlertDialogShowError(isTheRecordValid.getValidationMessage(), "ERROR");
        } else if (isTheRecordValid.isWarning() && !bAcceptWarning) {
            AlertDialogShow("Please check\n" + isTheRecordValid.getValidationMessage() + "\nPress 'Save' one more time to confirm the data as VALID or update the input data.", "Warning");
        } else if (isTheRecordDup.isWarningDuplicate() && !bAcceptWarningDuplicate) {
            AlertDialogHighWarning("Please check\n" + isTheRecordDup.getValidationMessage() + "\nPress 'Save' one more time to confirm the data as VALID or update the input data.", "Warning");
            return isTheRecordDup;
        } else if ((bAcceptRecord) && (bAcceptDup)) {
            System.out.println(isTheRecordValid.getValidationMessageWarning() + isTheRecordValid.getValidationMessageError());
            maxId = se_table.AddToTable(current_site_event_reading);
            isRecordsSavedToDB = false;
            maxId++;
            clearForms();
            System.out.println("NEW max id " + maxId.toString());
        }

        return isTheRecordValid;
    }


    private void AlertDialogShowError(String message, String title) {
        AlertDialogShow(message, title, "OK", "error");
    }

    private void AlertDialogHighWarning(String message, String title) {
        AlertDialogShow(message, title, "OK", "warning");
    }

    private void AlertDialogShow(String message, String title) {
        AlertDialogShow(message, title, "OK");
    }

    private void AlertDialogShow(String message, String title, String button) {
        AlertDialogShow(message, title, button, "default");
    }

    private void AlertDialogShow(String message, String title, String button, String theme) {
        int themeResId = R.style.AlertDialogTheme;
        try {
            if (theme.toLowerCase().equals("warning")) {
                themeResId = R.style.AlertDialogWarning;
            }
            if (theme.toLowerCase().equals("error")) {
                themeResId = R.style.AlertDialogError;
            }

            AlertDialog ad = new AlertDialog.Builder(this, themeResId)
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

    private void text_event_time_picker() {
        text_event_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar mcurrentTime =
                        DateTimeHelper.GetCalendarFromDateTime(current_site_event_reading.getDatSE_Time(), "");

                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
//https://stackoverflow.com/questions/32678968/android-timepickerdialog-styling-guide-docs
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ct, R.style.CustomTimePickerDialog,
                        (timePicker, selectedHour, selectedMinute) -> {
                            String strSE_DateTime = current_site_event_reading.getDatSE_Date();
                            Calendar cal = DateTimeHelper.GetCalendarFromDateTime(strSE_DateTime, "");
                            strSE_DateTime = DateTimeHelper.UpdateTime(strSE_DateTime, selectedHour, selectedMinute, 0);
                            String aTime = DateTimeHelper.GetStringTimeFromDateTime(strSE_DateTime);
                            text_event_time.setText(aTime);
                            current_site_event_reading.setDatSE_Date(strSE_DateTime);
                        }, hour, minute, false);
                mTimePicker.setTitle("Select Event Time");
                mTimePicker.show();

            }
        });
    }

    private void text_resolve_time_picker() {
        text_resolve_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar mcurrentTime =
                        DateTimeHelper.GetCalendarFromDateTime(current_site_event_reading.getDatResDate(), "");

                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ct, R.style.CustomTimePickerDialog,
                        (timePicker, selectedHour, selectedMinute) -> {
                            String strSE_DateTime = current_site_event_reading.getDatSE_Date();
                            Calendar cal = DateTimeHelper.GetCalendarFromDateTime(strSE_DateTime, "");
                            strSE_DateTime = DateTimeHelper.UpdateTime(strSE_DateTime, selectedHour, selectedMinute, 0);
                            text_resolve_time.setText(DateTimeHelper.GetStringTimeFromDateTime(strSE_DateTime));
                            current_site_event_reading.setDatResDate(strSE_DateTime);
                        }, hour, minute, false);
                mTimePicker.setTitle("Select Resolved Time");
                mTimePicker.show();

            }
        });
    }

    private void text_resolve_date_picker() {
        text_resolve_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar mcurrentTime =
                        DateTimeHelper.GetCalendarFromDateTime(current_site_event_reading.getDatResDate(), "");

                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentTime.get(Calendar.MONTH);
                int year = mcurrentTime.get(Calendar.YEAR);


                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(mContext, R.style.SpinnerDatePickerDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker dPicker, int year,
                                                  int month, int day) {

                                String strSE_DateTime = current_site_event_reading.getDatSE_Date();
                                Calendar cal = DateTimeHelper.GetCalendarFromDateTime(strSE_DateTime, "");
                                strSE_DateTime = DateTimeHelper.UpdateDate(strSE_DateTime, year, month, day);

                                text_resolve_date.setText(DateTimeHelper.GetStringDateFromDateTime(strSE_DateTime, ""));
                                current_site_event_reading.setDatResDate(strSE_DateTime);

                            }
                        }, year, month, day);
                mDatePicker.setTitle("Select Resolved Date");
                mDatePicker.show();
            }
        });
    }

    private void text_event_date_picker() {
        text_event_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar mcurrentTime =
                        DateTimeHelper.GetCalendarFromDateTime(current_site_event_reading.getDatSE_Time(), "");

                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentTime.get(Calendar.MONTH);
                int year = mcurrentTime.get(Calendar.YEAR);


                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(mContext, R.style.SpinnerDatePickerDialog,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker dPicker, int year,
                                                  int month, int day) {

                                String strSE_DateTime = current_site_event_reading.getDatSE_Date();
                                Calendar cal = DateTimeHelper.GetCalendarFromDateTime(strSE_DateTime, "");
                                strSE_DateTime = DateTimeHelper.UpdateDate(strSE_DateTime, year, month, day);

                                text_event_date.setText(DateTimeHelper.GetStringDateFromDateTime(strSE_DateTime, ""));
                                current_site_event_reading.setDatSE_Date(strSE_DateTime);

                            }
                        }, year, month, day);
                mDatePicker.setTitle("Select Event Date");
                mDatePicker.show();

            }
        });
    }

    @Override
    public void onBackPressed() {

        Log.i("isLastRecordSavedToTable ", " onBackPressed isLastRecordSavedToTable BEFORE " + isLastRecordSavedToTable.toString());
        Log.i("isLastRecordSavedToTable ", " onBackPressed isRecordsSavedToDB  " + isRecordsSavedToDB.toString());

        if (isLastRecordSavedToTable && isRecordsSavedToDB) {
            // code here to show dialog
            super.onBackPressed();  // optional depending on your needs
        }

        if (isLastRecordSavedToTable && !isRecordsSavedToDB) {
            isRecordsSavedToDB = true;
            AlertDialogHighWarning("Do you want to Exit Input Form Without Saving?" + "\n" + "Hit Done to Save or Back button again to exit without saving.", "Warning!");
        }

        if (!isLastRecordSavedToTable) {
            isLastRecordSavedToTable = true;
            AlertDialogHighWarning("The record has not been saved." + "\n" + "Hit Done or Back button again to exit without saving.", "Warning!");
        }


        Log.i("isLastRecordSavedToTable ", "onBackPressed isLastRecordSavedToTable AFTER " + isLastRecordSavedToTable.toString());
        Log.i("isLastRecordSavedToTable ", " onBackPressed isRecordsSavedToDB AFTER " + isRecordsSavedToDB.toString());
    }

    @Override
    public void onTriggerEvent(TriggerStateChangeEvent triggerStateChangeEvent) {

    }

    private void setSpinnerValue(Spinner spinner, ArrayList<String[]> strValues, String strValue) {
        int index = getIndexFromArraylist(strValues, strValue, 1);
        spinner.setSelection(index);
    }
}