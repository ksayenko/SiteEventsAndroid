package com.honeywell.stevents;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CallWebServices2 {
    private static final int TIMEOUT_SOCKET = 180000;

    public final String BASE_URL = "https://api.limstor.com/";
    public final String PART1_NAMESPACE_WEB = "api.limstor.com";
    public final String PART2_NAMESPACE_WEB = "/stdet/wsstdet.asmx";
    public final String METHOD_NAME_SERVERDATE = "GetServerDate";
    public final String RESPONSE_METHOD_NAME1 = "GetServerDateResult";

    public final static String EQUIP_IDENT = "tbl_Equip_Ident";
    public final static String USERS = "tbl_Users";
    public final static String MAINTENANCE  = "tbl_MaintPersIdent";
    public final static String SITE_EVENT_DEF = "tbl_Site_Event_Def";


    public final Integer PORT = 443;
    public final Integer TIMEOUT = 1000;

    private final File directoryApp;


    public CallWebServices2(File dir) {
        //view =v;
        directoryApp = dir;

    }

    public String WS_GetServerDateResponse(boolean bWrite) throws IOException {
        final String[] ServerDate = {""};
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.i("Rest API", " WS_GetServerDate ");

        RestApiService service = retrofit.create(RestApiService.class);

        Response<String> respDate = service.getServerDateString().execute();
        if (respDate.isSuccessful()) {
            String date = respDate.body();
            return date;
            // Process data in the background
        } else {
            // Handle API errors
        }
    return "ERROR";
    }


    public List<Object> WS_GetDatasetAndWriteToTheFileSystem(String DatasetName, File directoryApp) throws IOException {
        String[] errormessage =  new String[]{""};
        File newJson;
        StdetWeb_Lists thelist = new StdetWeb_Lists();
        newJson = new File(directoryApp + "/" + DatasetName+".json");
        List<Object> array =  new ArrayList<>();
        if(DatasetName == USERS) {
            array = Collections.singletonList(WS_GetDataSet_Users_Response());
        }
        if(DatasetName == MAINTENANCE) {
            array = Collections.singletonList(WS_GetDataSet_MaintPersIdent_Response());
        }
        if(DatasetName == SITE_EVENT_DEF) {
            array = Collections.singletonList(WS_GetDataSet_Site_Event_Def_Response());
        }
        if(DatasetName == EQUIP_IDENT) {
            array = Collections.singletonList(WS_GetDataSet_Equip_Ident_Response());
        }
            thelist.setTheList(array);
            thelist.writeJsonToFile( newJson.getAbsolutePath());

        return array;

    }

//https://api.limstor.com/stdetweb/Data/GetDataset?datasetName=tbl_Users
    public List<Users> WS_GetDataSet_Users_Response() throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.i("Rest API", " in WS_GetDataSetResponse ");
        ArrayList<Users> users  = new ArrayList<Users>();

        RestApiService service = retrofit.create(RestApiService.class);
        try {
        Response<List<Users>> respDataSet = service.getDataSetUsers(USERS).execute();
        if (respDataSet.isSuccessful()) {
            users = (ArrayList<Users>)respDataSet.body();
            return users;
            // Process data in the background
        } else {
            String error = respDataSet.errorBody().toString();
            Log.i("Rest API", " ERROR for WS_GetDataSet_Users_Response USERS " + error);
            return null;
        }
        } catch (Exception e) {
            Log.i("Rest API", " Exception for WS_GetDataSet_Users_Response   " + e);
            return users;
        }
        //return "ERROR";
    }

    //https://api.limstor.com/stdetweb/Data/GetDataset?datasetName=tbl_Equip_Ident
    public List<Equip_Ident> WS_GetDataSet_Equip_Ident_Response() throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.i("Rest API", " in WS_GetDataSetResponse ");
        ArrayList<Equip_Ident> array = new ArrayList<Equip_Ident>();
        try {
            RestApiService service = retrofit.create(RestApiService.class);

            Response<List<Equip_Ident>> respDataSet = service.getDataSetEquipIdent(EQUIP_IDENT).execute();
            if (respDataSet.isSuccessful()) {
                array = (ArrayList<Equip_Ident>) respDataSet.body();
                return array;
                // Process data in the background
            } else {
                String error = respDataSet.errorBody().toString();
                Log.i("Rest API", " ERROR for WS_GetDataSet_Equip_Ident_Response  USERS " + error);
                return null;
            }
        } catch (Exception e) {
            Log.i("Rest API", " Exception for WS_GetDataSet_Equip_Ident_Response   " + e);
            return array;
        }
    }

    public Boolean WS_GetLogin(String username, String password)
    {
        String[] errormessage =  new String[]{""};
        return WS_GetLogin(username, password,errormessage);
    }

    //https://api.limstor.com/stdetweb/Data/GetDataset?datasetName=tbl_Site_Event_Def
    //   https://api.limstor.com/stdetweb/Data/Login?user=Kateryna.sayenko?password=Deti9503%23
    // when using Retrofit's execute() method,
    // you must perform the network operation on a background thread.
    public Boolean WS_CallLogin(String username, String password, String[] errormessage) {
        final Boolean[] bLogin = {false};
        String passwordNew = java.net.URLEncoder.encode(password);
        Log.i("Rest API", " passwordNew "+password+" "+passwordNew);
        String usernameNew = java.net.URLEncoder.encode(username);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.i("Rest API", " in WS_CallLogin ");


        try { RestApiService service = retrofit.create(RestApiService.class);
            Call<String> respDataSet = service.GetLogin(username,password);

            respDataSet.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    // Handle successful response on the main thread
                    String UserOk = response.body();
                    if (UserOk.toLowerCase()== "true")
                        bLogin[0] = true;
                    else
                        errormessage[0] = response.message()+ response.errorBody();
                    Log.i("Rest API", " in WS_CallLogin " + UserOk+response.message()+ response.errorBody());

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Handle failure on the main thread
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Rest API", " Exception for WS_CallLogin   " + e);
            return bLogin[0];
        }
        return bLogin[0];
    }
    public Boolean WS_CallLogin(String username, String password)
    {
        String[] errormessage =  new String[]{""};
        return WS_CallLogin(username, password,errormessage);
    }

    //https://api.limstor.com/stdetweb/Data/GetDataset?datasetName=tbl_Site_Event_Def
 //   https://api.limstor.com/stdetweb/Data/Login?user=Kateryna.sayenko?password=Deti9503%23
   //when using Retrofit's execute() method,
    // you must perform the network operation on a background thread.
    public Boolean WS_GetLogin(String username, String password, String[] errormessage) {
        Boolean bLogin = false;
        String passwordNew = java.net.URLEncoder.encode(password);
        Log.i("Rest API", " passwordNew "+password+" "+passwordNew);
        String usernameNew = java.net.URLEncoder.encode(username);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.i("Rest API", " in WS_GetLogin ");


        try { RestApiService service = retrofit.create(RestApiService.class);
            Response<String> respDataSet = service.GetLogin(username,password).execute();
            if (respDataSet.isSuccessful()) {
                String UserOk = respDataSet.body();
                assert UserOk != null;
                if (UserOk.equalsIgnoreCase("true"))
                    bLogin = true;
                Log.i("Rest API", " OK WS_GetLogin" + UserOk);
                return bLogin;
                // Process data in the background
            } else {
                String error = respDataSet.errorBody().toString();
                Log.i("Rest API", " ERROR for WS_GetLogin   " + error);
                return bLogin;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Rest API", " Exception for WS_GetLogin   " + e);
            return bLogin;
        }
    }


    //https://api.limstor.com/stdetweb/Data/GetDataset?datasetName=tbl_Site_Event_Def
    public List<Site_Event_Def> WS_GetDataSet_Site_Event_Def_Response() throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.i("Rest API", " in WS_GetDataSet_Site_Event_Def_Response ");
        ArrayList<Site_Event_Def> array = new ArrayList<Site_Event_Def>();

        RestApiService service = retrofit.create(RestApiService.class);
        try {
            Response<List<Site_Event_Def>> respDataSet = service.getDataSetSite_Event_Def(SITE_EVENT_DEF).execute();
            if (respDataSet.isSuccessful()) {
                array = (ArrayList<Site_Event_Def>) respDataSet.body();
                Log.i("Rest API", " OK SITE_EVENT_DEF WS_GetDataSet_Site_Event_Def_Response " + array.toString());
                return array;
                // Process data in the background
            } else {
                String error = respDataSet.errorBody().toString();
                Log.i("Rest API", " ERROR for WS_GetDataSet_Site_Event_Def_Response  SITE_EVENT_DEF " + error);
                return array;
            }
        } catch (Exception e) {
            Log.i("Rest API", " ERROR for WS_GetDataSet_Site_Event_Def_Response  SITE_EVENT_DEF " + e);
            return array;
        }

    }


    //https://api.limstor.com/stdetweb/Data/GetDataset?datasetName=tbl_Equip_Ident
      public List<MaintPersIdent> WS_GetDataSet_MaintPersIdent_Response() throws IOException {

          Retrofit retrofit = new Retrofit.Builder()
                  .baseUrl(BASE_URL)
                  .addConverterFactory(GsonConverterFactory.create())
                  .build();
          Log.i("Rest API", " in WS_GetDataSetResponse ");
          ArrayList<MaintPersIdent> array = new ArrayList<MaintPersIdent>();
          try {
              RestApiService service = retrofit.create(RestApiService.class);

              Response<List<MaintPersIdent>> respDataSet = service.getDataSetMaintPersIdent(MAINTENANCE).execute();
              if (respDataSet.isSuccessful()) {
                  array = (ArrayList<MaintPersIdent>) respDataSet.body();
                  return array;
                  // Process data in the background
              } else {
                  String error = respDataSet.errorBody().toString();
                  Log.i("Rest API", " ERROR for WS_GetDataSet_MaintPersIdent_Response   " + error);
                  return null;
              }
          } catch (Exception e) {
              Log.i("Rest API", " ERROR for WS_GetDataSet_MaintPersIdent_Response   " + e);
              return array;
          }
      }

    public AppDataTables WS_GetALLDatasets() throws IOException {
        List<Object> dataset1;
        StdetFiles f = new StdetFiles(directoryApp);
        AppDataTables tables = new AppDataTables();
        tables.AddStdetDataTable(new DataTable_SiteEvent());
        String sResp;
        boolean brv;

        dataset1 = WS_GetDatasetAndWriteToTheFileSystem(USERS,directoryApp);
        tables.AddStdetDataTable(HandHeldDomParser.JSONParse(USERS, dataset1));
        Log.i("Rest API",dataset1.toString());
        dataset1 = WS_GetDatasetAndWriteToTheFileSystem(SITE_EVENT_DEF,directoryApp);
        tables.AddStdetDataTable(HandHeldDomParser.JSONParse(SITE_EVENT_DEF, dataset1));
        dataset1 = WS_GetDatasetAndWriteToTheFileSystem(MAINTENANCE,directoryApp);
        tables.AddStdetDataTable(HandHeldDomParser.JSONParse(MAINTENANCE, dataset1));
        dataset1 = WS_GetDatasetAndWriteToTheFileSystem(EQUIP_IDENT,directoryApp);
        tables.AddStdetDataTable(HandHeldDomParser.JSONParse(EQUIP_IDENT, dataset1));
        return tables;
    }

    public Boolean WS_UploadFile(byte[] file, String filename, String user, String pwd, String Media) {
        boolean bResponse = false;
        String sUploadResponse = "";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                 .client(new OkHttpClient())
                .build();
        Log.i("Rest API", " in WS_GetDataSetResponse ");

         try {
             RestApiService service = retrofit.create(RestApiService.class);
             RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);

             Call<String> call = service.UploadFile(filename,user,pwd,fileBody);
             Response<String> response = call.execute();

             Log.i("Rest API","Response Code: " + response.code());
             Log.i("Rest API","Response: " + response.body());

             String body = response.body();
             assert body != null;
             if ( body.equalsIgnoreCase("true")&& response.code()==200)
                 return true;//

             Log.i("Rest API","Response message: " + response.message());
             Log.i("Rest API","Response errorBody: " + response.errorBody());
//            }
        } catch (Exception e) {
            Log.i("Rest API", " Exception for WS_UploadFile   " + e);
            return false;
        }

        return  false;
    }



    public String WS_GetServerDate(boolean bWrite) {
        final String[] ServerDate = {""};
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.i("Rest API", " WS_GetServerDate ");

        RestApiService service = retrofit.create(RestApiService.class);

        Call<String> callDate = service.getServerDateString();
        callDate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("Rest API", "getServerDateString "+ response.message()+ response.body()+ response.errorBody());
                if (response.isSuccessful()) {
                    ServerDate[0] = response.body();
                    Log.i("Rest API", "getServerDateString "+  ServerDate[0] +" ");

                } else {
                    // might need to inspect the error body
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("Rest API","onFailure WS_GetServerDate getServerDateString ");
            }
        });
        Log.i(" Rest Api", "New WS_GetServerDate: "+ ServerDate[0]);
        return ServerDate[0];
    }

}
