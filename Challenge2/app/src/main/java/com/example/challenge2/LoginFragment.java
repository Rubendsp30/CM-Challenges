package com.example.challenge2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


public class LoginFragment extends Fragment {

    private Button createAccountButton;
    private Button loginButton;
    private Button tempSeeUsers;
    private EditText usernameLogin;
    private EditText passwordLogin;
    @Nullable private FragmentChangeListener FragmentChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        this.FragmentChangeListener = (MainActivity) inflater.getContext();
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.createAccountButton = view.findViewById(R.id.createAccountButton);
        this.tempSeeUsers = view.findViewById(R.id.tempSeeUsers);
        this.loginButton = view.findViewById(R.id.loginButton);
        this.usernameLogin = view.findViewById(R.id.usernameLogin);
        this.passwordLogin = view.findViewById(R.id.passwordLogin);

        createAccountButton.setOnClickListener(v -> {
            goToRegisterDisplay();
        });

        tempSeeUsers.setOnClickListener(v -> {
            goToUsersDisplay();
        });

        loginButton.setOnClickListener(v -> {
            login();
        });
    }
    private void goToRegisterDisplay() {

        Bundle bundle = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(bundle);
        FragmentChangeListener.replaceFragment(fragment);

    }

    private void goToUsersDisplay() {

        Bundle bundle = new Bundle();
        UsersFragment fragment = new UsersFragment();
        fragment.setArguments(bundle);
        FragmentChangeListener.replaceFragment(fragment);

    }

    private void login() {
        String username = usernameLogin.getText().toString();
        String password = passwordLogin.getText().toString();

        List<User> users = readUsersFromFile();
        User loggedInUser = null;

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                loggedInUser = user;
                break;
            }
        }

        if (loggedInUser != null) {

            Bundle bundle = new Bundle();
            bundle.putSerializable("loggedInUser", loggedInUser);
            ListNotesFragment fragment = new ListNotesFragment();
            fragment.setArguments(bundle);
            FragmentChangeListener.replaceFragment(fragment);

        } else {
            Toast.makeText(getContext(), "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
        }
    }


    private List<User> readUsersFromFile() {
        List<User> users = new ArrayList<>();

        try {
            File file = new File(getContext().getFilesDir(), "out.txt");
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            while (true) {
                try {
                    User user = (User) objectInputStream.readObject();
                    users.add(user);
                } catch (EOFException e) {
                    break;
                }
            }

            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return users;
    }


}