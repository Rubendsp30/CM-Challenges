package com.example.challenge1;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.challenge1.FragmentChangeListener;
import com.example.challenge1.util.Animal;
import com.example.challenge1.viewModel.AnimalViewModel;

public class MainActivity extends AppCompatActivity implements FragmentChangeListener {
    private static final String TAG_FRAGMENT_ANIMAL = "fragment_animal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        // Create Animal objects
        Animal frog = new Animal(R.drawable.frog, "Miguel", "Flippers", 5);
        Animal rhino = new Animal(R.drawable.rhino, "Francisco", "Rajah", 10);
        Animal snail = new Animal(R.drawable.snail, "Joana", "Shelly", 2);

        // Initialize ViewModel
        AnimalViewModel animalViewModel = new ViewModelProvider(this).get(AnimalViewModel.class);
        animalViewModel.addAnimals(frog, rhino, snail);

        // Load initial fragment
        loadFragment(new fragment_animal(), TAG_FRAGMENT_ANIMAL);

    }

    @Override
    public void replaceFragment(Fragment fragment) {
        loadFragment(fragment, fragment.toString());
    }
    private void loadFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment, tag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);

        if (currentFragment instanceof fragment_edit) {
            // If the current fragment is "fragment_edit," replace it with "fragment_animal"
            loadFragment(new fragment_animal(), TAG_FRAGMENT_ANIMAL);
        } else if (currentFragment instanceof fragment_animal) {
            // If the current fragment is "fragment_animal," exit the app
            finish();
        } else {
            // For any other fragment, perform the default back button behavior
            super.onBackPressed();
        }
    }


}