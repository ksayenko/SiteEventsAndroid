package com.honeywell.stevents;

import android.util.Log;

import java.util.ArrayList;
import java.util.Objects;

public class AppDataTables {
    private ArrayList<AppDataTable> dataTables;

    public AppDataTables() {
        setDataTables(new ArrayList<AppDataTable>());
    }

    public void AddStdetDataTable(AppDataTable dt) {
        dataTables.add(dt);
    }

    public AppDataTable getAppDataTable(String name) {
        AppDataTable dt = null;

        if (dataTables == null || dataTables.size() == 0)
            return dt;

        for (AppDataTable dt1 : dataTables) {
            if (Objects.equals(dt1.getName().toLowerCase(), name.toLowerCase()))
                return dt1;
        }

        return dt;


    }

    public ArrayList<AppDataTable> getDataTables() {
        return dataTables;
    }

    public int getDataTablesCount() {
        return dataTables.size();
    }

    public void setDataTables(ArrayList<AppDataTable> dataTables) {
        this.dataTables = dataTables;
    }

    public void SetSiteEventsTablesStructure() {
        dataTables = new ArrayList<>();
        dataTables.add(new DataTable_SiteEvent());
        dataTables.add(new DataTable_Site_Event_Def());
        dataTables.add(new DataTable_Equip_Ident());
        dataTables.add(new DataTable_LoginInfo());
        dataTables.add(new DataTable_Maint());
        dataTables.add(new DataTable_Users());

    }
}
