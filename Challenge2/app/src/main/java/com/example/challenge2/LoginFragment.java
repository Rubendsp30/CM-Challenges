package com.example.challenge2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import androidx.lifecycle.ViewModelProvider;

public class LoginFragment extends Fragment {

    private EditText usernameLogin;
    private EditText passwordLogin;
    @Nullable private FragmentChangeListener FragmentChangeListener;
    private NotesViewModel notesViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the login fragment layout
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize the FragmentChangeListener
        this.FragmentChangeListener = (MainActivity) inflater.getContext();
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements and set click listeners
        Button createAccountButton = view.findViewById(R.id.createAccountButton);
        Button tempSeeUsers = view.findViewById(R.id.tempSeeUsers);
        Button loginButton = view.findViewById(R.id.loginButton);
        this.usernameLogin = view.findViewById(R.id.usernameLogin);
        this.passwordLogin = view.findViewById(R.id.passwordLogin);

        createAccountButton.setOnClickListener(v -> goToRegisterDisplay());
        tempSeeUsers.setOnClickListener(v -> goToUsersDisplay());
        loginButton.setOnClickListener(v -> login());
    }

    private void goToRegisterDisplay() {
        // Navigate to the registration fragment

        if (FragmentChangeListener != null) {
            FragmentChangeListener.replaceFragment(new RegisterFragment());
        } else {
            // Handle the case where FragmentChangeListener is null
            Log.e("LoginFragment-goToRegisterDisplay", "FragmentChangeListener is null. Unable to replace the fragment.");
        }
    }

    private void goToUsersDisplay() {
        // Navigate to the users fragment

        if (FragmentChangeListener != null) {
            FragmentChangeListener.replaceFragment(new UsersFragment());
        } else {
            // Handle the case where FragmentChangeListener is null
            Log.e("LoginFragment-goToUsersDisplay", "FragmentChangeListener is null. Unable to replace the fragment.");
        }
    }

    private void login() {
        // Handle the login process

        String username = usernameLogin.getText().toString();
        String password = passwordLogin.getText().toString();

        List<User> users = readUsersFromFile();

        User loggedInUser = findUserInList(users, username, password);

        if (loggedInUser != null) {
            // If login is successful, navigate to the list notes fragment
            navigateToListNotesFragment(loggedInUser);
        } else {
            // Display a login failed message
            Toast.makeText(getContext(), "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
        }
    }

    private List<User> readUsersFromFile() {
        // Read user data from a file and return a list of users

        List<User> users = new ArrayList<>();

        try {
            Context context = getContext();
            File file = null;

            if (context != null) {
                file = new File(context.getFilesDir(), "users.txt");
            }

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

    private User findUserInList(List<User> users, String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    private void navigateToListNotesFragment(User loggedInUser) {
        if (FragmentChangeListener != null) {
            try {
                notesViewModel = new ViewModelProvider(requireActivity()).get(NotesViewModel.class);
                notesViewModel.setNetworkAvailable(isNetworkAvailable());
            } catch (Exception e) {
                Log.e("LoginFragment", "Error creating NotesViewModel: " + e.getMessage());
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable("loggedInUser", loggedInUser);
            ListNotesFragment fragment = new ListNotesFragment();
            fragment.setArguments(bundle);
            FragmentChangeListener.replaceFragment(fragment);
        } else {
            Log.e("LoginFragment-login", "FragmentChangeListener is null. Unable to replace the fragment.");
        }
    }

    // Method to check if the network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        return false;
    }
}
