package com.example.challenge3;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;


public class ReadingsViewModel extends ViewModel {

    private final MutableLiveData<List<SensorReading>> temperature_data = new MutableLiveData<>();
    private final MutableLiveData<List<SensorReading>> humidity_data = new MutableLiveData<>();

    public ReadingsViewModel() {
    }

    public void addHumidityLiveData(SensorReading sensorReading) {
        // Retrieve the current list from LiveData
        List<SensorReading> currentHumidityData = humidity_data.getValue();

        // Update the list with the new sensor reading
        if (currentHumidityData == null) {
            currentHumidityData = new ArrayList<>();
        }
        currentHumidityData.add(sensorReading);

        // Update the LiveData with the new list
        humidity_data.setValue(currentHumidityData);
    }

    public void addTemperatureLiveData(SensorReading sensorReading) {
        // Retrieve the current list from LiveData
        List<SensorReading> currentTemperaturesData = temperature_data.getValue();

        // Update the list with the new sensor reading
        if (currentTemperaturesData == null) {
            currentTemperaturesData = new ArrayList<>();
        }
        currentTemperaturesData.add(sensorReading);

        // Update the LiveData with the new list
        temperature_data.setValue(currentTemperaturesData);
    }

    public ArrayList<SensorReading> getHumidityData() {
        // Retrieve the value from MutableLiveData and return it
        List<SensorReading> humidityList = humidity_data.getValue();
        if (humidityList != null) {
            return new ArrayList<>(humidityList);
        } else {
            return new ArrayList<>();
        }
    }

    public ArrayList<SensorReading> getTemperatureData() {
        // Retrieve the value from MutableLiveData and return it
        List<SensorReading> temperatureList = temperature_data.getValue();
        if (temperatureList != null) {
            return new ArrayList<>(temperatureList);
        } else {
            return new ArrayList<>();
        }
    }

    //N tem DB implementado mas será esta função usada
    public MutableLiveData<List<SensorReading>> getHumidityDataFirestore() {
        ArrayList<SensorReading> readingArr = new ArrayList<>();
        humidity_data.setValue(readingArr);
        return humidity_data;
    }

    //N tem DB implementado mas será esta função usada
    public MutableLiveData<List<SensorReading>> getTemperatureDataFirestore() {
            ArrayList<SensorReading> readingArr = new ArrayList<>();
        temperature_data.setValue(readingArr);
        return temperature_data;
    }

}