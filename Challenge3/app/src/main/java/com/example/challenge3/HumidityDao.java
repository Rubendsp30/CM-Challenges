package com.example.challenge3;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HumidityDao {

    @Query("Select * from humidity_table")
    List<HumidityEntity> getAllHumidityList();

    @Insert
    long insertHumidityEntity (HumidityEntity humidity);

    @Query("DELETE FROM humidity_table WHERE timestamp < :cutoff")
    void deleteOldHumidityEntities(long cutoff);

}
