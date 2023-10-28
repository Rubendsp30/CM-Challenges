package com.example.challenge2;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends ViewModel {
    // Create a MutableLiveData to hold a list of User objects.
    private final MutableLiveData<ArrayList<User>> users = new MutableLiveData<>();

    // Method to add User objects to the MutableLiveData.
    public void addUsers(User... users) {
        // Create a new ArrayList containing the provided User objects and set it as the value of the MutableLiveData.
        this.users.setValue(new ArrayList<>(List.of(users)));
    }

    // Method to retrieve the list of Users objects from the MutableLiveData.
    public ArrayList<User> getAnimals() {
        // Return the current value of the MutableLiveData, which is the list of User objects.
        return this.users.getValue();
    }
}
