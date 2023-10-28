package com.example.challenge2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {

    private Button backLoginButton;
    private TextView usersInfo;
    @Nullable
    private FragmentChangeListener FragmentChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        // Initialize the FragmentChangeListener
        this.FragmentChangeListener = (MainActivity) inflater.getContext();
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.backLoginButton = view.findViewById(R.id.backLoginButton);
        this.usersInfo = view.findViewById(R.id.usersInfo);

        displayUsers();
        backLoginButton.setOnClickListener(v -> {
            goToLoginDisplay();
        });
    }

    private void goToLoginDisplay() {

        Bundle bundle = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(bundle);
        FragmentChangeListener.replaceFragment(fragment);

    }

    private void displayUsers() {

        File file = new File(getContext().getFilesDir(), "out.txt");
        if (file.exists()) {
            List<String> userInfos = new ArrayList<>();

            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                while (true) {
                    try {
                        User user = (User) inputStream.readObject();
                        String userInfo = "Username: " + user.getUsername() + "\nPassword: " + user.getPassword() + "\n";
                        userInfos.add(userInfo);
                    } catch (EOFException e) {
                        break; // Reached the end of the file
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            // Convert the list of user information to a single string
            StringBuilder userInfoText = new StringBuilder();
            for (String userInfo : userInfos) {
                userInfoText.append(userInfo).append("\n");
            }

            // Display the user information in the TextView
            usersInfo.setText(userInfoText.toString());
        }

    }

}