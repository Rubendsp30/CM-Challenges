package com.example.challenge1;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challenge1.util.Animal;
import com.example.challenge1.viewModel.AnimalViewModel;

public class fragment_animal extends Fragment {

    // ViewModel for managing animal data
    private AnimalViewModel animalViewModel;

    // Index of the currently selected animal
    private int animalSelected;

    // UI elements
    private Spinner animalSpinner;
    private ImageView avatar;
    private TextView displayOwner;
    private TextView displayName;
    private TextView displayAge;
    private Button editButton;

    // Listener for fragment change events
    @Nullable
    private FragmentChangeListener FragmentChangeListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize the AnimalViewModel using the ViewModelProvider
        this.animalViewModel = new ViewModelProvider(requireActivity()).get(AnimalViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_animal, container, false);

        // Initialize the FragmentChangeListener
        this.FragmentChangeListener = (MainActivity) inflater.getContext();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Retrieve the selected animal index from the fragment arguments
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.animalSelected = bundle.getInt("animal_selected");
        }
        // Mudar isto para o onViewCreated????
        // Initialize UI elements by finding their views in the layout
        this.animalSpinner = view.findViewById(R.id.animal_spinner);
        this.animalSpinner.setSelection(this.animalSelected);
        this.avatar =  view.findViewById(R.id.animal_avatar);
        this.displayOwner = view.findViewById(R.id.display_owner);
        this.displayName =  view.findViewById(R.id.display_name);
        this.displayAge =  view.findViewById(R.id.display_age);
        this.editButton = view.findViewById(R.id.edit_button);

        // Set an item selection listener for the animalSpinner
        this.animalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Ensure that the AnimalViewModel and position are valid
                assert animalViewModel.getAnimals() != null;
                assert position < animalViewModel.getAnimals().size();

                // Update the displayed animal information and selected index
                updateDisplayAnimal(position);
                animalSelected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // If nothing is selected, set the animalSelected to 0
                animalSelected = 0;
            }
        });

        // Set Edit Button Listener
        this.editButton.setOnClickListener(v -> {
            // Build Information Bundle for sharing state with EditAnimal Fragment
            Bundle bundle2 = new Bundle();
            bundle2.putInt("animal_selected", this.animalSelected);

            // Load new fragment with bundled information and switch to the other fragment
            fragment_edit fragment = new fragment_edit();
            fragment.setArguments(bundle2);
            FragmentChangeListener.replaceFragment(fragment);
        });

    }

    // Update the displayed animal's information based on its position
    private void updateDisplayAnimal(int position) {

        if (animalViewModel.getAnimals() != null && position < animalViewModel.getAnimals().size()) {
            // Get the selected animal from the ViewModel
            Animal animal = animalViewModel.getAnimals().get(position);

            // Update the avatar image and text views with the animal's data
            avatar.setImageResource(animal.getAvatar());
            String ownerText = animal.getOwner();
            String nameText  = animal.getName();
            String ageText = String.valueOf(animal.getAge());

            displayOwner.setText(ownerText);
            displayName.setText(nameText);
            displayAge.setText(ageText);
        }
    }
}