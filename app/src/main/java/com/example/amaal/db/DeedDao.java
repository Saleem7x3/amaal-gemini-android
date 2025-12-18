package com.example.amaal.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.amaal.model.Deed;
import java.util.List;

@Dao
public interface DeedDao {
    @Insert
    void insert(Deed deed);

    @Insert
    void insertAll(List<Deed> deeds);

    @Update
    void update(Deed deed);

    @Delete
    void delete(Deed deed);

    @Query("SELECT * FROM deeds WHERE isCompleted = 0")
    List<Deed> getIncompleteDeeds();

    @Query("SELECT * FROM deeds")
    List<Deed> getAllDeeds();
    
    @Query("DELETE FROM deeds")
    void deleteAll();
}