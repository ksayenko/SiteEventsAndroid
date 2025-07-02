package com.honeywell.stevents;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;

public class HandHeldDomParser {


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
                System.out.println(e.toString());
            }
        }
        return data;
    }
}
