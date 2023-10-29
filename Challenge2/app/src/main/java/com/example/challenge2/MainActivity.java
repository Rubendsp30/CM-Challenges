package com.example.challenge2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements FragmentChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new LoginFragment(), "fragment_login");
        //loadFragment(new ListNotesFragment(), "fragment_list_notes");
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
}