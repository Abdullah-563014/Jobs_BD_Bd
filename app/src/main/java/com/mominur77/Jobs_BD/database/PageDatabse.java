package com.mominur77.Jobs_BD.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Page.class}, version = 1)
public abstract class PageDatabse extends RoomDatabase {
    public abstract PageDao pageDao();
}
