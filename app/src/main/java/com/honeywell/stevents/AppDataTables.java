package com.honeywell.stevents;

import java.util.ArrayList;

public class AppDataTables {
    private ArrayList<AppDataTable> dataTables;

    public AppDataTables() {
        setDataTables(new ArrayList<AppDataTable>());
    }

    public void AddStdetDataTable(AppDataTable dt) {
        dataTables.add(dt);
    }

    public ArrayList<AppDataTable> getDataTables() {
        return dataTables;
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
        dataTables.add(new DataTable_Users());

    }
}
