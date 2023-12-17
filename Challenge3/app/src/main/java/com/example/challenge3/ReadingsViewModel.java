package com.example.challenge3;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ReadingsViewModel extends AndroidViewModel {

    private final MutableLiveData<List<TemperatureEntity>> listOfTemperature;
    private final MutableLiveData<List<HumidityEntity>> listOfHumidity;

    private double minTemperature;
    private double minHumidity;
    private double maxTemperature;
    private double maxHumidity;

    private final AppDatabase appDatabase;
    private final ExecutorService executorService;

    public ReadingsViewModel(Application application) {
        super(application);
        listOfTemperature = new MutableLiveData<>();
        listOfHumidity = new MutableLiveData<>();
        appDatabase = AppDatabase.getDBinstance(getApplication().getApplicationContext());

        executorService = Executors.newSingleThreadExecutor();
    }

    public MutableLiveData<List<TemperatureEntity>> getListOfTemperatureEntity() {
        return listOfTemperature;
    }

    public void getAllTemperatureEntityList() {
        executorService.execute(() -> {
            // Delete old entries first
            long cutoff = System.currentTimeMillis() - (48 * 60 * 60 * 1000);
            appDatabase.temperatureDao().deleteOldTemperatureEntities(cutoff);

            List<TemperatureEntity> temperatureEntityList = appDatabase.temperatureDao().getAllTemperatureList();
            if (temperatureEntityList.size() > 0) {
                listOfTemperature.postValue(temperatureEntityList);
            } else {
                listOfTemperature.postValue(null);
            }
        });
    }

    public void addTemperatureLiveData(TemperatureEntity temperatureEntity){
        executorService.execute(() -> {
            long rowId = appDatabase.temperatureDao().insertTemperatureEntity(temperatureEntity);
            if (rowId > 0) {
                // Insertion was successful
                Log.d("Insert Temperature", "SUCESS");
            } else {
                // Insertion failed
                Log.d("Insert Temperature", "FAIL");
            }
            getAllTemperatureEntityList();
        });
    }

    public ArrayList<TemperatureEntity> getTemperatureData() {
        // Retrieve the value from MutableLiveData and return it
        List<TemperatureEntity> temperatureList = listOfTemperature.getValue();
        if (temperatureList != null) {
            return new ArrayList<>(temperatureList);
        } else {
            return new ArrayList<>();
        }
    }

    public MutableLiveData<List<HumidityEntity>> getListOfHumidityEntity() {

        return listOfHumidity;
    }

    public void getAllHumidityEntityList() {
        executorService.execute(() -> {
            // Delete old entries first
            long cutoff = System.currentTimeMillis() - (48 * 60 * 60 * 1000);
            appDatabase.humidityDao().deleteOldHumidityEntities(cutoff);

            List<HumidityEntity> humidityEntityList = appDatabase.humidityDao().getAllHumidityList();
            if (humidityEntityList.size() > 0) {
                listOfHumidity.postValue(humidityEntityList);
            } else {
                listOfHumidity.postValue(null);
            }
        });
    }

    public void addHumidityLiveData(HumidityEntity humidityEntity){
        executorService.execute(() -> {
            long rowId = appDatabase.humidityDao().insertHumidityEntity(humidityEntity);
            if (rowId > 0) {
                // Insertion was successful
                Log.d("Insert Humidity", "SUCESS");
            } else {
                // Insertion failed
                Log.d("Insert Humidity", "FAIL");
            }
            getAllHumidityEntityList();
        });
    }

    public ArrayList<HumidityEntity> getHumidityData() {
        // Retrieve the value from MutableLiveData and return it
        List<HumidityEntity> humidityList = listOfHumidity.getValue();
        if (humidityList != null) {
            return new ArrayList<>(humidityList);
        } else {
            return new ArrayList<>();
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }


    public void setMaxHumidity(double maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public double getMaxHumidity() {
        return maxHumidity;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMinHumidity(double minHumidity) {
        this.minHumidity = minHumidity;
    }

    public double getMinHumidity() {
        return minHumidity;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

}