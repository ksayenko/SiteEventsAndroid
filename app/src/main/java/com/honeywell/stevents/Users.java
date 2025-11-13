package com.honeywell.stevents;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Users {
    @SerializedName("strUserName")
    private String strUserName;

    public Users(String strUserName, String name, String domain) {
        this.strUserName = strUserName;
        Name = name;
        Domain = domain;
    }

    public String getDomain() {
        return Domain;
    }

    public void setDomain(String domain) {
        Domain = domain;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @SerializedName("Name")
    private String Name;
    @SerializedName("Domain")
    private String Domain;

    public String getStrUserName() {
        return strUserName;
    }

    public void setStrUserName(String strUserName) {
        this.strUserName = strUserName;
    }

    @Override
    public String toString() {
        return "Users{" +
                "strUserName='" + strUserName + '\'' +
                ", Name='" + Name + '\'' +
                ", Domain='" + Domain + '\'' +
                '}';
    }

    public ArrayList<String> CreateArrayListForInsert()
    {
        DataTable_Users table =  new DataTable_Users();
        ArrayList<String> row = table.getEmptyDataRow();
        row.set(table.GetElementIndex(DataTable_Users.strUserName), strUserName);
        row.set(table.GetElementIndex(DataTable_Users.strName), Name);
        row.set(table.GetElementIndex(DataTable_Users.strDomain), Domain);
        row.set(table.GetElementIndex(DataTable_Users.nID), "-1");
        return   row;
    }

    public static ArrayList<Users> LoadFromJsonFile(String filename){
        Log.i("populateDB LoadFromJsonFile",filename+" -- SIZE 1");
        ArrayList<Users> myObjects =  new ArrayList<>();
        try {
            Gson gson = new Gson();
            InputStream inputStream;
            inputStream = new FileInputStream(filename);
            InputStreamReader  ir =  new InputStreamReader(inputStream);
            JsonReader reader =   new JsonReader(ir);
            String content = new String(Files.readAllBytes(Paths.get(filename)));

            Log.i("populateDB LoadFromJsonFile", content);
            Log.i("populateDB LoadFromJsonFile", inputStream.toString());
            Log.i("populateDB LoadFromJsonFile", reader.toString());
            Type listType = new TypeToken<ArrayList<Users>>() {
            }.getType();
            myObjects = gson.fromJson(ir, listType);
            Log.i("populateDB LoadFromJsonFile", filename + " -- SIZE " + String.valueOf(myObjects.size()));
        }
        catch (Exception ex) {
            Log.i("populateDB LoadFromJsonFile","ERROR:" + ex.toString());
        }
        return myObjects;

    }

}
