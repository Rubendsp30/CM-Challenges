package com.example.challenge3;

import androidx.room.Database;

@Database(entities = {TemperatureEntity.class, HumidityEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TemperatureDAO temperatureDAO();
    public abstract HumidityDAO humidityDAO();

    private static volatile AppDatabase INSTANCE;
    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "sensor_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}