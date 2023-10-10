package com.example.challenge1.viewModel;
import com.example.challenge1.util.Animal;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class AnimalViewModel extends ViewModel {
    // Create a MutableLiveData to hold a list of Animal objects.
    private final MutableLiveData<ArrayList<Animal>> animals = new MutableLiveData<>();

    // Method to add Animal objects to the MutableLiveData.
    public void addAnimals(Animal... animals) {
        // Create a new ArrayList containing the provided Animal objects and set it as the value of the MutableLiveData.
        this.animals.setValue(new ArrayList<>(List.of(animals)));
    }

    // Method to retrieve the list of Animal objects from the MutableLiveData.
    public ArrayList<Animal> getAnimals() {
        // Return the current value of the MutableLiveData, which is the list of Animal objects.
        return this.animals.getValue();
    }
}