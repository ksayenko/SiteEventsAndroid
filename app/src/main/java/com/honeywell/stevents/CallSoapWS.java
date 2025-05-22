package com.honeywell.stevents;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.xmlpull.v1.XmlPullParserException;


import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

/*
SOAP 1.1
The following is a sample SOAP 1.1 request and response. The placeholders shown need to be replaced with actual values.

POST /stdet/wsstdet.asmx HTTP/1.1
Host: api.limstor.com
Content-Type: text/xml; charset=utf-8
Content-Length: length
SOAPAction: "http://api.limstor.com/stdet/wsstdet.asmx/GetDataset"

<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <GetDataset xmlns="http://api.limstor.com/stdet/wsstdet.asmx">
      <datasetName>string</datasetName>
    </GetDataset>
  </soap:Body>
</soap:Envelope>
HTTP/1.1 200 OK
Content-Type: text/xml; charset=utf-8
Content-Length: length

<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <GetDatasetResponse xmlns="http://api.limstor.com/stdet/wsstdet.asmx">
      <GetDatasetResult>
        <xsd:schema>schema</xsd:schema>xml</GetDatasetResult>
    </GetDatasetResponse>
  </soap:Body>
</soap:Envelope>


SOAP 1.2
The following is a sample SOAP 1.2 request and response. The placeholders shown need to be replaced with actual values.

POST /stdet/wsstdet.asmx HTTP/1.1
Host: api.limstor.com
Content-Type: application/soap+xml; charset=utf-8
Content-Length: length

<?xml version="1.0" encoding="utf-8"?>
<soap12:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
  <soap12:Body>
    <GetDataset xmlns="http://api.limstor.com/stdet/wsstdet.asmx">
      <datasetName>string</datasetName>
    </GetDataset>
  </soap12:Body>
</soap12:Envelope>
HTTP/1.1 200 OK
Content-Type: application/soap+xml; charset=utf-8
Content-Length: length

<?xml version="1.0" encoding="utf-8"?>
<soap12:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
  <soap12:Body>
    <GetDatasetResponse xmlns="http://api.limstor.com/stdet/wsstdet.asmx">
      <GetDatasetResult>
        <xsd:schema>schema</xsd:schema>xml</GetDatasetResult>
    </GetDatasetResponse>
  </soap12:Body>
</soap12:Envelope>
 */
public class CallSoapWS {
    private static final int TIMEOUT_SOCKET = 180000;

    public final String NAMESPACE = "http://api.limstor.com/stdet/wsstdet.asmx";
    public final String METHOD_NAME_SERVERDATE = "GetServerDate";
    public final String RESPONSE_METHOD_NAME1 = "GetServerDateResult";
    public final String SOAP_ACTION_SERVERDATE = "http://api.limstor.com/stdet/wsstdet.asmx/GetServerDate";
    public final String METHOD_NAME_DATATABLES = "GetDataset";
    public final String METHOD_NAME_UPLOAD = "UploadFile";
    public final String METHOD_NAME_UPLOAD2 = "UploadFile2";
    public final String METHOD_NAME_LOGIN = "Login";
    public final String RESPONSE_METHOD_NAME2 = "GetDatasetResult";
    public final String SOAP_ACTION2 = "http://api.limstor.com/stdet/wsstdet.asmx/GetDataset";
    public final String SOAP_ACTION_UPLOAD = "http://api.limstor.com/stdet/wsstdet.asmx/UploadFile";
    public final String SOAP_ACTION_UPLOAD2 = "http://api.limstor.com/stdet/wsstdet.asmx/UploadFile2";
    public final String SOAP_ACTION_LOGIN = "http://api.limstor.com/stdet/wsstdet.asmx/Login";

    public final static String SITE_EVENT = "tbl_Inst_Readings";
    public final static String EQUIP_IDENT = "tbl_Equip_Ident";
    public final static String USERS = "tbl_Users";
    public final static String SITE_EVENT_DEF = "tbl_Site_Event_Def";


    public final String SOAP_ADDRESS = "http://api.limstor.com/stdet/wsstdet.asmx";

    private File directoryApp;
    private Object response;

    public File GetDirectory() {
        return directoryApp;
    }

    View view;

    public CallSoapWS(File dir) {
        //view =v;
        directoryApp = dir;
    }

    public Boolean WS_UploadFile2(byte[] file, String filename, String user, String pwd) {
        boolean bResponse = false;
        String sUploadResponse = "";
        System.out.println("in WS_UploadFile2  ");
        String addr = getSoapAction(METHOD_NAME_UPLOAD2);
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_UPLOAD2);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        PropertyInfo pi1 = new PropertyInfo();
        pi1.setName("file");//"file");
        pi1.setValue(file);//"file");
        request.addProperty(pi1);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("fileName");//"file");
        pi2.setValue(filename);//"file");
        request.addProperty(pi2);

        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("user");//"file");
        pi3.setValue(user);//"file");
        request.addProperty(pi3);

        PropertyInfo pi4 = new PropertyInfo();
        pi4.setName("password");//"file");
        pi4.setValue(pwd);//"file");
        request.addProperty(pi4);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        new MarshalBase64().register(envelope); // serialization

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(addr);
        Object response = null;
        try {
            httpTransport.call(SOAP_ACTION_UPLOAD2, envelope);
            response = envelope.getResponse();
        } catch (Exception exception) {
            System.out.println("ERROR WS_UploadFile2  " + exception);
            exception.printStackTrace();
            response = exception.toString();
            //bResponse = false;
        }

        sUploadResponse = response.toString();
        System.out.println("WS_UploadFile2 response "+sUploadResponse);
        if (sUploadResponse.equalsIgnoreCase("true"))
            bResponse = true;
        return bResponse;
    }

    public Boolean WS_UploadFile(byte[] file, String filename, String user, String pwd) {
        boolean bResponse = false;
        String sUploadResponse = "";
        String addr = getSoapAction(METHOD_NAME_UPLOAD);
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_UPLOAD);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        PropertyInfo pi1 = new PropertyInfo();
        pi1.setName("file");//"file");
        pi1.setValue(file);//"file");
        request.addProperty(pi1);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("fileName");//"file");
        pi2.setValue(filename);//"file");
        request.addProperty(pi2);

        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("user");//"file");
        pi3.setValue(user);//"file");
        request.addProperty(pi3);

        PropertyInfo pi4 = new PropertyInfo();
        pi4.setName("password");//"file");
        pi4.setValue(pwd);//"file");
        request.addProperty(pi4);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        new MarshalBase64().register(envelope); // serialization

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(addr);
        Object response = null;
        try {
            httpTransport.call(SOAP_ACTION_UPLOAD, envelope);
            response = envelope.getResponse();
        } catch (Exception exception) {
            System.out.println("WS_UploadFile  " + exception);
            exception.printStackTrace();
            response = exception.toString();
        }

        sUploadResponse = response.toString();
        System.out.println(sUploadResponse);
        if (sUploadResponse.equalsIgnoreCase("true"))
            bResponse = true;
        return bResponse;
    }

    public String CheckConnection() {
        String ServerDate = "";
        String addr = getSoapAction(METHOD_NAME_SERVERDATE);
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_SERVERDATE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(addr);
        Object response = null;
        try {
            httpTransport.call(SOAP_ACTION_SERVERDATE, envelope);
            response = envelope.getResponse();
        } catch (UnknownHostException exception) {
            System.out.println("WS_GetServerDate " + exception.toString());
            exception.printStackTrace();
            response = "ERROR! Check Your Connection";// + exception.toString();
        } catch (Exception exception) {
            System.out.println("WS_GetServerDate " + exception.toString());
            exception.printStackTrace();
            response = "ERROR! Check Your Connection" + exception.toString();

        }
        return response.toString();
    }


    public String WS_GetServerDate(boolean bWrite) {
        String ServerDate = "";
        String addr = getSoapAction(METHOD_NAME_SERVERDATE);
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_SERVERDATE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(addr);
        Object response = null;
        try {
            httpTransport.call(SOAP_ACTION_SERVERDATE, envelope);
            response = envelope.getResponse();
        } catch (Exception exception) {
            System.out.println("WS_GetServerDate " + exception.toString());
            exception.printStackTrace();
            response = "ERROR In Webserver Connection "+exception.toString();

        }

        ServerDate = response.toString();
        StdetFiles f = new StdetFiles(directoryApp);
        if (bWrite) {
            boolean brv = f.WriteServerDateAsXML(ServerDate, "ServerDate" + ".xml", "dateTime", "dateTime");
        }
            return ServerDate;
    }

    public AppDataTables WS_GetALLDatasets() throws IOException {
        String dataset1;
        StdetFiles f = new StdetFiles(directoryApp);
        AppDataTables tables = new AppDataTables();
        tables.AddStdetDataTable(new DataTable_SiteEvent());
        String sResp;
        boolean brv;

        dataset1 = WS_GetDataset(USERS);
        tables.AddStdetDataTable(f.WriteXMLDataAndCreateSTDETable(dataset1, USERS + ".xml"));
        System.out.println("ks:: TABLE USERS.XML ");
        dataset1 = WS_GetDataset(CallSoapWS.SITE_EVENT_DEF);
        tables.AddStdetDataTable(f.WriteXMLDataAndCreateSTDETable(dataset1, CallSoapWS.SITE_EVENT_DEF + ".xml"));
        System.out.println("KS:: SITE_EVENT_DEF.XML");
        dataset1 = WS_GetDataset(CallSoapWS.EQUIP_IDENT);
        tables.AddStdetDataTable(f.WriteXMLDataAndCreateSTDETable(dataset1, CallSoapWS.EQUIP_IDENT + ".xml"));
        System.out.println("KS:: EQUIP_IDENT.XML");

        // Gets the data repository in write mode
        return tables;
    }

    public void WS_DownloadXMLDatasets() throws IOException {
        String dataset1;
        StdetFiles f = new StdetFiles(directoryApp);
        AppDataTables tables = new AppDataTables();
        tables.AddStdetDataTable(new DataTable_SiteEvent());
        String sResp;
        boolean brv;

        dataset1 = WS_GetDataset(USERS);
        f.WriteXMLData(dataset1, USERS + ".xml");

        dataset1 = WS_GetDataset(CallSoapWS.SITE_EVENT_DEF);
        f.WriteXMLData(dataset1, CallSoapWS.SITE_EVENT_DEF + ".xml");

        dataset1 = WS_GetDataset(CallSoapWS.EQUIP_IDENT);
        f.WriteXMLData(dataset1, CallSoapWS.EQUIP_IDENT + ".xml");

    }

    public Boolean WS_GetLogin(String username, String Pasword)
    {
        String[] errormessage =  new String[]{""};
        return WS_GetLogin(username, Pasword,errormessage);
    }

    public Boolean WS_GetLogin(String username, String Pasword, String[] errormessage) {
        String sUploadResponse = "";
        boolean bResponse = false;
        String addr = getSoapAction(METHOD_NAME_LOGIN);
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_LOGIN);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        PropertyInfo pi1 = new PropertyInfo();
        pi1.setName("user");//"file");
        pi1.setValue(username);//"file");
        request.addProperty(pi1);

        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("password");//"file");
        pi2.setValue(Pasword);//"file");
        request.addProperty(pi2);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        new MarshalBase64().register(envelope); // serialization

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(addr);
        Object response = null;
        try {
            httpTransport.call(SOAP_ACTION_LOGIN, envelope);
            response = envelope.getResponse();
        } catch (Exception exception) {
            System.out.println("WS_GetLogin  " + exception);
            exception.printStackTrace();
            response = exception.toString();
            errormessage[0] = exception.toString();
            //bResponse = false;
        }
        if (response != null) {
            sUploadResponse = response.toString();
            System.out.println(sUploadResponse);
            if (sUploadResponse.equalsIgnoreCase("true"))
                bResponse = true;
        } else
            bResponse = false;
        return bResponse;
    }


    public String WS_GetDataset(String DatasetName)
    {
        String errormessage[] =  new String[]{""};
        return WS_GetDataset(DatasetName,errormessage);
    }

    public String WS_GetDataset(String DatasetName, String errormessage[]) {
        String responseString = "";
        String addr = getSoapAction(METHOD_NAME_DATATABLES);
        String xmlData = "";
        String xmlData1 = "";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_DATATABLES);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        PropertyInfo pi = new PropertyInfo();
        pi.setName("datasetName");//"datasetName");
        pi.setValue(DatasetName);//"tbl_Inst_Readings");

        request.addProperty(pi);


        SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(request);
        HttpTransportSE httpTransport = getHttpTransportSE(addr);

        Object response = null;
        String s1 = "";
        try {
            httpTransport.call(SOAP_ACTION2, envelope);
            xmlData1 = httpTransport.responseDump.toString();
            xmlData = xmlData1.replace(
                    "soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" ",
                    "");
            //later will add
            xmlData = xmlData.replace(
                    "<?xml version=\"1.0\" encoding=\"utf-8\"?>",
                    "");


            SoapObject resultsString = (SoapObject) envelope.getResponse();
            //resultsString looks like this ==> anyType{schema=anyType{element=anyType{complexType=anyType{choice=anyT....etc
            //here i found some complex in getproperty(0) so i not need it
            //then i make Object1 that contain datatable from my database getproperty(1)
            SoapObject Object1 = (SoapObject) resultsString.getProperty(1);
            //object1 look like this ==> anyType{NewDataSet=anyType{Table1=anyType{ID_CAT=1; CAT_V_N=ma
            //here my table1 i wanna to fitch the NewDataSet

            SoapObject Object0 = (SoapObject) resultsString.getProperty(0);
            //object1 look like this ==> anyType{NewDataSet=anyType{Table1=anyType{ID_CAT=1; CAT_V_N=ma
            //here my table1 i wanna to fitch the NewDataSet

            SoapObject tablesS = (SoapObject) Object0.getProperty(0);  //NewDataset
            int n0 = tablesS.getPropertyCount();
            SoapObject[] ObjectsStructure =  new SoapObject[n0];
            for (int i = 0; i < n0; i++) {
                ObjectsStructure[i] = (SoapObject) tablesS.getProperty(i);
            }

            SoapObject tables = (SoapObject) Object1.getProperty(0);  //NewDataset
            //tables object now looks like this  ==> anyType{Table1=anyType{ID_CAT=1;CAT_N ...etc
            int nTables = tables.getPropertyCount();
            //now i wanna loop in my table to get columns valus it will be tablesObject properties depend on iteration to get row by row
            SoapObject[] Objecttable =  new SoapObject[nTables];
            for (int i = 0; i < nTables; i++) {
                Objecttable[i] = (SoapObject) tables.getProperty(i);
            }

           //xmlData = httpTransport.responseDump.toString();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            System.out.println("XmlPullParserException "+ e);
            xmlData = e.toString();
            errormessage[0] = e.toString();

        } catch (SoapFault soapFault) {
            System.out.println("SoapFault soapFault "+ soapFault);
            soapFault.printStackTrace();
            xmlData = soapFault.toString();
            errormessage[0] = soapFault.toString();
        } catch (HttpResponseException e) {
            System.out.println("HttpResponseException e "+ e);
            e.printStackTrace();
            xmlData = e.toString();
            errormessage[0] = e.toString();
        } catch (IOException e) {
            System.out.println("IOException e "+ e);
            e.printStackTrace();
            xmlData = e.toString();
            errormessage[0] = e.toString();
        } catch (Exception exception) {
            System.out.println("Exception exception "+ exception);
            exception.printStackTrace();
            Log.i(DatasetName, exception.getMessage());
            xmlData = exception.toString();
            errormessage[0] = exception.toString();
        }
        //xmlData = xmlData2;
        //Log.i("xmlData : ",xmlData);


        return xmlData;

    }

    public String getSoapAction(String method) {
        //return "\"" + SOAP_ADDRESS + "?op="+method + "\"";
        //return  SOAP_ADDRESS + "/"+method ;
        return  SOAP_ADDRESS + "?op="+method ;
    }

    public void SaveXMLFile(){

    }

    public final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request){
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.encodingStyle=SoapSerializationEnvelope.XSD;
        envelope.setOutputSoapObject(request);
        new MarshalBase64().register(envelope);
        envelope.setAddAdornments(false);

        //envelope.headerOut = new Element[1];
        Element header = new Element().createElement(this.NAMESPACE, "AuthorizationToken");
        Element token = new Element().createElement(this.NAMESPACE, "Token");
        return envelope;
    }

    public final HttpTransportSE getHttpTransportSE(String URL){
        HttpTransportSE ht = new HttpTransportSE(URL);
        ht.debug = true;
        //ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return ht;
    }

}