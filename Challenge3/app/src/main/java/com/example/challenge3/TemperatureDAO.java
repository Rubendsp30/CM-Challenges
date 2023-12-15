package com.example.challenge3;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TemperatureDAO {
    @Insert
    void insert(TemperatureEntity temperature);

    @Query("SELECT * FROM temperatureEntity")
    LiveData<List<TemperatureEntity>> getAllTemperatures();

    @Query("SELECT * FROM temperatureEntity WHERE id = :temperatureId")
    TemperatureEntity getTemperatureById(int temperatureId);

    @Update
    void update(TemperatureEntity temperature);

    @Delete
    void delete(TemperatureEntity temperature);

    @Query("DELETE FROM temperatureEntity")
    void deleteAll();
}

