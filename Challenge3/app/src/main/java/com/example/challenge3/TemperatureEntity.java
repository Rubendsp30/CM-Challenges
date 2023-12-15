package com.example.challenge3;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class TemperatureEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "value")
    public double value;

    @ColumnInfo(name = "timestamp")
    public String timestamp;

    // Construtores, getters e setters aqui
    public TemperatureEntity(double value, String timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    // Getters e setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
