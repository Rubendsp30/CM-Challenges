package com.example.challenge3;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TemperatureEntity.class,HumidityEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TemperatureDao temperatureDao();
    public abstract HumidityDao humidityDao();
    public static AppDatabase INSTANCE;

    public static AppDatabase getDBinstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "AppDatabase").build();
        }
        return INSTANCE;
    }
}
