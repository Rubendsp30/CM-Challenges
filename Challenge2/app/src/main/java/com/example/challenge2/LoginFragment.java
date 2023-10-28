package com.example.challenge2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


public class LoginFragment extends Fragment {

    private Button createAccountButton;

    // Listener for fragment change events
    @Nullable
    private FragmentChangeListener FragmentChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize the FragmentChangeListener
        this.FragmentChangeListener = (MainActivity) inflater.getContext();
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.createAccountButton = view.findViewById(R.id.createAccountButton);

        // Set Back Button Listener
        createAccountButton.setOnClickListener(v -> {
            goToRegisterDisplay();
        });
    }
    private void goToRegisterDisplay() {

        Bundle bundle = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(bundle);
        FragmentChangeListener.replaceFragment(fragment);

    }

    private void Login() {

       /* Bundle bundle = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(bundle);
        FragmentChangeListener.replaceFragment(fragment);*/
        File file = new File(getContext().getFilesDir(), "out.txt");
        if (file.exists()) {
            List<String> usernames = new ArrayList<>();

            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                while (true) {
                    try {
                        User user = (User) inputStream.readObject();
                        usernames.add(user.getUsername());
                    } catch (EOFException e) {
                        break; // Reached the end of the file
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            // Convert the list of usernames to a single string
            StringBuilder usernameText = new StringBuilder();
            for (String username : usernames) {
                usernameText.append(username).append("\n");
            }

            // Display the usernames in the TextView
           // appNameRegister.setText(usernameText.toString());
        }

    }

}