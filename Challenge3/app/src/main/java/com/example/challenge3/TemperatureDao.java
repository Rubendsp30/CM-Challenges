package com.example.challenge3;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TemperatureDao {

    @Query("Select * from temperatures")
    List<TemperatureEntity> getAllTemperatureList();

    @Insert
    long insertTemperatureEntity (TemperatureEntity temperature);

    @Query("DELETE FROM temperatures WHERE timestamp < :cutoff")
    void deleteOldTemperatureEntities(long cutoff);

}
