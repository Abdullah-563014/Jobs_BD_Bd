package com.mominur77.Jobs_BD.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private Context context;
    private static DatabaseClient databaseClient;
    private PageDatabse pageDatabse;


    private DatabaseClient(Context context) {
        this.context = context;
        pageDatabse = Room.databaseBuilder(context, PageDatabse.class, "PageDatabase").build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (databaseClient == null) {
            databaseClient = new DatabaseClient(context);
        }
        return databaseClient;
    }

    public PageDatabse getAppDatabase() {
        return pageDatabse;
    }
}
