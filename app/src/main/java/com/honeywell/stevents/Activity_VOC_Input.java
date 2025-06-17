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
import android.view.KeyEvent;
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


public class Activity_VOC_Input extends AppCompatActivity implements BarcodeReader.BarcodeListener,
        BarcodeReader.TriggerListener {

    MeasurementTypes.MEASUREMENT_TYPES current_type = MeasurementTypes.MEASUREMENT_TYPES.VOC;
    private BarcodeReader barcodeReader;
    private ListView barcodeList;

    private String current_SEDateTime;
    private String current_ResDateTime;

    private String default_SE="Monitor";

    private Spinner spin_SE_Code;
    private Spinner spin_Equip_Code;
    private Spinner spin_User_name;

    private TextView text_event_date;
    private TextView text_event_time;
    private TextView text_resolve_date;
    private TextView text_resolve_time;

    private TextView text_Value;
    private TextView text_Unit;

    private TextView txt_comment;

    private RadioGroup rbResolved;
    private RadioButton rbND;
    private RadioButton rbDetected;


    Cursor Cursor_SE_code = null;
    ArrayList<String[]> array_SE_code = null;
    private String current_se = "";
    private String prior_current_se = "";


    Cursor Cursor_Users = null;
    ArrayList<String[]> array_Users = null;
    String current_username = "";
    String prior_current_username = "";
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
    Button btnDelete;
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
        setContentView(R.layout.activity_input_voc);

        bAcceptWarningValid = false;
        bAcceptWarningDuplicate = false;
        prior_current_equipment = "NA";

        current_site_event_reading = SiteEvents.GetDefaultReading();
        current_unit = "ppm";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            System.out.println("we have default reading");
            current_site_event_reading = (SiteEvents) getIntent().getSerializableExtra("SE");
            current_site_event_reading.setMeasurementType(current_type);
            se_table = (DataTable_SiteEvent) getIntent().getSerializableExtra("SE_TABLE");

            if (se_table == null)
                se_table = new DataTable_SiteEvent();
        }

        try {
            current_site_event_reading_copy = (SiteEvents) current_site_event_reading.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        //current_se = default_site_event_reading.getStrSE_ID();
        current_se = default_SE;
        current_site_event_reading.setMeasurementType(current_type);
        current_unit = "ppm";
        current_equipment = current_site_event_reading.getStrEq_ID();
        current_username = current_site_event_reading.getStrUserName();
        current_comment = current_site_event_reading.getStrComment();
        current_SEDateTime = current_site_event_reading.getDatSE_Date();
        current_ResDateTime = current_site_event_reading.getDatResDate();
        current_yn_resolve = current_site_event_reading.getBoolResolved();
        current_reading = current_site_event_reading.getValue();
        prior_current_username = current_username;
        // prior_current_equipment = current_equipment;
        prior_current_se = current_se;
        current_site_event_reading.setMeasurementType(current_type);


        //((TextView)findViewById(R.id.txtActivityTitle)).setText("Input Form");
        AppDataTables tables = new AppDataTables();
        tables.SetSiteEventsTablesStructure();

        dbHelper = new HandHeld_SQLiteOpenHelper(ct, tables);
        db = dbHelper.getReadableDatabase();

        int rowsInDB = dbHelper.getRowsInLookupTables(db);
        if (rowsInDB < 1) {
            AlertDialogShow("The Lookup Tables aren't populated, " +
                    "go to Menu | Download and Populate Lookup DB", "ERROR!", "warning");
        }
        maxId = dbHelper.getMaxID_FromSiteEventsTable(db);

        ///Log.i("------------onCreate", Locs.getColumnName(1));
        spin_SE_Code = (Spinner) findViewById(R.id.txt_Site_Event_Code);
        spin_Equip_Code = (Spinner) findViewById(R.id.txt_equip_id);
        spin_User_name = (Spinner) findViewById(R.id.txt_User_name);

        rbND = (RadioButton) findViewById(R.id.radio_true);
        rbDetected = (RadioButton) findViewById(R.id.radio_false);
        rbResolved = (RadioGroup) findViewById(R.id.radio_group);

        //define all controls first
        text_event_time = (TextView) findViewById(R.id.text_event_time);
        text_event_date = (TextView) findViewById(R.id.text_event_date);
        text_resolve_time = (TextView) findViewById(R.id.text_resolve_time);
        text_resolve_date = (TextView) findViewById(R.id.text_resolve_date);
        txt_comment = (EditText) findViewById(R.id.txt_comment);
        text_Unit = (TextView) findViewById(R.id.txtUnit);
        text_Value = (TextView) findViewById(R.id.txtValue);


        Log.i("codedebug", "VOC 1  " + current_site_event_reading.getYnResolved() + current_yn_resolve);

        if (current_site_event_reading.getIntResolved() == -1)
            rbResolved.clearCheck();
        else {
            if (current_yn_resolve)
                rbResolved.check(R.id.radio_true);
            else
                rbResolved.check(R.id.radio_false);
        }
        rbResolved.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);

                if (rb == rbDetected) {
                    txt_comment.setEnabled(true);
                    text_Value.setEnabled(true);
                    text_Unit.setEnabled(true);
                    current_ResDateTime = DateTimeHelper.GetDateTimeNow();
                    text_resolve_date.setText(
                            DateTimeHelper.GetStringDateFromDateTime(current_ResDateTime, ""));

                    text_resolve_time.setText(DateTimeHelper.GetStringTimeFromDateTime(current_ResDateTime, ""));

                    if (!current_equipment.equals("NA") && !Objects.equals(current_reading, ""))
                        txt_comment.setText("VOC Monitoring - "
                                + current_reading
                                + " " + current_unit);
                    else
                        txt_comment.setText("");


                } else {
                    text_Value.setText("");
                    text_Value.setEnabled(false);
                    text_Unit.setEnabled(false);
                    txt_comment.setText("VOC Monitoring - ND");

                }

            }

        });

        text_Value.setText(current_reading);
        text_Value.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /// Toast.makeText(ct, text_Value.getText(), Toast.LENGTH_SHORT).show();
            }

            public void afterTextChanged(Editable s) {
                current_reading = text_Value.getText().toString();
                current_unit = text_Unit.getText().toString();
                if (rbDetected.isChecked() && (!current_reading.equals(""))) {
                    current_comment = "VOC Monitoring - " + current_reading + " " + current_unit;
                    txt_comment.setText(current_comment);
                    isLastRecordSavedToTable = false;
                }
            }
        });
        text_Value.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Toast.makeText(ct, text_Value.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });


        text_Unit.setText(current_unit);
        text_Unit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                 current_reading = text_Value.getText().toString();
                current_unit = text_Unit.getText().toString();
                current_comment = "VOC Monitoring - " + current_reading + " " + current_unit;
                txt_comment.setText(current_comment);
                isLastRecordSavedToTable = false;
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
        Cursor_Eq = dbHelper.GetCursorEquipment(db);
        array_Eq = transferCursorToArrayList(Cursor_Eq);
        String[] from_Eq = new String[]{DataTable_Equip_Ident.strEqID};
        SimpleCursorAdapter adapter_Eq =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                        Cursor_Eq, from_Eq, toL, 0);
        adapter_Eq.setDropDownViewResource(android.R.layout.simple_spinner_item);
        //prior_current_equipment = current_equipment;
        spin_Equip_Code.setAdapter(adapter_Eq);
        SetSpinnerValue(spin_Equip_Code, array_Eq, current_equipment, 1);
        spin_Equip_Code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //CUSTOM METHOD
                spin_Equip_Code_Listener(parent, pos);

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //USERS
        Cursor_Users = dbHelper.GetCursorUsers(db);
        array_Users = transferCursorToArrayList(Cursor_Users);

        String[] from_Users = new String[]{DataTable_Users.strName};

        SimpleCursorAdapter adapter_users =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                        Cursor_Users, from_Users, toL, 0);
        adapter_users.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_User_name.setAdapter(adapter_users);
        SetSpinnerValue(spin_User_name, array_Users, current_username, 1);
        spin_User_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                current_username = GetSpinnerValue(spin_User_name);
                if ((!current_username.equals("NA"))
                        && (!current_username.equals(prior_current_username)))
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
        SetSpinnerValue(spin_SE_Code, array_SE_code, current_se, 2);
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
                System.out.println(bAcceptWarningValid);
                SaveReadingsToSiteEventRecord();
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

                prior_current_username = current_username;
                prior_current_equipment = current_equipment;
                prior_current_se = current_se;


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
                        String message = "The data (" + records + " records) is saved and ready to be uplaoded.";
                        Toast.makeText(ct, message, Toast.LENGTH_SHORT).show();
                        se_table = new DataTable_SiteEvent();
                    }
                } else
                    isRecordsSavedToDB = false;

                 if (isLastRecordSavedToTable && isRecordsSavedToDB) {
                    clearForms();
                }
                Intent intent = new Intent(ct, Activity_Main.class);
                startActivity(intent);
                finish();
                //onBackPressed();

            }
        });

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
        Log.i("CodeDebug", "spin_Equip_Code_Listener voc -> " + " " + current_equipment + current_site_event_reading.getStrEq_ID() + current_site_event_reading_copy.getStrEq_ID());

        try {
            current_site_event_reading_copy = (SiteEvents) current_site_event_reading.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        current_equipment_type = ((String[]) array_Eq.get(pos))[2];
        //prior_current_equipment=current_equipment;
        current_equipment = ((String[]) array_Eq.get(pos))[1];

        current_SEDateTime = DateTimeHelper.GetDateTimeNow();
        text_event_date.setText(DateTimeHelper.GetStringDateFromDateTime(current_SEDateTime, ""));
        text_event_time.setText(DateTimeHelper.GetStringTimeFromDateTime(current_SEDateTime, ""));

        SaveReadingsToSiteEventRecord();
        boolean b = current_site_event_reading.equalAllExceptEquipment(current_site_event_reading_copy);
        Log.i("CodeDebug", "voc -> " + b + " " + current_equipment + current_site_event_reading.getStrEq_ID() + current_site_event_reading_copy.getStrEq_ID());

//        if (!bBarcodeEquip) {
//            //strDataModComment = "Manual";
//            bBarcodeEquip = false;
//        } else {
//            //strDataModComment = "";
//            bBarcodeEquip = false;
//        }
        current_type  = MeasurementTypes.GetFrom_SE_ID(current_equipment, current_equipment_type);
        current_site_event_reading.setMeasurementType(current_type);
        if(current_equipment.startsWith("NA"))
            return;

        if ((!current_equipment.startsWith("NA") &&
                !Objects.equals(prior_current_equipment, current_equipment)) || b) {

//collect dataIntent seintent
            Intent seintent = null;
            Log.i("CodeDebug", "setting intent" + current_type);


            if (current_type == MeasurementTypes.MEASUREMENT_TYPES.GENERAL_BARCODE) {
                seintent = new Intent("android.intent.action.INPUT_GENERAL_EQ_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
            }
            if (current_type == MeasurementTypes.MEASUREMENT_TYPES.PH) {
                seintent = new Intent("android.intent.action.INPUT_PH_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
            }
            if (current_type == MeasurementTypes.MEASUREMENT_TYPES.NOISE) {
                seintent = new Intent("android.intent.action.INPUT_NOISE_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
            }
            if (current_type == MeasurementTypes.MEASUREMENT_TYPES.VOC) {
                current_SEDateTime = DateTimeHelper.GetDateTimeNow();
                text_event_time.setText(DateTimeHelper.GetStringTimeFromDateTime(current_SEDateTime, ""));
                text_event_date.setText(DateTimeHelper.GetStringDateFromDateTime(current_SEDateTime, ""));

                //seintent = new Intent("android.intent.action.INPUT_VOC_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
            }
            if (current_type == MeasurementTypes.MEASUREMENT_TYPES.OTHER) {
                seintent = new Intent("android.intent.action.SE_MAIN_INPUT_BARCODEACTIVITY");//Activity_GeneralEq_Input.class);
            }
            if (seintent != null)
                SetAndStartIntent(seintent);
        }
        bAcceptWarningValid = false;
        bAcceptWarningDuplicate = false;

        if (!Objects.equals(current_equipment, "NA")
                && !Objects.equals(current_equipment, prior_current_equipment) )
            isLastRecordSavedToTable = false;
    }

    private void SetAndStartIntent(Intent seintent) {
        Log.i("Codedebug", "SetAndStartIntent - VOC");
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
            Log.i("codedebug", "VOC SetAndStartIntent startActivity ->" + seintent.toString());
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

                //currentDateTime = Calendar.getInstance().getTime(); //
                if (!Objects.equals(tempcurrent, ""))
                    current_equipment = tempcurrent;

                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                        ct, android.R.layout.simple_list_item_1, list);

                int id = GetIndexFromArraylist(array_Eq, current_equipment, 1);

                if (id > 0) {
                    bBarcodeEquip = true;
                }
                if (id > -1) {
                    spin_Equip_Code.setSelection(id);
                    //barcodeList.setAdapter(dataAdapter);
                    isRecordsSavedToDB = false;
                    bAcceptWarningDuplicate = false;
                    bAcceptWarningValid = false;
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

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            barcodeReader.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (barcodeReader != null) {
            // unregister barcode event listener
            barcodeReader.removeBarcodeListener(this);

            // unregister trigger state change listener
            barcodeReader.removeTriggerListener(this);
            db.close();
        }
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

    private void SaveReadingsToSiteEventRecord() {

        current_comment = (String) txt_comment.getText().toString();
        current_reading = text_Value.getText().toString();
        current_site_event_reading.setValue(current_reading);
        current_site_event_reading.setUnit(text_Unit.getText().toString());
        current_equipment = GetSpinnerValue(spin_Equip_Code);

        current_username = GetSpinnerValue(spin_User_name);
        current_site_event_reading.setStrUserName(current_username);

        String userupload = dbHelper.GetUserUploadName(db, current_username);
        if (userupload == null || (!userupload.equals("")))
            current_site_event_reading.setStrUserUploadName(userupload);

        current_se = GetSpinnerValue(spin_SE_Code);
        String desc = dbHelper.GetEqDescDB(db, current_equipment);
        if (desc == null || (!desc.equals("")))
            current_site_event_reading.setStrEqDesc(desc);

        current_site_event_reading.setStrComment(current_comment);
        current_site_event_reading.setStrEq_ID(current_equipment);
        current_site_event_reading.setStrSE_ID(current_se);

        String temp1 = text_event_date.getText().toString();
        String temp2 = text_event_time.getText().toString();

        current_site_event_reading.setDatSE_Date(DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1, temp2));
        temp1 = text_resolve_date.getText().toString();
        temp2 = text_resolve_time.getText().toString();
        current_site_event_reading.setDatResDate(DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1, temp2));


        if (rbDetected.isChecked()) {
            current_yn_resolve = true;
            current_site_event_reading.setYnResolved("true");
        }
        if (rbND.isChecked()) {
            current_yn_resolve = false;
            current_site_event_reading.setYnResolved("false");
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


    public void clearForms() {

        txt_comment.setText("");
        int id = GetIndexFromArraylist(array_Eq, "NA", 1);
        current_equipment = "NA";
        spin_Equip_Code.setSelection(id);
        id = GetIndexFromArraylist(array_SE_code, default_SE, 1);
        current_se = default_SE;
        spin_SE_Code.setSelection(id);

        //rbDetected.setChecked(false);
        //rbND.setChecked(true);
        text_Value.setText("");
        text_Unit.setText("ppm");
        rbND.setChecked(true);
        txt_comment.setText("");

        bBarcodeEquip = false;
        isLastRecordSavedToTable = true;
        Log.i("codedebug", " clear forms " + isLastRecordSavedToTable.toString());

        spin_Equip_Code.requestFocus();
    }

    public String GetSpinnerValue(Spinner spinner) {
        TextView textView = (TextView) spinner.getSelectedView();
        String text = textView.getText().toString();

        return text;

    }

    public Validation saveForms(boolean bAcceptWarning, boolean bAcceptWarningDuplicate) {

        current_site_event_reading.setLngID((int) (new Date().getTime() / 1000));

        Validation isTheRecordValid = Activity_Main_Input.IsRecordValid(current_site_event_reading,
                spin_Equip_Code,
                spin_SE_Code, spin_User_name, null);
        Validation isTheRecordDup = Activity_Main_Input.IsRecordDup(db, dbHelper, current_site_event_reading, se_table);

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


            AlertDialog.Builder builder = new AlertDialog.Builder(this, themeResId);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            AlertDialog ad = builder.create();
            if (ad != null) {
                ad.dismiss();
            }
            assert ad != null;
            ad.show();

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
                String dt = DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1, temp2);

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

    private void text_resolve_time_picker() {
        text_resolve_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String temp1 = text_resolve_date.getText().toString();
                String temp2 = text_resolve_time.getText().toString();
                String dt = DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1, temp2);

                final Calendar mcurrentTime =
                        DateTimeHelper.GetCalendarFromDateTime(dt, "");

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
                String temp1 = text_resolve_date.getText().toString();
                String temp2 = text_resolve_time.getText().toString();
                String dt = DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1, temp2);
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
                String temp1 = text_event_date.getText().toString();
                String temp2 = text_event_time.getText().toString();
                String dt = DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1, temp2);
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

    }
    public void SetSpinnerValue(Spinner spinner, ArrayList<String[]> strValues, String strValue, int iDataColumn) {
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


}