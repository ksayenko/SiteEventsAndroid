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
import android.view.KeyEvent;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class Activity_VOC_Edit extends AppCompatActivity {

    MeasurementTypes.MEASUREMENT_TYPES current_type = MeasurementTypes.MEASUREMENT_TYPES.VOC;

    private String current_SEDateTime;
    private String current_ResDateTime;

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


    Cursor Cursor_Users = null;
    ArrayList<String[]> array_Users = null;
    String current_maintenance = "";
    Cursor Cursor_Eq = null;
    ArrayList<String[]> array_Eq = null;
    String current_equipment = "";
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

    Boolean[] bDialogChoice = {false};

    private Boolean isLastRecordSavedToTable = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_voc);

        bAcceptWarningValid = false;
        bAcceptWarningDuplicate = false;
        //Toast.makeText(ct, ct.getClass().getName(), Toast.LENGTH_SHORT).show();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            System.out.println("we have default reading");
            current_site_event_reading = (SiteEvents) getIntent().getSerializableExtra("SE");

        }
        try {
            current_site_event_reading_copy  = (SiteEvents) current_site_event_reading.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        current_se = current_site_event_reading.getStrSE_ID();
        current_equipment = current_site_event_reading.getStrEq_ID();
        current_maintenance = current_site_event_reading.getStrM_Per_FirstLastName();
        current_comment = current_site_event_reading.getStrComment();
        current_SEDateTime = current_site_event_reading.getDatSE_Date();
        current_ResDateTime = current_site_event_reading.getDatResDate();
        current_se = current_site_event_reading.getStrSE_ID();
        current_reading = current_site_event_reading.getValue();
        current_unit = current_site_event_reading.getUnit();
        current_yn_resolve = current_site_event_reading.getBoolResolved();

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
        spin_Equip_Code.setEnabled(false);
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
        text_Unit =(TextView) findViewById(R.id.txtUnit);
        text_Value= (TextView) findViewById(R.id.txtValue);


        rbResolved.clearCheck();

        if (current_yn_resolve)
            rbDetected.setChecked(true);
        else
            rbND.setChecked(true);

        rbResolved.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);

                if (rb == rbDetected) {

                    text_Value.setEnabled(true);
                    text_Unit.setEnabled(true);
                    current_ResDateTime = DateTimeHelper.GetDateTimeNow();
                    text_resolve_date.setText(
                            DateTimeHelper.GetStringDateFromDateTime(current_ResDateTime, ""));

                    text_resolve_time.setText(   DateTimeHelper.GetStringTimeFromDateTime(current_ResDateTime, ""));

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
                    if (!current_equipment.equals("NA"))
                        txt_comment.setText("VOC Monitoring - ND");
                    else
                        txt_comment.setText("");
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
                current_comment = "VOC Monitoring - " + current_reading + " "+current_unit;
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
        SetSpinnerValue(spin_Equip_Code, array_Eq, current_equipment,1);


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
                 current_comment = txt_comment.getText().toString();
                if (!current_comment .equals(""))
                    isLastRecordSavedToTable = false;

            }
        });


        btnClear = (Button) findViewById(R.id.btn_clear);
        btnClear.setText("Delete");
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ct);
                alert.setTitle("Delete entry");
                alert.setMessage("Are you sure you want to delete this record ? ");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dbHelper.deleteRecords(db, current_site_event_reading.getLngID().toString());

                        Intent intent = new Intent(ct, Activity_EditListSE.class);
                        startActivity(intent);
                        finish();

                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
            }


        });

        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setText("Update");
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(bAcceptWarningValid);
                SaveReadingsToSiteEventRecord();
                Validation iChecked = saveForms(bAcceptWarningValid, bAcceptWarningDuplicate);
                if (iChecked.isValid() ||
                        (iChecked.isWarning() && bAcceptWarningValid)
                        || (iChecked.isWarningDuplicate() && bAcceptWarningDuplicate))
                {
                    dbHelper.getUpdateSiteEvent(db, current_site_event_reading);

                    AlertDialog.Builder alert = new AlertDialog.Builder(ct);
                    alert.setTitle("Success!");
                    alert.setMessage("The Record got Updated for Equipment " + current_site_event_reading.getStrEq_ID());
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    alert.show();
                }

                System.out.println(bAcceptWarningValid);
                System.out.println(iChecked);
            }
        });


        btnDone = (Button) findViewById(R.id.btn_done);
        btnDone.setText("Cancel");
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ct, Activity_Main.class);
                startActivity(intent);
                finish();
                //onBackPressed();

            }
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }


//    private void SetAndStartIntent(Intent seintent) {
//        Log.i("SetAndStartIntent", "SetAndStartIntent - VOC");
//        seintent.putExtra("SE", current_site_event_reading);
//        seintent.putExtra("SE_TABLE", se_table );
//        seintent.putExtra("USER", current_maintenance );
//        if (!isLastRecordSavedToTable) {
//            isLastRecordSavedToTable = true;
//            AlertDialogHighWarning("The record has not been saved." + "\n" + "Hit Done or Back button again to exit without saving.", "Warning!");
//        } else
//            startActivity(seintent);
//    }



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
        //note that dates and times saved in the events
        current_comment = (String) txt_comment.getText().toString();
        current_reading = text_Value.getText().toString()  ;
        current_site_event_reading.setValue(current_reading);
        current_site_event_reading.setUnit(text_Unit.getText().toString());


        current_site_event_reading.setMeasurementType(MeasurementTypes.MEASUREMENT_TYPES.VOC);
        current_maintenance =GetSpinnerValue(spin_User_name);
        current_se =GetSpinnerValue(spin_SE_Code);
        String desc = dbHelper.GetEqDescDB(db,current_equipment);
        if(desc == null || (!desc.equals("")))
            current_site_event_reading.setStrEqDesc(desc);

        current_site_event_reading.setStrComment(current_comment);
        current_site_event_reading.setStrM_Per_FirstLastName(current_maintenance);
        String initials = dbHelper.GetMaintenanceInitialsByFirstLastName(db,current_maintenance);
        if(initials == null || (!initials.equals("")))
            current_site_event_reading.setStrM_Per_ID(initials);
        current_site_event_reading.setStrEq_ID(current_equipment);
        current_site_event_reading.setStrSE_ID(current_se);

        String temp1 = text_event_date.getText().toString();
        String temp2 = text_event_time.getText().toString();

        current_site_event_reading.setDatSE_Date(DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1,temp2));
        temp1 = text_resolve_date.getText().toString();
        temp2 = text_resolve_time.getText().toString();
        current_site_event_reading.setDatResDate(DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1,temp2));


        if (rbDetected.isChecked()) {
            current_yn_resolve = true;
            current_site_event_reading.setYnResolved("true");
        } else {
            current_yn_resolve = false;
            current_site_event_reading.setYnResolved("false");
        }
        isLastRecordSavedToTable = current_site_event_reading.equals(current_site_event_reading_copy);

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
        spin_Equip_Code.setSelection(id);
        id = GetIndexFromArraylist(array_SE_code, "Monitor", 1);
        spin_SE_Code.setSelection(id);
        //rbDetected.setChecked(false);
        //rbND.setChecked(true);
        text_Value.setText("");
        text_Unit.setText("ppm");
        txt_comment.setText("");

        bBarcodeEquip = false;
        isLastRecordSavedToTable = true;
        spin_Equip_Code.requestFocus();
    }
    public String GetSpinnerValue(Spinner spinner) {
        TextView textView = (TextView)spinner.getSelectedView();
        String text = textView.getText().toString();

        return text;

    }
    public Validation saveForms(boolean bAcceptWarning, boolean bAcceptWarningDuplicate) {

        Validation isTheRecordValid = Activity_Main_Input.IsRecordValid(current_site_event_reading,
                spin_Equip_Code,
                spin_SE_Code, spin_User_name, null);
        //Validation isTheRecordDup = Activity_Main_Input.IsRecordDup(db, dbHelper,current_site_event_reading, se_table);

        //boolean bAcceptDup = isTheRecordDup.isValid() || (isTheRecordDup.isWarningDuplicate() && bAcceptWarningDuplicate);
        boolean bAcceptRecord = isTheRecordValid.isValid() || (isTheRecordValid.isWarning() && bAcceptWarning);

        if (isTheRecordValid.isError()) {
            AlertDialogShowError(isTheRecordValid.getValidationMessage(), "ERROR");
        } else if (isTheRecordValid.isWarning() && !bAcceptWarning) {
            AlertDialogShow("Please check\n" + isTheRecordValid.getValidationMessage() + "\nPress 'Save' one more time to confirm the data as VALID or update the input data.", "Warning");
        }
//        else if (isTheRecordDup.isWarningDuplicate() && !bAcceptWarningDuplicate) {
//            AlertDialogHighWarning("Please check\n" + isTheRecordDup.getValidationMessage() + "\nPress 'Save' one more time to confirm the data as VALID or update the input data.", "Warning");
//            return isTheRecordDup;
//        } else if ((bAcceptRecord) && (bAcceptDup)) {
//            System.out.println(isTheRecordValid.getValidationMessageWarning() + isTheRecordValid.getValidationMessageError());
//            maxId = se_table.AddToTable(current_site_event_reading);
//            isRecordsSavedToDB = false;
//            maxId++;
//            clearForms();
//            System.out.println("NEW max id " + maxId.toString());
//        }

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


            AlertDialog.Builder builder = new AlertDialog.Builder(this, themeResId);
            builder .setTitle(title);
            builder   .setMessage(message);
            builder   .setPositiveButton(button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            AlertDialog ad = builder.create();
            if (ad != null) { ad.dismiss(); }
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

    private void text_resolve_time_picker() {
        text_resolve_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String temp1 = text_resolve_date.getText().toString();
                String temp2 = text_resolve_time.getText().toString();
                String dt =DateTimeHelper.GetStringDateTimeFromDateAndTime(temp1,temp2);

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


}