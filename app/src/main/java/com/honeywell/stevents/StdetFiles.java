package com.honeywell.stevents;


import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;
import android.util.Log;
import android.util.Xml;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class StdetFiles  {

    private final File directoryApp;
    //View view;
    public File GetDirectory(){
        return directoryApp;
    }
    public final String NAMESPACE = "http://api.limstor.com/stdet/wsstdet.asmx";


    public StdetFiles(File dir){
        //view =v;
        directoryApp = dir;
    }

    public boolean WriteServerDateAsXML(String data, String filename, String opentag, String closetag){
        File newXml = null;

        try {

            if (!directoryApp.exists())
                directoryApp.mkdir();

            newXml = new File(directoryApp + "/" + filename);

            FileOutputStream fos;
            String fullfilename = newXml.getAbsolutePath();
            fos = new FileOutputStream(fullfilename);

            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument("UTF-8", null);
            serializer.startTag(NAMESPACE,opentag);

            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.endDocument();
            serializer.text(data);
            serializer.endTag(NAMESPACE,closetag);

            serializer.flush();
            fos.close();
        } catch (IOException exception) {
            System.out.println(exception);

            return false;
        } catch (Exception exception) {
            System.out.println(exception);
            return false;
        }
        return true;
    }


    public AppDataTable WriteXMLDataAndCreateSTDETable(String data, String filename) {
        File newXml = null;
        AppDataTable table = null;

        try {

            if (!directoryApp.exists())
                directoryApp.mkdir();

            newXml = new File(directoryApp + "/" + filename);

            FileOutputStream fos;
            String fullfilename = newXml.getAbsolutePath();
            data = "<?xml version='1.0' encoding='UTF-8' ?>" + data;
            data = data.replace("</soap:Body>","");
            data = data.replace("<soap:Body>","");
            data = data.replace("</soap:Envelope>","");
            data = data.replace("<xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">","");
            XmlPullParserHandler parser = new XmlPullParserHandler();


            fos = new FileOutputStream(fullfilename);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
            myOutWriter.append(data);

            myOutWriter.close();

            fos.flush();
            fos.close();
            FileInputStream input = new FileInputStream(fullfilename);
            table = HandHeldFileParser.XMLParse(fullfilename, filename);
            input.close();

          /*
            XmlSerializer serializer = Xml.newSerializer();

            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument("UTF-8", null);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.endDocument();
            serializer.text(handleEscapeCharacter(data));
            serializer.flush();
           */

        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception);

            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception);
            return null;
        }
        return table;
    }

    public void WriteXMLData(String data, String filename) {
        File newXml = null;
        AppDataTable table = null;

        try {

            if (!directoryApp.exists())
                directoryApp.mkdir();

            newXml = new File(directoryApp + "/" + filename);

            FileOutputStream fos;
            String fullfilename = newXml.getAbsolutePath();
            data = "<?xml version='1.0' encoding='UTF-8' ?>" + data;
            data = data.replace("</soap:Body>","");
            data = data.replace("<soap:Body>","");
            data = data.replace("</soap:Envelope>","");
            data = data.replace("<xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">","");
            XmlPullParserHandler parser = new XmlPullParserHandler();



            fos = new FileOutputStream(fullfilename);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);
            myOutWriter.append(data);

            myOutWriter.close();

            fos.flush();
            fos.close();
            FileInputStream input = new FileInputStream(fullfilename);



          /*
            XmlSerializer serializer = Xml.newSerializer();

            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument("UTF-8", null);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.endDocument();
            serializer.text(handleEscapeCharacter(data));
            serializer.flush();
           */

        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception);

        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception);

        }

    }

    public static<T> List<T> LoadFromJsonFile(String filename, Class<T> classType){
              List<T> myObjects = new ArrayList<>();
        try {
            Gson gson = new Gson();
            InputStream inputStream;
            inputStream = new FileInputStream(filename);
            InputStreamReader ir =  new InputStreamReader(inputStream);
            JsonReader reader =   new JsonReader(ir);
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            Type listType = new TypeToken<ArrayList<T>>() {
            }.getType();
            myObjects = gson.fromJson(ir, listType);
            Log.i("populateDB LoadFromJsonFile", classType.getName());
            Log.i("populateDB LoadFromJsonFile",filename );

            Log.i("populateDB LoadFromJsonFile", " FILE ARRAY SIZE " + String.valueOf(myObjects.size()));
        }
        catch (Exception ex) {
            Log.i("populateDB LoadFromJsonFile","ERROR:" + ex.toString());
        }
        return myObjects;

    }
    public AppDataTable ReadJSON_To_STDETable(String filename) {
        File theFile = null;
        AppDataTable table = null;

        try {

            if (!directoryApp.exists())
                directoryApp.mkdir();

            theFile = new File(directoryApp + "/" + filename);
            String fullfilename = theFile.getAbsolutePath();

            Log.i("populateDB ReadJSON_To_STDETable","fullfilename "+fullfilename);

            FileInputStream input = new FileInputStream(fullfilename);
            table = HandHeldFileParser.JSONParse(fullfilename, filename);
            input.close();


        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception);

            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception);
            return null;
        }
        return table;
    }
    public AppDataTable ReadXMLToSTDETable(String filename) {
        File newXml = null;
        AppDataTable table = null;

        try {

            if (!directoryApp.exists())
                directoryApp.mkdir();

            newXml = new File(directoryApp + "/" + filename);


            String fullfilename = newXml.getAbsolutePath();

            FileInputStream input = new FileInputStream(fullfilename);
            table = HandHeldFileParser.XMLParse(fullfilename, filename);
            input.close();


        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception);

            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception);
            return null;
        }
        return table;
    }
    public AppDataTables ReadJSON_To_STDETables() {
        File newXml = null;
        AppDataTables tables = new AppDataTables();

        try {

            if (!directoryApp.exists())
                directoryApp.mkdir();

            String ext  = ".json";


            tables.AddStdetDataTable(new DataTable_SiteEvent());

            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.EQUIP_IDENT + ext));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.USERS + ext));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.SITE_EVENT_DEF +ext));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.MAINTENANCE + ext));

        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception);
            return null;
        }
        return tables;
    }

    public AppDataTables ReadXMLToSTDETables() {
        File newXml = null;
        AppDataTables tables = new AppDataTables();

        try {

            if (!directoryApp.exists())
                directoryApp.mkdir();


            tables.AddStdetDataTable(new DataTable_SiteEvent());

            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.EQUIP_IDENT + ".xml"));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.USERS + ".xml"));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.SITE_EVENT_DEF + ".xml"));
            tables.AddStdetDataTable(ReadXMLToSTDETable(HandHeld_SQLiteOpenHelper.MAINTENANCE + ".xml"));

        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception);
            return null;
        }
        return tables;
    }


    public String handleEscapeCharacter( String strOrig ) {
        String[] escapeCharacters = {
                "&gt;", "&lt;", "&amp;", "&quot;", "&apos;" };
        String[] onReadableCharacter = {">", "<", "&", "\"\"", "'"};
        String str = strOrig;
        String stringFixed = str;
        for (int i = 0; i < escapeCharacters.length; i++) {
            str = str.replace(escapeCharacters[i], onReadableCharacter[i]);
        }
        int a = 2;
        return str;


    }
}
