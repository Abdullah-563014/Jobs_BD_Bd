package com.mominur77.Jobs_BD.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PageDao {

    @Query("SELECT * FROM page")
    List<Page> getAll();

    @Insert
    void insert(Page page);

    @Delete
    void delete(Page page);
}
