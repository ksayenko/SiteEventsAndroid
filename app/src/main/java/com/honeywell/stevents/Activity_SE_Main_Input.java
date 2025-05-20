package com.honeywell.stevents;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Activity_SE_Main_Input extends AppCompatActivity
        implements BarcodeReader.BarcodeListener,
        BarcodeReader.TriggerListener {


    //public enum VALIDATION {VALID,ERROR,WARNING}
    private BarcodeReader barcodeReader;
    private ListView barcodeList;

    SiteEvents input_reading;

    private Context mContext;
    private Date currentDateTime;

    private Spinner spin_SE_Code;
    private Spinner spin_Equip_Code;
    private Spinner spin_User_name;

    private TextView text_event_date;
    private TextView text_event_time;
    private TextView text_resolve_date;
    private TextView text_resolve_time;

    private TextView txt_comment;

    private RadioGroup rbResloved ;
    private RadioButton rbTrue ;
    private RadioButton rbFalse ;



    Cursor Cursor_SE_code = null;
    ArrayList<String[]> array_SE_code = null;
    private String current_se = "";
    int iCol_se_id = 1;
    int iCol_se_desq = 2;
    Cursor Cursor_Users = null;
    ArrayList<String[]> array_Users = null;
    String curent_username = "";

    Cursor Cursor_Eq = null;
    ArrayList<String[]> array_Eq = null;
    String current_equipment = "";
    int iCol_eq_id = 1;
    int iCol_eq_type = 2;

    boolean bresolve=true;

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


    private SiteEvents default_reading;
    private DataTable_SiteEvent se_table = new DataTable_SiteEvent();

    Boolean[] bDialogChoice = {false};

    private Boolean isLastRecordSavedToTable = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bAcceptWarningValid = false;
        bAcceptWarningDuplicate = false;
        mContext = this;

        input_reading = new SiteEvents();
        default_reading = SiteEvents.GetDefaultReading();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            System.out.println("we have default reading");
            default_reading = (SiteEvents) getIntent().getSerializableExtra("SE");
        } else {
            System.out.println("no default reading");
            default_reading = SiteEvents.GetDefaultReading();
        }

        current_se = default_reading.getStrSE_ID();
        current_equipment = default_reading.getStrEq_ID();
        curent_username = default_reading.getStrUserName();
        current_comment = default_reading.getStrComment();



        Log.i("------------onCreate Activity_SE_Main_Input", "10");
        //super.onCreate(savedInstanceState);
        Log.i("------------onCreate Activity_SE_Main_Input", "1");
        setContentView(R.layout.activity_input_main_se);

        //((TextView)findViewById(R.id.txtActivityTitle)).setText("Input Form");
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

        String selectedDate = DateTimeHelper.GetDateTimeNow();
        default_reading.setDatResDate(selectedDate);
        default_reading.setDatSE_Date(selectedDate);

        rbTrue =(RadioButton) findViewById(R.id.radio_true);
        rbFalse =(RadioButton) findViewById(R.id.radio_false);
        if (default_reading.getYnResolved()=="true")
            rbTrue.setChecked(true);
        else rbFalse.setChecked(false);
        rbTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbTrue.isChecked())
                    bresolve = true;
                else
                    bresolve = false;
            }
        }) ;



        String aDate = DateTimeHelper.GetDateFromDateTime(selectedDate,"");
        String aTime = DateTimeHelper.GetTimeFromDateTime(selectedDate,"");

        //text_event_time
        text_event_time =(TextView)findViewById(R.id.text_event_time);
        text_event_time.setText(aTime);
        text_event_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mContext,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                if (selectedHour < 12 )
                                    text_event_time.setText( selectedHour + ":" + selectedMinute + " am");
                                else
                                    text_event_time .setText( selectedHour-12 + ":" + selectedMinute + " pm");
                            }
                        }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        //text_event_date
        text_event_date =(TextView)findViewById(R.id.text_event_date);
        text_event_date.setText(aDate);
        text_event_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar mcurrentTime = Calendar.getInstance();
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentTime.get(Calendar.MONTH);
                int year = mcurrentTime.get(Calendar.YEAR);


                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(mContext,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet (DatePicker dPicker, int year,
                                                       int month, int day) {

                                text_event_date.setText( month + "/" + day + "/"+ year);

                            }
                        },year, month, day);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();

            }
        });
        text_event_time =(TextView)findViewById(R.id.text_event_time);
        text_event_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mContext,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                if (selectedHour < 12 )
                                    text_event_time.setText( selectedHour + ":" + selectedMinute + " am");
                                else
                                    text_event_time .setText( selectedHour-12 + ":" + selectedMinute + " pm");
                            }
                        }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        //resolve date
        text_resolve_date =(TextView)findViewById(R.id.text_resolve_date);
        text_resolve_date.setText(aDate);
        text_resolve_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar mcurrentTime = Calendar.getInstance();
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentTime.get(Calendar.MONTH);
                int year = mcurrentTime.get(Calendar.YEAR);


                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(mContext,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet (DatePicker dPicker, int year,
                                                   int month, int day) {

                                text_resolve_date.setText( month + "/" + day + "/"+ year);

                            }
                        },year, month, day);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();

            }
        });
        //text_resolve_time
        text_resolve_time = (TextView) findViewById(R.id.text_resolve_time);
        text_resolve_time.setText(aTime);
        text_resolve_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mContext,
                        new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (selectedHour < 12 )
                            text_resolve_time.setText( selectedHour + ":" + selectedMinute + " am");
                        else
                            text_resolve_time .setText( selectedHour-12 + ":" + selectedMinute + " pm");
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        // needed for spinner
        int[] toL = new int[]{android.R.id.text1};
        //Populate Spinners (comboboxes)
        // equipment
        Cursor_Eq = dbHelper.GetCursorEquipment(db);
        array_Eq = transferCursorToArrayList(Cursor_Eq);
        String[] from_Eq= new String[]{DataTable_Equip_Ident.strEqTypeID};
        SimpleCursorAdapter adapter_Eq =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                        Cursor_Eq, from_Eq, toL, 0);
        adapter_Eq.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_Equip_Code.setAdapter(adapter_Eq);

        Cursor_Users = dbHelper.GetCursorUsers(db);
        array_Users = transferCursorToArrayList(Cursor_Users);

        String[] from_Users= new String[]{DataTable_Users.strUserName};

        SimpleCursorAdapter adapter_users=
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                        Cursor_Users, from_Users, toL, 0);
        adapter_users.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_User_name.setAdapter(adapter_users);

        //Site event types
        Cursor_SE_code = dbHelper.GetCursorSECode(db);
        array_SE_code = transferCursorToArrayList(Cursor_SE_code);

        String[] from_SE_CODE= new String[]{DataTable_Site_Event_Def.strSE_Desc};

        SimpleCursorAdapter adapter_SE_code =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                        Cursor_SE_code, from_SE_CODE, toL, 0);
        adapter_SE_code.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin_SE_Code.setAdapter(adapter_SE_code);

//        txt_Reading.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            public void afterTextChanged(Editable s) {
//                bAcceptWarningValid = false;
//                bAcceptWarningDuplicate = false;
//                isLastRecordSavedToTable = false;
//                Log.i("isLastRecordSavedToTable", "txt_Reading.addTextChangedListener " + isLastRecordSavedToTable.toString());
//            }
//        });



//        text_event_time.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
////                FragmentManager fm1=getSupportFragmentManager();
////                picktime = new TimePickerFragment();
////                picktime.show(fm,"timeoicker");
//
//                        //(fm, "TimePicker");
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.charAt(s.length() - 1) == '\n') {
//                    Log.d("TAG", "Enter was pressed");
//                }
//            }}
//            );



        txt_comment = (EditText) findViewById(R.id.txt_comment);
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
                }
                else
                    isRecordsSavedToDB = false;

                Log.i("isLastRecordSavedToTable", "btnDone.setOnClickListener ,isLastRecordSavedToTable " + isLastRecordSavedToTable.toString());
                if (isLastRecordSavedToTable && isRecordsSavedToDB ) {
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
        spin_Equip_Code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);

                String desc = ((String[]) array_Eq.get(pos))[2];
                current_se = ((String[]) array_Eq.get(pos))[1];

                if (!bBarcodeEquip) {
                    //strDataModComment = "Manual";
                    bBarcodeEquip = false;
                } else {
                    //strDataModComment = "";
                    bBarcodeEquip = false;
                }


                bAcceptWarningValid = false;
                bAcceptWarningDuplicate = false;

                if (!Objects.equals(current_se, "NA"))
                    isLastRecordSavedToTable = false;

                Log.i("isLastRecordSavedToTable", "spin_loc_id.listener isLastRecordSavedToTable:" + isLastRecordSavedToTable.toString());
                Log.i("isLastRecordSavedToTable", "spin_loc_id.listener isRecordsSavedToDB:" + isRecordsSavedToDB.toString());
                Log.i("current_se", "spin_loc_id.listener current_se" + current_se);
            }

            public void onNothingSelected(AdapterView<?> parent) {

                Log.i("isLastRecordSavedToTable", "spin_loc_id.listener onNothingSelected isLastRecordSavedToTable:" + isLastRecordSavedToTable.toString());
            }
        });


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

//        SimpleCursorAdapter adLocs =
//                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Locs, fromLoc, toL, 0);
//        adLocs.setDropDownViewResource(android.R.layout.simple_spinner_item);
//        spin_Equip_Code.setAdapter(adLocs);
//        spin_Equip_Code.setSelection(0);
//
//        SimpleCursorAdapter adFO =
//                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Fac_Oper_Status, fromFO, toL, 0);
//        adFO.setDropDownViewResource(android.R.layout.simple_spinner_item);
//        spin_FAC_OP.setAdapter(adFO);
//
//        SimpleCursorAdapter adEO =
//                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Eq_Oper_Status, fromEO, toL, 0);
//        adEO.setDropDownViewResource(android.R.layout.simple_spinner_item);
//        spin_EQ_OP.setAdapter(adEO);
//
//        SimpleCursorAdapter adU =
//                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Equip, fromU, toL, 0);
//        adU.setDropDownViewResource(android.R.layout.simple_spinner_item);
//        spin_UNITS.setAdapter(adU);
//
//        SimpleCursorAdapter adelev =
//                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, Elev, fromEl, toL, 0);
//        adelev.setDropDownViewResource(android.R.layout.simple_spinner_item);
//        spin_elev_code.setAdapter(adelev);


        // set lock the orientation
        // otherwise, the onDestory will trigger when orientation changes
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // get bar code instance from MainActivity
        barcodeReader = Activity_Main.getBarcodeObject();

        if (barcodeReader != null) {

            // register bar code event listener
            barcodeReader.addBarcodeListener(this);
            Log.i("onCreate", "barcodeReader !=null");

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
        Log.i("barcodeList", barcodeList.toString());
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // update UI to reflect the data
                Log.i("onBarcodeEvent", "onBarcodeEvent!!!!");
                List<String> list = new ArrayList<>();
                String tempcurrent_loc = event.getBarcodeData();
                currentDateTime = Calendar.getInstance().getTime(); //
                if (tempcurrent_loc != null || tempcurrent_loc != "")
                    current_se = tempcurrent_loc;
                //current_se = event.getBarcodeData();
                Log.i("onBarcodeEvent", getCurrent_Equipment());

                isLastRecordSavedToTable = false;

                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                        Activity_SE_Main_Input.this, android.R.layout.simple_list_item_1, list);

                int id = getIndexFromArraylist(array_Eq, getCurrent_Equipment(), 1);

                Log.i("onBarcodeEvent", "id: " + Integer.toString(id));
                if (id > 0) {
                    bBarcodeEquip = true;
                }
                //spin_Loc_id.setSelection(id);
                barcodeList.setAdapter(dataAdapter);
                isRecordsSavedToDB = false;
                bAcceptWarningDuplicate = false;
                bAcceptWarningValid = false;

                Log.i("isLastRecordSavedToTable", "on barcode event isLastRecordSavedToTable " + isLastRecordSavedToTable.toString());
                Log.i("isLastRecordSavedToTable", "on barcode event isRecordsSavedToDB " + isRecordsSavedToDB.toString());
            }
        });
    }

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
    @Override
    public void onTriggerEvent(TriggerStateChangeEvent event) {
        try {
            // only handle trigger presses
            // turn on/off aimer, illumination and decoding
            Log.i("onTriggerEvent", "no Data");
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
                Log.i("no data", "no Data");
                if (Objects.equals(current_equipment, "NA")) {
                    int id = getIndexFromArraylist(array_Eq, "NA", 1);

                    Log.i("onFailureEvent", "Id = "+Integer.toString(id));

                    bBarcodeEquip = false;
                    //spin_Loc_id.setSelection(id);
                    //barcodeList.setAdapter(dataAdapter);
                    Toast.makeText(Activity_SE_Main_Input.this, "No data yet", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Activity_SE_Main_Input.this, current_equipment, Toast.LENGTH_SHORT).show();

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

    public String getCurrent_Equipment() {
        return current_equipment;
    }

    public void setCurrent_loc(String current_loc) {
        this.current_se = current_loc;
    }

    public void clearForms() {

        txt_comment.setText("");

        int id = 0;
        id = getIndexFromArraylist(array_Eq, "NA", 1);
//        Log.i("clearForms " , "id " + Integer.toString(id));
//        //spin_Loc_id.setSelection(id);
//        //id = getIndexFromArraylist(array_SE, "NA", 1);
//        //spin_COL_ID.setSelection(id);
//        id = getIndexFromArraylist(alFac_Oper_Status, "NA", 1);
//        spin_FAC_OP.setSelection(id);
//        id = getIndexFromArraylist(alEq_Oper_Status, "NA", 1);
//        spin_EQ_OP.setSelection(id);
//        id = getIndexFromArraylist(array_Eq, "NA", 1);
//        spin_UNITS.setSelection(id);

        bBarcodeEquip = false;
        isLastRecordSavedToTable = true;
        Log.i("isLastRecordSavedToTable", " clear forms " + isLastRecordSavedToTable.toString());

        //txt_Reading.requestFocus();
    }

    public Validation saveForms(boolean bAcceptWarning, boolean bAcceptWarningDuplicate) {

        input_reading.setLngID((int) (new Date().getTime() / 1000));

        currentDateTime = Calendar.getInstance().getTime();
        //adding seconds April 2023. KS
        //default sqlllite format YYYY-MM-DD HH:MM:SS
        String timestamp1 = new SimpleDateFormat(DataTable_SiteEvent.Datetime_pattern_default).format(currentDateTime);
        String timeStamp = new SimpleDateFormat(DataTable_SiteEvent.Datetime_pattern_with_sec).format(currentDateTime);

//        TextView temp;
//        temp = (TextView) spin_Loc_id.getSelectedView();
//        current_se = temp.getText().toString();
//        input_reading.setStrD_Loc_ID(current_se);
//        input_reading.setDatIR_Date(timeStamp);
//
//        temp = (TextView) spin_COL_ID.getSelectedView();
//        current_equipment = temp.getText().toString();
//        input_reading.setStrD_Col_ID(current_equipment);
//        default_reading.setStrD_Col_ID(current_equipment);//saving the last collector id
//
//        temp = (TextView) spin_EQ_OP.getSelectedView();
//        curent_username = temp.getText().toString();
//        input_reading.setStrEqO_StatusID(curent_username);
//
//        temp = (TextView) spin_FAC_OP.getSelectedView();
//        curent_fo = temp.getText().toString();
//        input_reading.setStrFO_StatusID(curent_fo);
//
//        temp = (TextView) spin_UNITS.getSelectedView();
//        current_unit = temp.getText().toString();
//        input_reading.setStrIR_Units(current_unit);
//
//        current_reading = txt_Reading.getText().toString();
//        input_reading.setDblIR_Value(current_reading);
//
//        current_comment = txt_comment.getText().toString();
//        input_reading.setStrComment(current_comment);
//
//
//        temp = (TextView) spin_elev_code.getSelectedView();
//        curent_elevationcode = temp.getText().toString();
//        input_reading.setElev_code(curent_elevationcode);
//
//
       Validation isTheRecordValid = isRecordValid();
        Validation isTheRecordDup = isRecordDup();

//        boolean bAcceptDup = isTheRecordDup.isValid() || (isTheRecordDup.isWarningDuplicate() && bAcceptWarningDuplicate);
//        boolean bAcceptRecord = isTheRecordValid.isValid() || (isTheRecordValid.isWarning() && bAcceptWarning);

//        if (isTheRecordValid.isError()) {
//            AlertDialogShowError(isTheRecordValid.getValidationMessage(), "ERROR");
//        } else if (isTheRecordValid.isWarning() && !bAcceptWarning) {
//            AlertDialogShow("Please check\n" + isTheRecordValid.getValidationMessage() + "\nPress 'Save' one more time to confirm the data as VALID or update the input data.", "Warning");
//        } else if (isTheRecordDup.isWarningDuplicate() && !bAcceptWarningDuplicate) {
//            AlertDialogHighWarning("Please check\n" + isTheRecordDup.getValidationMessage() + "\nPress 'Save' one more time to confirm the data as VALID or update the input data.", "Warning");
//            return isTheRecordDup;
//        } else if ((bAcceptRecord) && (bAcceptDup)) {
//            System.out.println(isTheRecordValid.getValidationMessageWarning() + isTheRecordValid.getValidationMessageError());
//            maxId = se_table.AddToTable(input_reading);
//            isRecordsSavedToDB = false;
//            maxId++;
//            clearForms();
//            System.out.println("NEW max id " + maxId.toString());
//        }

        return isTheRecordValid;
    }

//    public static void getTimePicker(Context context, final int position, final PickerClickListener pickerClickListener) {
//        final Dialog dialog = new Dialog(context);
//        dialog.setContentView(R.layout.dialog_time);
//        dialog.show();
//        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
//        timePicker.setIs24HourView(false);
//        Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);
//        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
//        buttonCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        buttonOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm");
//                    SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a");
//
//                    String strTime = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
//                    Date time = sdf24.parse(strTime);
//                    String formattedTime = sdf12.format(time);
//                    Log.d(TAG, "formattedTime: " + formattedTime);
//                    pickerClickListener.onTimeSelect(formattedTime, position);
//                    dialog.dismiss();
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        });
//    }

    private Validation isRecordDup() {
        Validation isValidPotentialDups = new Validation();

        //check the database
        String datestamp1 = new SimpleDateFormat(DataTable_SiteEvent.Datetime_pattern_dateonly).format(currentDateTime);
        String sql = DataTable_SiteEvent.PotentialNewDups(current_se, datestamp1);
        String scount = dbHelper.GeneralQueryFirstValue(db, sql);
        //check the inner table se_table
        boolean bPotDup2 = se_table.IsPotentialDuplicateInInnerTable(current_se, datestamp1);

        if ((!Objects.equals(scount, "") && !Objects.equals(scount, "0")) || bPotDup2) {
            isValidPotentialDups.setValidation(Validation.VALIDATION.WARNING_DUPLICATE);
            isValidPotentialDups.setValidationMessageWarning("Potential Duplicate Found! \nLocation : " + current_se);
            isValidPotentialDups.setFocus(Validation.FOCUS.LOCATION);
        }
        return isValidPotentialDups;
    }

    private Validation isRecordValid() {
        String message = "";
        String[] focus = new String[]{""};
        Validation isValid = new Validation();

        double reading;
        try {
            reading = Double.parseDouble(current_reading);
        } catch (Exception ex) {
            reading = 0.0;
        }

        isValid = input_reading.isRecordValid();

//        if (isValid.getValidation() != Validation.VALIDATION.VALID) {
//            if (isValid.getFocus() == Validation.FOCUS.READING)
//                txt_Reading.requestFocus();
//            else if (isValid.getFocus() == Validation.FOCUS.LOCATION)
//                spin_Loc_id.requestFocus();
//            else if (isValid.getFocus() == Validation.FOCUS.COLLECTOR)
//                spin_COL_ID.requestFocus();
//            else if (isValid.getFocus() == Validation.FOCUS.ELEVATION)
//                spin_elev_code.requestFocus();
 //       }

        return isValid;
    }


    public DataTable_SiteEvent getIr_table() {
        return se_table;
    }

    public void setIr_table(DataTable_SiteEvent ir_table) {
        this.se_table = ir_table;
    }

    private boolean isNA(String sValue) {
        boolean isna = sValue == null || sValue.equals("") || sValue.equalsIgnoreCase("NA");
        return isna;
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
            AlertDialogHighWarning("Do you want to Exit Input Form Without Saving?"+ "\n" +"Hit Done to Save or Back button again to exit without saving.", "Warning!");
        }

        if (!isLastRecordSavedToTable) {
            isLastRecordSavedToTable = true;
            AlertDialogHighWarning("The record has not been saved."+ "\n" +"Hit Done or Back button again to exit without saving.", "Warning!");
        }


        Log.i("isLastRecordSavedToTable ", "onBackPressed isLastRecordSavedToTable AFTER " + isLastRecordSavedToTable.toString());
        Log.i("isLastRecordSavedToTable ", " onBackPressed isRecordsSavedToDB AFTER " + isRecordsSavedToDB.toString());
    }

}