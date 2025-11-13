package com.honeywell.stevents;

import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StdetWeb_Lists {
    public StdetWeb_Lists() {
        this.theList = new ArrayList<>();
    }

    List<Object> theList;

    public StdetWeb_Lists(List<Object> list) {
        this.theList = list;
    }

    public List<Object> getTheList() {

        return theList;
    }

    public void setTheList(List<Object> theList) {
        List<Object> theList1= theList;
        try {
            List<Object> theList2 = Collections.singletonList(theList1.get(0));
            if (!theList2.isEmpty())
                theList=theList2;
        }
        catch(Exception ex){}


        this.theList = theList;
    }

    public void writeJsonToFile(String fullName) {
        Gson gson = new Gson();
        String data = gson.toJson(theList.get(0));
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fullName);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fos);

            myOutWriter.append(data);
            myOutWriter.close();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
