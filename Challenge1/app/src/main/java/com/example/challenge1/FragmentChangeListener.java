package com.example.challenge1;
// This code defines a Java interface called FragmentChangeListener in the "com.example.challenge1" package.
//Example from https://stackoverflow.com/questions/21228721/how-to-replace-a-fragment-on-button-click-of-that-fragment
import androidx.fragment.app.Fragment;

public interface FragmentChangeListener {
    void replaceFragment(Fragment fragment);
}
