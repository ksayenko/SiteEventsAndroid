package com.honeywell.stevents;
import static java.util.Collections.list;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HandHeldDomParser {


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
        int nRows = 0;
        int nCols = 0;

        if (data != null) {
            try {
                nRows = list.size();
                for (Integer i = 0; i < nRows; i++) {
                    ArrayList row = new ArrayList();
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
                System.out.println("Error: System.out.println(e.toString());or inside the Row : " + e);

            }
        }


        return data;
    }
    public static AppDataTable XMLParse(String fullfilename, String filename) {
        AppDataTable data;
        String sName = "";
        String sValue = "";
        data = null;
        if (filename.equalsIgnoreCase(CallSoapWS.USERS + ".xml")) {
            data = new DataTable_Users();
        } else if (filename.equalsIgnoreCase(CallSoapWS.EQUIP_IDENT + ".xml")) {
            data = new DataTable_Equip_Ident();
        } else if (filename.equalsIgnoreCase(CallSoapWS.SITE_EVENT_DEF + ".xml")) {
            data = new DataTable_Site_Event_Def();
        } else if (filename.equalsIgnoreCase(CallSoapWS.MAINTENANCE + ".xml")) {
            data = new DataTable_Maint();
        } else
            data = new AppDataTable();

        int nRows = 0;
        int nCols = 0;

        if (data != null) {
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

                for (Integer i = 0; i < nRows; i++) {
                    try {
                        Node nNode1 = nListTable.item(i);
                        //System.out.println("\nCurrent Element :" + i.toString() + " " + nNode1.getNodeName());
                        NamedNodeMap map = nNode1.getAttributes();
                        NodeList nList = nNode1.getChildNodes();

                        ArrayList<String> dRow = data.getEmptyDataRow();

                        nCols = nList.getLength();
                        for (Integer k = 0; k < nCols; k++) {

                            Node nNode = nList.item(k);
                            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element) nNode;

                                String sk = k.toString();

                                sName = nList.item(k).getNodeName();
                                NodeList nlisttemp = element.getChildNodes();
                                sValue = "";
                                if (nlisttemp != null && nlisttemp.item(0) != null) {
                                    sValue = nlisttemp.item(0).getTextContent();
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
                        System.out.println("Error: System.out.println(e.toString());or inside the Row : " + filename + " " + sName + sValue);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
            }
        }
        return data;
    }
}
