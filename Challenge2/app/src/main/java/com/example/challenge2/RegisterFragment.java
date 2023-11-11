package com.example.challenge2;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RegisterFragment extends Fragment {

    private EditText usernameRegister;
    private EditText passwordRegister;
    private EditText confirmPasswordRegister;
    @Nullable private FragmentChangeListener FragmentChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Get FragmentChangeListener from the parent activity
        this.FragmentChangeListener = (MainActivity) inflater.getContext();
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        ImageButton backButton = view.findViewById(R.id.backButton);
        this.usernameRegister = view.findViewById(R.id.usernameRegister);
        this.passwordRegister = view.findViewById(R.id.passwordRegister);
        this.confirmPasswordRegister = view.findViewById(R.id.confirmPasswordRegister);
        Button registerButton = view.findViewById(R.id.registerButton);

        // Add text change listeners for input validation
        usernameRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateUsernameLength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        passwordRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswordLength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        confirmPasswordRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateConfirmPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Set Back Button Listener
        backButton.setOnClickListener(v -> goToLoginDisplay());

        // Set Register Button Listener
        registerButton.setOnClickListener(v -> {
            if (validateAllInput()) {
                try {
                    registerUser();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private boolean validateUsernameLength(String input) {
        if (input != null && input.length() >= 3 && input.length() <= 15) {
            return true;
        } else {
            usernameRegister.setError("Username too short/long");
            return false;
        }
    }

    private boolean validatePasswordLength(String input) {
        if (input != null && input.length() >= 5) {
            return true;
        } else {
            passwordRegister.setError("Password too short");
            return false;
        }
    }

    private boolean validateConfirmPassword(String input) {
        if (input.equals(passwordRegister.getText().toString())) {
            return true;
        } else {
            confirmPasswordRegister.setError("Password not identical");
            return false;
        }
    }

    private boolean validateAllInput() {
        boolean isUsernameValid = validateUsernameLength(usernameRegister.getText().toString());
        boolean isPasswordValid = validatePasswordLength(passwordRegister.getText().toString());
        boolean isConfirmPasswordValid = validateConfirmPassword(confirmPasswordRegister.getText().toString());

        return isUsernameValid && isPasswordValid && isConfirmPasswordValid;
    }

    private void goToLoginDisplay() {
        if (FragmentChangeListener != null) {
            LoginFragment fragment = new LoginFragment();
            FragmentChangeListener.replaceFragment(fragment);
        } else {
            // Handle the case where FragmentChangeListener is null
            Log.e("RegisterFragment", "FragmentChangeListener is null. Unable to replace the fragment.");
        }
    }

    private void registerUser() throws IOException {
        String newUsername = usernameRegister.getText().toString();
        String newPassword = passwordRegister.getText().toString();

        // Create a new User object
        User newUser = new User(newUsername, newPassword);

        Context context = getContext();
        File file = null;

        if (context != null) {
            file = new File(context.getFilesDir(), "users.txt");
        }

        // Check if the file exists, and create it if it doesn't
        if (file != null && !file.exists()) {
            boolean fileCreated = file.createNewFile();
            if (!fileCreated) {
                Log.e("RegisterFragment", "File creation failed for 'users.txt'");
            }
        }
        List<User> existingUsers = new ArrayList<>();

        // Read the existing user data from the file
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    User user = (User) inputStream.readObject();
                    existingUsers.add(user);
                } catch (EOFException e) {
                    break; // Reached the end of the file
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Check if the new user already exists
        boolean userExists = false;
        for (User user : existingUsers) {
            if (user.getUsername().equals(newUsername)) {
                userExists = true;
                break;
            }
        }

        if (!userExists) {
            // Append the new user
            existingUsers.add(newUser);

            // Write the updated user data back to the file
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file, false))) {
                for (User user : existingUsers) {
                    outputStream.writeObject(user);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            goToLoginDisplay();
        } else {
            usernameRegister.setError("Username already exists!");
        }
    }
}
