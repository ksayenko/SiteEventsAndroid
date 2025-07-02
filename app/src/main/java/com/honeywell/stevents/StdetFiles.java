package com.honeywell.stevents;


import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.util.Xml;

public class StdetFiles  {

    private File directoryApp;
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
            String fullfilename = newXml.getAbsolutePath().toString();
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
            System.out.println(exception.toString());

            return false;
        } catch (Exception exception) {
            System.out.println(exception.toString());
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
            String fullfilename = newXml.getAbsolutePath().toString();
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
            table = HandHeldDomParser.XMLParse(fullfilename, filename);
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
            System.out.println(exception.toString());

            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception.toString());
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
            String fullfilename = newXml.getAbsolutePath().toString();
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
            System.out.println(exception.toString());

        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception.toString());

        }

    }

    public AppDataTable ReadXMLToSTDETable(String filename) {
        File newXml = null;
        AppDataTable table = null;

        try {

            if (!directoryApp.exists())
                directoryApp.mkdir();

            newXml = new File(directoryApp + "/" + filename);


            String fullfilename = newXml.getAbsolutePath().toString();

            FileInputStream input = new FileInputStream(fullfilename);
            table = HandHeldDomParser.XMLParse(fullfilename, filename);
            input.close();


        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception.toString());

            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println(exception.toString());
            return null;
        }
        return table;
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
            System.out.println(exception.toString());
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
