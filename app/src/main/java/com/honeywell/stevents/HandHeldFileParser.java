package com.honeywell.stevents;
import android.util.Log;


import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HandHeldFileParser {


    public static final String EXT_XML = ".xml";
    public static final String EXT_JSON = ".json";

    public static AppDataTable JSONParse(String dataset, List<Object> l) {
        AppDataTable data;
        String sName = "";
        String sValue = "";

        data = null;
        if (dataset.equalsIgnoreCase(CallWebServices2.USERS)) {
            data = new DataTable_Users();
        } else if (dataset.equalsIgnoreCase(CallWebServices2.EQUIP_IDENT)) {
            data = new DataTable_Equip_Ident();
        } else if (dataset.equalsIgnoreCase(CallWebServices2.SITE_EVENT_DEF)) {
            data = new DataTable_Site_Event_Def();
        } else if (dataset.equalsIgnoreCase(CallWebServices2.MAINTENANCE)) {
            data = new DataTable_Maint();
        } else
            data = new AppDataTable();

        ArrayList list = (ArrayList) l.get(0);
        Log.i("JSONParse", dataset+" "+String.valueOf(list.size()));
        int nRows = 0;
        int nCols = 0;
        int i=0;
        ArrayList row = new ArrayList();
        try {
            nRows = list.size();
            for (i = 0; i < nRows; i++) {
                row = new ArrayList();
                Object o = list.get(i);
                if (dataset.equalsIgnoreCase(CallWebServices2.USERS)) {
                    row = ((Users) o).CreateArrayListForInsert();
                } else if (dataset.equalsIgnoreCase(CallWebServices2.EQUIP_IDENT)) {
                    row = ((Equip_Ident) o).CreateArrayListForInsert();
                } else if (dataset.equalsIgnoreCase(CallWebServices2.SITE_EVENT_DEF)) {
                    row = ((Site_Event_Def) o).CreateArrayListForInsert();
                } else if (dataset.equalsIgnoreCase(CallWebServices2.MAINTENANCE)) {
                    MaintPersIdent o1 = (MaintPersIdent) o;
                    row = ((MaintPersIdent) o).CreateArrayListForInsert();
                }
                data.AddRowToData(row);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Error JSONParse", "JSONParse for" + dataset + e.toString());
            Log.i("Error JSONParse", "JSONParse for" + i);

        }


        return data;
    }

    public static AppDataTable JSONParse(String fullfilename, String filename) throws FileNotFoundException {
        AppDataTable data;
        String sName = "";
        String sValue = "";
        data = null;
        List<Object> l =  new ArrayList<>();
        Log.i("populateDB JSONParse","filename "+filename);
        if (filename.equalsIgnoreCase(CallWebServices2.USERS + EXT_JSON)) {
            l= Collections.singletonList(StdetFiles.LoadFromJsonFile(fullfilename,Users.class));
        } else if (filename.equalsIgnoreCase(CallWebServices2.EQUIP_IDENT + EXT_JSON)) {
            l= Collections.singletonList(StdetFiles.LoadFromJsonFile(fullfilename,Equip_Ident.class));

        } else if (filename.equalsIgnoreCase(CallWebServices2.SITE_EVENT_DEF +EXT_JSON)) {
            l= Collections.singletonList(StdetFiles.LoadFromJsonFile(fullfilename,Site_Event_Def.class));

        } else if (filename.equalsIgnoreCase(CallWebServices2.MAINTENANCE + EXT_JSON)) {
            l = Collections.singletonList(StdetFiles.LoadFromJsonFile(fullfilename, MaintPersIdent.class));
        }


        data = JSONParse(fullfilename,l);

        return data;
    }
    public static AppDataTable XMLParse(String fullfilename, String filename) {
        AppDataTable data;
        String sName = "";
        String sValue = "";
        data = null;
        if (filename.equalsIgnoreCase(CallSoapWS.USERS + EXT_XML)) {
            data = new DataTable_Users();
        } else if (filename.equalsIgnoreCase(CallSoapWS.EQUIP_IDENT + EXT_XML)) {
            data = new DataTable_Equip_Ident();
        } else if (filename.equalsIgnoreCase(CallSoapWS.SITE_EVENT_DEF + EXT_XML)) {
            data = new DataTable_Site_Event_Def();
        } else if (filename.equalsIgnoreCase(CallSoapWS.MAINTENANCE + EXT_XML)) {
            data = new DataTable_Maint();
        } else
            data = new AppDataTable();

        int nRows = 0;
        int nCols = 0;

        try {
            File inputFile = new File(fullfilename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            System.out.println("Before dBuilder.parse(inputFile)" + fullfilename);
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nListTable = doc.getElementsByTagName("Table");

            Element el = doc.getDocumentElement();
            NamedNodeMap nListMap = el.getAttributes();
            nRows = nListTable.getLength();

            for (int i = 0; i < nRows; i++) {
                try {
                    Node nNode1 = nListTable.item(i);
                    //System.out.println("\nCurrent Element :" + i.toString() + " " + nNode1.getNodeName());
                    NamedNodeMap map = nNode1.getAttributes();
                    NodeList nList = nNode1.getChildNodes();

                    ArrayList<String> dRow = data.getEmptyDataRow();

                    nCols = nList.getLength();
                    for (int k = 0; k < nCols; k++) {

                        Node nNode = nList.item(k);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) nNode;

                            String sk = Integer.toString(k);

                            sName = nList.item(k).getNodeName();
                            NodeList nlistTemp = element.getChildNodes();
                            sValue = "";
                            if (nlistTemp != null && nlistTemp.item(0) != null) {
                                sValue = nlistTemp.item(0).getTextContent();
                                sValue = sValue.trim();

                                int iIndex = data.GetElementIndex(sName);
                                if (iIndex > -1) {
                                    dRow.set(iIndex, sValue);
                                }
                            } else
                                continue;

                        }
                    }

                    data.AddRowToData(dRow);
                    dRow = data.getEmptyDataRow();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Error", "XMLParse" + filename + " " + sName + sValue);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            Log.i("Error", "XMLParse" + filename + " " + e);
        }
        return data;
    }
}
