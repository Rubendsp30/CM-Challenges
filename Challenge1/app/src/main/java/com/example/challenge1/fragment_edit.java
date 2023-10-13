package com.example.challenge1;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challenge1.util.Animal;
import com.example.challenge1.viewModel.AnimalViewModel;

public class fragment_edit extends Fragment {

    // ViewModel for managing animal data
    private AnimalViewModel animalViewModel;

    // Index of the currently selected animal
    private int animalSelected;

    // UI elements
    private ImageView avatar;
    private EditText editOwner;
    private EditText editName;
    private EditText editAge;
    private Button backButton;
    private Button saveButton;

    // Listener for fragment change events
    @Nullable
    private FragmentChangeListener FragmentChangeListener;

    // Declare a TextWatcher instance to reuse
    private TextWatcher inputTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateInputLength(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            validateAllInput();
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize the AnimalViewModel using the ViewModelProvider
        this.animalViewModel = new ViewModelProvider(requireActivity()).get(AnimalViewModel.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        // Initialize the FragmentChangeListener
        this.FragmentChangeListener = (MainActivity) inflater.getContext();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the selected animal index from the fragment arguments
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.animalSelected = bundle.getInt("animal_selected");
        }

        // Initialize UI elements by finding their views in the layout
        this.avatar =  view.findViewById(R.id.avatar_edit);
        this.editOwner = view.findViewById(R.id.edit_owner);
        this.editName =  view.findViewById(R.id.edit_name);
        this.editAge =  view.findViewById(R.id.edit_age);
        this.backButton = view.findViewById(R.id.back_button);
        this.saveButton = view.findViewById(R.id.save_button);

        // Add a TextChangedListener to editAge for input validation
        editAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateAgeInput(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateAllInput();
            }
        });

        // Add a TextChangedListener to editOwner for input validation
        editOwner.addTextChangedListener(inputTextWatcher);

        // Add a TextChangedListener to editName for input validation
        editName.addTextChangedListener(inputTextWatcher);

        assert animalViewModel.getAnimals() != null;

        if (animalSelected != -1) {
            Animal animal = animalViewModel.getAnimals().get(animalSelected);
            avatar.setImageResource(animal.getAvatar());
            editOwner.setText(animal.getOwner());
            editName.setText(animal.getName());
            editAge.setText(String.valueOf(animal.getAge()));
        }
        saveButton.setEnabled(false);

        // Set Save Button Listener
        saveButton.setOnClickListener(v -> {
            updateAnimalDetails();
        });

        // Set Back Button Listener
        backButton.setOnClickListener(v -> {
            goToAnimalDisplay();
        });
    }

    private void validateAllInput() {
        boolean isAgeValid = validateAgeInput(editAge.getText().toString());
        boolean isOwnerValid = validateInputLength(editOwner.getText().toString());
        boolean isNameValid = validateInputLength(editName.getText().toString());

        // Enable the "Save" button only when all input fields are valid
        saveButton.setEnabled(isAgeValid && isOwnerValid && isNameValid);
    }

    private boolean validateAgeInput(String input) {
        if (input != null && !input.isEmpty()) {
            try {
                int age = Integer.parseInt(input);
                if (age >= 0) {
                    // Valid input
                    return true;
                } else {
                    // Invalid input (e.g., negative age)
                    return false;
                }
            } catch (NumberFormatException e) {
                // Invalid input (not a valid integer)
                return false;
            }
        } else {
            // Empty or null input
            return false;
        }
    }

    private boolean validateInputLength(String input) {
        return input != null && !input.isEmpty() && input.length() <= 10;
    }


    private void updateAnimalDetails() {
        String newOwner = editOwner.getText().toString();
        String newName = editName.getText().toString();
        int newAge = Integer.parseInt(editAge.getText().toString());

        if (animalSelected != -1) {
            Animal animal = animalViewModel.getAnimals().get(animalSelected);
            animal.setOwner(newOwner);
            animal.setName(newName);
            animal.setAge(newAge);
        }
        Toast.makeText(getActivity(), "Saved!",
                Toast.LENGTH_LONG).show();
        //saveButton.setEnabled(false);
        goToAnimalDisplay();
    }

    private void goToAnimalDisplay() {

            Bundle bundle = new Bundle();
            bundle.putInt("animal_selected", animalSelected);
            getParentFragmentManager().setFragmentResult("animal", bundle);

            fragment_animal fragment = new fragment_animal();
            fragment.setArguments(bundle);
            FragmentChangeListener.replaceFragment(fragment);

    }

}