package com.example.challenge3;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HumidityDAO {
    @Insert
    void insert(HumidityDAO humidity);

    @Query("SELECT * FROM temperatureEntity")
    LiveData<List<HumidityDAO>> getAllHumidities();

    @Query("SELECT * FROM temperatureEntity WHERE id = :temperatureId")
    HumidityDAO getHumidityById(int humidityID);

    @Update
    void update(HumidityDAO humidity);

    @Delete
    void delete(HumidityDAO humidity);

    @Query("DELETE FROM temperatureEntity")
    void deleteAll();
}
