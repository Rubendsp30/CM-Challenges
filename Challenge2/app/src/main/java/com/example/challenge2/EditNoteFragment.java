package com.example.challenge2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class EditNoteFragment extends Fragment {

    @Nullable
    private FragmentChangeListener FragmentChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_note, container, false);
        Toolbar fragmentToolbar = v.findViewById(R.id.toolbarEdit);
        this.FragmentChangeListener = (MainActivity) inflater.getContext();
        ((AppCompatActivity) requireActivity()).setSupportActionBar(fragmentToolbar);
        setHasOptionsMenu(true); // Enable menu for this fragment
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_back) {

            Bundle bundle = new Bundle();
            ListNotesFragment fragment = new ListNotesFragment();
            fragment.setArguments(bundle);
            FragmentChangeListener.replaceFragment(fragment);

            //Toast.makeText(getContext(), "New note", Toast.LENGTH_SHORT).show();
        }else if (item.getItemId() == R.id.action_save_note) {

            Toast.makeText(getContext(), "Save note", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


}