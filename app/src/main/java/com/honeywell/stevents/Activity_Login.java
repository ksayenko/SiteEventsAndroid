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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Activity_Login extends Activity {
    private EditText txt_UserName;
    private EditText txt_Password;
    String name;
    String pwd;
    String encryptedPassword;
    Button btnDone;
    Button btnCheck;
    Button btnLogout;
    CallWebServices2 restFull;

    DataTable_LoginInfo loginInfo = new DataTable_LoginInfo();
    public HandHeld_SQLiteOpenHelper dbHelper;
    public SQLiteDatabase db;
    Context ct = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        //---------------
        AppDataTables tables = new AppDataTables();
        tables.SetSiteEventsTablesStructure();

        dbHelper = new HandHeld_SQLiteOpenHelper(ct, tables);
        db = dbHelper.getReadableDatabase();

        btnDone = findViewById(R.id.btnSaveLogin);
        btnCheck = findViewById(R.id.btnCheckConnectivity);
        btnLogout = findViewById(R.id.btnLogOut);

        txt_Password = findViewById(R.id.editTextPassword);
        txt_UserName = findViewById(R.id.editName);


        String[] credentials = dbHelper.getLoginInfo(db);
        if (!credentials[0].equals("")) {
            name = credentials[0];
            encryptedPassword = credentials[1];
            try {
                pwd = Application_Encrypt.decrypt(encryptedPassword);
                System.out.println(pwd);
            } catch (Exception e) {
                e.printStackTrace();
                pwd = "";
            }
        } else {
            name = "Fill the username";
            pwd = "";
        }
        txt_UserName.setText(name);
        txt_Password.setText(pwd);

        restFull = new CallWebServices2(null);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = txt_UserName.getText().toString();
                pwd = txt_Password.getText().toString();
                CheckLogin(false, true);

            }
        });


        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckLogin(true, false);
            }


        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bLogout();
            }
        });

    }

    private boolean CheckLogin(boolean ShowMessage, boolean BSave) {
        final boolean[] bReturnValue = {false};
        final String[] sResult = {""};
        final boolean[] bSaveLogin = {BSave};
        final boolean[] bShowMessage = {ShowMessage};
        ExecutorService executor = Executors.newFixedThreadPool(5);
        final Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Background work here
                try {
                    sResult[0] = doInBackground();
                    if (sResult[0].equalsIgnoreCase("true"))
                        bReturnValue[0] = true;
                    Log.i("Rest API", "KS :: After  doInBackground();" + sResult[0]);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //UI Thread work here
                            onPostExecute(sResult[0]);
                        }

                        private void onPostExecute(String result) {
                            Log.i("Rest API", "onPostExecute" + result);

                            ///  DECLARE DIALOGS -----------------------------------------

                            final AlertDialog.Builder adError = new AlertDialog.Builder(ct, R.style.AlertDialogWarning)
                                    .setTitle("error")
                                    .setMessage("The Login Info is incorrect. Can't save. Try again.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                        }
                                    });
                            final AlertDialog.Builder adSaved = new AlertDialog.Builder(ct, R.style.AlertDialogTheme)
                                    .setTitle("Do You Want To Save Login Info?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            Boolean b = updateDBWithNewInformation();
                                            if (b) {
                                                Toast.makeText(ct, "Updated ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.cancel();
                                        }
                                    });

                            final AlertDialog.Builder adOK = new AlertDialog.Builder(ct, R.style.AlertDialogWarning)
                                    .setTitle("OK")
                                    .setMessage("The Login Info is Correct.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }
                                    });
                            //-------------------------------------------------------------------//
                            if (bShowMessage[0]) {
                                if (sResult[0].equalsIgnoreCase("true")) {
                                    AlertDialog dialog = adOK.create();
                                    dialog.show();
                                    dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
                                } else {
                                    AlertDialog dialog = adError.create();
                                    dialog.show();
                                    dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
                                }
                            }

                            if (sResult[0].toLowerCase() == "true") {
                                bReturnValue[0] = true;
                                if (bSaveLogin[0]) {
                                    AlertDialog dialog = adSaved.create();
                                    dialog.show();
                                    dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

                                }
                                //Boolean b = updateDBWithNewInformation();
                            } else
                                bReturnValue[0] = false;
                        }
                    }, 1000);

                } catch (Exception e) {
                    System.out.println("KS :: Error FROM THE APP 1 : " + e.fillInStackTrace());
                }
            }
        });
        Log.i("Rest API", " Check Login returns :" + bReturnValue[0]);
        return bReturnValue[0];
    }



    private Boolean updateDBWithNewInformation() {
        boolean rv = false;
        try {
            encryptedPassword = Application_Encrypt.encrypt(pwd);
            System.out.println("encripted " + encryptedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error in encryption");
            encryptedPassword = "";
        }

        if (!Objects.equals(name, "") && !Objects.equals(encryptedPassword, "")) {
            dbHelper.updateLoginInformationInDB(db, name, encryptedPassword);
            dbHelper.updateUserNameInSiteEvents(db, name);
            rv = true;
        }
        return rv;
    }

    private void bSaveLoginInfo() {


    }

    private String doInBackground() {

        String sReturnValue = "false";
        Boolean bReturnValue = false;

        try {
           String[] errormessage = {""};
           String sName = txt_UserName.getText().toString();
           String sPassword = txt_Password.getText().toString();
           bReturnValue = restFull.WS_GetLogin(sName, sPassword, errormessage);
           Log.i("rest API", "bReturnValue "+ bReturnValue);
           if(bReturnValue){
               name = sName;
           pwd = sPassword;
           sReturnValue = "true";}
           else
               sReturnValue = "False";

            //ad.setMessage(resp);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("KS ::"+ ex);
            return "ERROR: " + ex;
        }

        return sReturnValue;
    }

    private void bLogout() {

        name = "username";
        pwd = "";
        dbHelper.updateLoginInformationInDB(db, "username", "");
        txt_UserName.setText(name);
        txt_Password.setText(pwd);

    }

    private void AlertDialogShow(String message, String title, String button,  String theme) {
        int themeResId = R.style.AlertDialogTheme;
        try {
            if (theme.equalsIgnoreCase("warning")) {
                themeResId = R.style.AlertDialogWarning;
            }
            if (theme.equalsIgnoreCase("error")) {
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

}