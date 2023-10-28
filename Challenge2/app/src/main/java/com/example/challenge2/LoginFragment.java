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
    private Button tempSeeUsers;

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
        this.tempSeeUsers = view.findViewById(R.id.tempSeeUsers);

        createAccountButton.setOnClickListener(v -> {
            goToRegisterDisplay();
        });

        tempSeeUsers.setOnClickListener(v -> {
            goToUsersDisplay();
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

    private void Login() {

    }

}