package com.honeywell.stevents;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
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
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Activity_GeneralEq_Input extends AppCompatActivity implements BarcodeReader.BarcodeListener,
        BarcodeReader.TriggerListener {
    private BarcodeReader barcodeReader;
    private ListView barcodeList;
    private final String default_SE="Maintain";
    private String current_SEDateTime;

    MeasurementTypes.MEASUREMENT_TYPES current_type = MeasurementTypes.MEASUREMENT_TYPES.GENERAL_BARCODE;

    private Spinner spin_SE_Code;
    private Spinner spin_Equip_Code;
    private Spinner spin_User_name;

    private TextView text_event_date;
    private TextView text_event_time;
//    private TextView text_resolve_date;
//    private TextView text_resolve_time;

    private TextView txt_comment;

    private RadioGroup rbResloved;
    private RadioButton rbStartup;
    private RadioButton rbShutdown;


    Cursor Cursor_SE_code = null;
    ArrayList<String[]> array_SE_code = null;
    private String current_se = "";
    private String prior_current_se = "";


    Cursor Cursor_Users = null;
    ArrayList<String[]> array_Users = null;
    String current_maintenance = "";
    String prior_current_maintenance = "";

    Cursor Cursor_Eq = null;
    ArrayList<String[]> array_Eq = null;
    String current_equipment = "";
    String prior_current_equipment = "";
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
    Button btnDone;

    Context ct = this;
    Boolean isRecordsSavedToDB = true;
    Boolean bAcceptWarningValid = false;
    Boolean bAcceptWarningDuplicate = false;


    private SiteEvents current_site_event_reading_copy;
    private SiteEvents current_site_event_reading;
    private DataTable_SiteEvent se_table = new DataTable_SiteEvent();

    Boolean[] bDialogChoice = {false};

    private Boolean isLastRecordSavedToTable = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_general_eq);

        AppDataTables tables = new AppDataTables();
        tables.SetSiteEventsTablesStructure();
        dbHelper = new HandHeld_SQLiteOpenHelper(ct, tables);
        db = dbHelper.getReadableDatabase();


        bAcceptWarningValid = false;
        bAcceptWarningDuplicate = false;

       // Toast.makeText(ct, ct.getPackageName(), Toast.LENGTH_SHORT).show();
        prior_current_equipment = "NA";
        current_site_event_reading = SiteEvents.GetDefaultReading();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            System.out.println("we have default reading");
            current_site_event_reading = (SiteEvents) getIntent().getSerializableExtra("SE");
            se_table = (DataTable_SiteEvent) getIntent().getSerializableExtra("SE_TABLE");

            if (se_table == null)
                se_table = new DataTable_SiteEvent();
        }
        current_site_event_reading.setMeasurementType(current_type);
        String[] login = dbHelper.getLoginInfo(db);
        if (login !=null && login.length >0) {
            current_site_event_reading.setStrUserName(login[0]);
            current_site_event_reading.setStrUserUploadName(login[0]);
        }
        try {
            current_site_event_reading_copy = (SiteEvents) current_site_event_reading.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        //current_se = default_site_event_reading.getStrSE_ID();

        current_se =default_SE;
        current_equipment = current_site_event_reading.getStrEq_ID();
        current_maintenance = current_site_event_reading.getStrM_Per_FirstLastName();
        current_comment = current_site_event_reading.getStrComment();
        current_SEDateTime = current_site_event_reading.getDatSE_Date();

        current_yn_resolve = current_site_event_reading.getBoolResolved();
        prior_current_maintenance = current_maintenance;
        //prior_current_equipment = current_equipment;
        prior_current_se = current_se;
        current_site_event_reading.setMeasurementType(current_type);


        int rowsInDB = dbHelper.getRowsInLookupTables(db);
        if (rowsInDB < 1) {
            AlertDialogShow("The Lookup Tables aren't populated, go to Menu | Download and Populate Lookup DB", "ERROR!", "warning");
        }


        maxId = dbHelper.getMaxID_FromSiteEventsTable(db);

        ///Log.i("------------onCreate", Locs.getColumnName(1));
        spin_SE_Code = (Spinner) findViewById(R.id.txt_Site_Event_Code);
        spin_Equip_Code = (Spinner) findViewById(R.id.txt_equip_id);
        spin_User_name = (Spinner) findViewById(R.id.txt_User_name);

        rbStartup = (RadioButton) findViewById(R.id.radio_true);
        rbShutdown = (RadioButton) findViewById(R.id.radio_false);
        rbResloved = (RadioGroup) findViewById(R.id.radio_group);
        rbResloved.clearCheck();
//        if (current_yn_resolve)
//            rbResloved.check(R.id.radio_true);
//        else
//            rbResloved.check(R.id.radio_false);
        rbResloved.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                if (rb == rbStartup)
                    txt_comment.setText("PCTF Startup ");
                if (rb == rbShutdown)
                    txt_comment.setText("PCTF Shutdown ");
                if ((rb == rbStartup) || (rb == rbShutdown))
                    SetSpinnerValue(spin_SE_Code, array_SE_code, "Operate",2);
            }
        });


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
//        text_resolve_date = (TextView) findViewById(R.id.text_resolve_date);
//        text_resolve_date.setEnabled(false);
//        text_resolve_date.setText(DateTimeHelper.GetStringDateFromDateTime(current_ResDateTime, ""));
//        text_resolve_date_picker();
        //text_resolve_time
//        text_resolve_time = (TextView) findViewById(R.id.text_resolve_time);
//        text_resolve_time.setEnabled(false);
//        text_resolve_time.setText(DateTimeHelper.GetStringTimeFromDateTime(current_ResDateTime, ""));
//        text_resolve_time_picker();


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
        //prior_current_equipment = current_equipment;
        spin_Equip_Code.setAdapter(adapter_Eq);
        SetSpinnerValue(spin_Equip_Code, array_Eq, current_equipment,1);
        spin_Equip_Code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //CUSTOM METHOD
                spin_Equip_Code_Listener(parent, pos);
               // isLastRecordSavedToTable = false;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //USERS
        Cursor_Users = dbHelper.GetCursorMaintenancePerson(db);
        array_Users = transferCursorToArrayList(Cursor_Users);

        String[] from_Users = new String[]{DataTable_Users.strName};

        SimpleCursorAdapter adapter_users =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                        Cursor_Users, from_Users, toL, 0);
        adapter_users.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_User_name.setAdapter(adapter_users);
        SetSpinnerValue(spin_User_name, array_Users, current_maintenance,1);
        spin_User_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                current_maintenance = GetSpinnerValue(spin_User_name);
                if ((!current_maintenance.equals("NA"))
                        && (!current_maintenance.equals(prior_current_maintenance)))
                    isLastRecordSavedToTable = false;

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Site event types
        Cursor_SE_code = dbHelper.GetCursorSECode(db);
        array_SE_code = transferCursorToArrayList(Cursor_SE_code);

        String[] from_SE_CODE = new String[]{DataTable_Site_Event_Def.strSE_Desc};

        SimpleCursorAdapter adapter_SE_code =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                        Cursor_SE_code, from_SE_CODE, toL, 0);
        adapter_SE_code.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_SE_Code.setAdapter(adapter_SE_code);
        SetSpinnerValue(spin_SE_Code, array_SE_code, current_se,2);
        spin_SE_Code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                current_se = GetSpinnerValue(spin_SE_Code);
                if ((!current_se.equals("NA") &&
                        (!current_se.equals(prior_current_se))))
                    isLastRecordSavedToTable = false;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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
                if (!txt_comment.getText().toString().equals(""))
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
                SaveReadingsToSiteEventRecord();
                System.out.println(bAcceptWarningValid);

                SaveFormAndValidate();

                isRecordsSavedToDB = false;

            }
        });

        btnDone = (Button) findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("CodeDebug","isLastRecordSavedToTable "+ Boolean.toString(isLastRecordSavedToTable) );
                if (!isLastRecordSavedToTable) {

                    SaveFormAndValidate();
                }
                if (isLastRecordSavedToTable) {
                    isRecordsSavedToDB = dbHelper.getInsertTable(db, se_table);
                    int records = se_table.GetNumberOfRecords();

                    if (isRecordsSavedToDB) {
                        String message = "The data (" + String.valueOf(records) + " records) is saved and ready to be uploaded.";
                        Toast.makeText(ct, message, Toast.LENGTH_SHORT).show();
                        se_table = new DataTable_SiteEvent();
                    }
                } else
                    isRecordsSavedToDB = false;

                Log.i("isLastRecordSavedToTable", "btnDone.setOnClickListener ,isLastRecordSavedToTable " + isLastRecordSavedToTable.toString());
                if (isLastRecordSavedToTable && isRecordsSavedToDB) {
                    clearForms();
                    Intent intent = new Intent(ct, Activity_Main.class);
                    startActivity(intent);
                    finish();
                    // onBackPressed();
                }
            }
        });

        // ADD BARCODE
// get bar code instance from MainActivity
        barcodeReader = Activity_Main.getBarcodeObject();

        if (barcodeReader != null) {

            // register bar code event listener
            barcodeReader.addBarcodeListener(this);
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
        barcodeList = (ListView) findViewById(R.id.listViewBarcodeData);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }
    private void spin_Equip_Code_Listener(AdapterView<?> parent, int pos) {
        Object item = parent.getItemAtPosition(pos);

        Log.i("CodeDebug", "GE -> " +  " "+ current_equipment + current_site_event_reading.getStrEq_ID() + current_site_event_reading_copy.getStrEq_ID());
        try {
            current_site_event_reading_copy = (SiteEvents) current_site_event_reading.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        current_equipment_type = ((String[]) array_Eq.get(pos))[2];
        current_equipment = ((String[]) array_Eq.get(pos))[1];

        current_SEDateTime = DateTimeHelper.GetDateTimeNow();
        text_event_date.setText(DateTimeHelper.GetStringDateFromDateTime(current_SEDateTime,""));
        text_event_time.setText(DateTimeHelper.GetStringTimeFromDateTime(current_SEDateTime,""));

        SaveReadingsToSiteEventRecord();

        if(Objects.equals(current_equipment, "NA"))
            return;

        boolean b = current_site_event_reading.equalAllExceptEquipment(current_site_event_reading_copy);
        current_type = MeasurementTypes.GetFrom_SE_ID(current_equipment, current_equipment_type);

        if((!current_equipment.startsWith("NA")   &&    !Objects.equals(prior_current_equipment, current_equipment))||b)
        {

//collect dataIntent seintent
            Intent seintent = null;

            if (current_type == MeasurementTypes.MEASUREMENT_TYPES.GENERAL_BARCODE) {
                prior_current_equipment=current_equipment;
                current_SEDateTime = DateTimeHelper.GetDateTimeNow();
                text_event_time.setText(DateTimeHelper.GetStringTimeFromDateTime(current_SEDateTime, ""));
                text_event_date.setText(DateTimeHelper.GetStringDateFromDateTime(current_SEDateTime, ""));
                //seintent = new Intent("android.intent.action.INPUT_GENERAL_EQ_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
            }
            if (current_type == MeasurementTypes.MEASUREMENT_TYPES.PH) {
                seintent = new Intent("android.intent.action.INPUT_PH_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
            }
            if (current_type == MeasurementTypes.MEASUREMENT_TYPES.NOISE) {
                seintent = new Intent("android.intent.action.INPUT_NOISE_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
            }
            if (current_type == MeasurementTypes.MEASUREMENT_TYPES.VOC) {

                seintent = new Intent("android.intent.action.INPUT_VOC_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
            }
            if (current_type == MeasurementTypes.MEASUREMENT_TYPES.OTHER) {
                seintent = new Intent("android.intent.action.SE_MAIN_INPUT_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
            }
            if (seintent != null)
                SetAndStartIntent(seintent);
        }
        bAcceptWarningValid = false;
        bAcceptWarningDuplicate = false;

        if (!Objects.equals(current_equipment, "NA") )
            isLastRecordSavedToTable = false;
    }

    public void SetSpinnerValue(Spinner spinner, ArrayList<String[]> strValues, String strValue, int iDataColumn ) {
        int index = GetIndexFromArraylist(strValues, strValue, iDataColumn);
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

    private void SetAndStartIntent(Intent seintent) {
        Log.i("Codedebug", "SetAndStartIntent - general eq");
        boolean isOnlyEquipmentChanged = true;
        if (isLastRecordSavedToTable)
            current_site_event_reading = current_site_event_reading.ResetValues();
        if (!isLastRecordSavedToTable)
            isOnlyEquipmentChanged = current_site_event_reading.equalAllExceptEquipment(current_site_event_reading_copy);

        if (isOnlyEquipmentChanged || isLastRecordSavedToTable)
            current_site_event_reading = current_site_event_reading.ResetValues();

        seintent.putExtra("SE", current_site_event_reading);
        seintent.putExtra("SE_TABLE", se_table);


        if (!isLastRecordSavedToTable && !isOnlyEquipmentChanged) {
            isLastRecordSavedToTable = true;
            AlertDialogHighWarning("The record has not been saved." + "\n" + "Hit Done or Back button again to exit without saving.", "Warning!");
            SetSpinnerValue(spin_Equip_Code, array_Eq, current_site_event_reading_copy.getStrEq_ID(),1);
            return;
        } else{
            Log.i("codedebug", "GE SetAndStartIntent startActivity ->" + seintent.toString());
            startActivity(seintent);
            finish();
        }
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // update UI to reflect the data
                
                List<String> list = new ArrayList<>();
                String tempcurrent = event.getBarcodeData();
                
                if (!Objects.equals(tempcurrent, ""))
                    current_equipment = tempcurrent;


                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                        ct, android.R.layout.simple_list_item_1, list);

                int id = getIndexFromArraylist(array_Eq, current_equipment, 1);

                if (id > 0) {
                    bBarcodeEquip = true;
                }
                if(id >-1) {
                    SetSpinnerValue(spin_Equip_Code, array_Eq, current_equipment,1);
                    //barcodeList.setAdapter(dataAdapter);
                    isRecordsSavedToDB = false;
                    bAcceptWarningDuplicate = false;
                    bAcceptWarningValid = false;
                }


            }
        });
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent arg0) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //List<String> list = new ArrayList<String>();
                //final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                //         StDetInputActivity.this, android.R.layout.simple_list_item_1, list);
                if (Objects.equals(current_equipment, "NA")) {
                    int id = getIndexFromArraylist(array_Eq, "NA", 1);

                    bBarcodeEquip = false;
                    //spin_Loc_id.setSelection(id);
                    //barcodeList.setAdapter(dataAdapter);
                    Toast.makeText(ct, "onFailureEvent No data yet", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ct, current_equipment, Toast.LENGTH_SHORT).show();

                }


            }
        });
    }


    // When using Automatic Trigger control do not need to implement the
    // onTriggerEvent function
    @Override
    public void onTriggerEvent(TriggerStateChangeEvent event) {
        try {
            // only handle trigger presses
            // turn on/off aimer, illumination and decoding

            /*
            To get the "CR" in the barcode to be processed two settings needs to be changed:
"Settings - Honeywell Settings - Scanning - Internal Scanner - Default profile - Data Processing Settings. Set:
Wedge Method" to 'keyboard'
Wedge as keys to empty
             */
            barcodeReader.aim(event.getState());
            barcodeReader.light(event.getState());
            barcodeReader.decode(event.getState());

        } catch (ScannerNotClaimedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Scanner is not claimed", Toast.LENGTH_SHORT).show();
        } catch (ScannerUnavailableException e) {
            e.printStackTrace();
            Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
            }
        }
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
        int id = GetIndexFromArraylist(array_Eq, "NA", 1);
        spin_Equip_Code.setSelection(id);
        id = GetIndexFromArraylist(array_SE_code, "Operate", 1);
        spin_SE_Code.setSelection(id);


        bBarcodeEquip = false;
        isLastRecordSavedToTable = true;

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
    private void SaveFormAndValidate() {

        Validation iChecked = saveForms(bAcceptWarningValid, bAcceptWarningDuplicate);
        if (iChecked.isValid() ||
                (iChecked.isWarning() && bAcceptWarningValid) || (iChecked.isWarningDuplicate() && bAcceptWarningDuplicate))
            isLastRecordSavedToTable = true;

        if (iChecked.isWarning())
            bAcceptWarningValid = true;
        if (iChecked.isWarningDuplicate())
            bAcceptWarningDuplicate = true;

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

                String temp1 = text_event_date.getText().toString();
                String temp2 = text_event_time.getText().toString();
                String dt =DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1,temp2);

                final Calendar mcurrentTime =
                        DateTimeHelper.GetCalendarFromDateTime(dt, "");

                int hour = mcurrentTime.get(Calendar.HOUR);
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

//    private void text_resolve_time_picker() {
//        text_resolve_time.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                String temp1 = text_resolve_date.getText().toString();
//                String temp2 = text_resolve_time.getText().toString();
//                String dt =DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1,temp2);
//
//                final Calendar mcurrentTime =
//                        DateTimeHelper.GetCalendarFromDateTime(dt, "");
//
//                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                int minute = mcurrentTime.get(Calendar.MINUTE);
//
//                TimePickerDialog mTimePicker;
//                mTimePicker = new TimePickerDialog(ct, R.style.CustomTimePickerDialog,
//                        (timePicker, selectedHour, selectedMinute) -> {
//                            String strSE_DateTime = current_site_event_reading.getDatSE_Date();
//                            Calendar cal = DateTimeHelper.GetCalendarFromDateTime(strSE_DateTime, "");
//                            strSE_DateTime = DateTimeHelper.UpdateTime(strSE_DateTime, selectedHour, selectedMinute, 0);
//                            text_resolve_time.setText(DateTimeHelper.GetStringTimeFromDateTime(strSE_DateTime));
//                            current_site_event_reading.setDatResDate(strSE_DateTime);
//                        }, hour, minute, false);
//                mTimePicker.setTitle("Select Resolved Time");
//                mTimePicker.show();
//
//            }
//        });
//    }
//
//    private void text_resolve_date_picker() {
//        text_resolve_date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                String temp1 = text_resolve_date.getText().toString();
//                String temp2 = text_resolve_time.getText().toString();
//                String dt =DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1,temp2);
//                final Calendar mcurrentTime =
//                        DateTimeHelper.GetCalendarFromDateTime(dt, "");
//
//                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
//                int month = mcurrentTime.get(Calendar.MONTH);
//                int year = mcurrentTime.get(Calendar.YEAR);
//
//
//                DatePickerDialog mDatePicker;
//                mDatePicker = new DatePickerDialog(ct, R.style.SpinnerDatePickerDialog,
//                        new DatePickerDialog.OnDateSetListener() {
//
//                            @Override
//                            public void onDateSet(DatePicker dPicker, int year,
//                                                  int month, int day) {
//
//                                String strSE_DateTime = current_site_event_reading.getDatSE_Date();
//                                Calendar cal = DateTimeHelper.GetCalendarFromDateTime(strSE_DateTime, "");
//                                strSE_DateTime = DateTimeHelper.UpdateDate(strSE_DateTime, year, month, day);
//
//                                text_resolve_date.setText(DateTimeHelper.GetStringDateFromDateTime(strSE_DateTime, ""));
//                                current_site_event_reading.setDatResDate(strSE_DateTime);
//
//                            }
//                        }, year, month, day);
//                mDatePicker.setTitle("Select Resolved Date");
//                mDatePicker.show();
//            }
//        });
//    }

    private void text_event_date_picker() {
        text_event_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String temp1 = text_event_date.getText().toString();
                String temp2 = text_event_time.getText().toString();
                String dt =DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1,temp2);
                final Calendar mcurrentTime =
                        DateTimeHelper.GetCalendarFromDateTime(dt, "");

                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentTime.get(Calendar.MONTH);
                int year = mcurrentTime.get(Calendar.YEAR);


                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(ct, R.style.SpinnerDatePickerDialog,
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

    private void SaveReadingsToSiteEventRecord() {
        //note that dates and times saved in the events
        current_comment = (String) txt_comment.getText().toString();
        current_maintenance = GetSpinnerValue(spin_User_name);

        String initials = dbHelper.GetMaintenanceInitialsByFirstLastName(db, current_maintenance);
        if (initials == null || (!initials.equals("")))
            current_site_event_reading.setStrM_Per_ID(initials);
        current_site_event_reading.setMeasurementType(MeasurementTypes.MEASUREMENT_TYPES.GENERAL_BARCODE);

        current_se = GetSpinnerValue(spin_SE_Code);
        current_equipment = GetSpinnerValue(spin_Equip_Code);
        current_site_event_reading.setStrComment(current_comment);
        current_site_event_reading.setStrM_Per_FirstLastName(current_maintenance);
        current_site_event_reading.setStrEq_ID(current_equipment);
        current_site_event_reading.setStrSE_ID(current_se);
        String desc = dbHelper.GetEqDescDB(db, current_equipment);
        if (desc == null || (!desc.equals("")))
            current_site_event_reading.setStrEqDesc(desc);

        if (rbStartup.isChecked()) {
            current_yn_resolve = true;
            current_site_event_reading.setYnResolved("true");
        }
        if  (rbShutdown.isChecked()){
            current_yn_resolve = false;
            current_site_event_reading.setYnResolved("false");
        }
        String temp1 = text_event_date.getText().toString();
        String temp2 = text_event_time.getText().toString();

        current_site_event_reading.setDatSE_Date(DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1,temp2));

    }
    public String GetSpinnerValue(Spinner spinner) {
        TextView textView = (TextView)spinner.getSelectedView();
        String text = textView.getText().toString();

        return text;

    }

}