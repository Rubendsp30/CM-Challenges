package com.example.challenge3;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

public class SensorReading implements Serializable {
    Double sensorReading;
    Date timestamp;

    public SensorReading(Double sensorReading) {
        this.sensorReading = sensorReading;
        this.timestamp = Timestamp.from(Instant.now());
    }

    //Usado pelo  Firebase futuramente
    public SensorReading() {
    }

    public Double getSensorReading() {
        return sensorReading;
    }


    public Date getTimestamp() {
        return timestamp;
    }


    @NonNull
    @Override
    public String toString() {
        return "SensorReading{" +
                "sensorReading=" + sensorReading +
                ", timestamp=" + timestamp +
                '}';
    }
}