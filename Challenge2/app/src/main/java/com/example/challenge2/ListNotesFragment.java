package com.example.challenge2;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class ListNotesFragment extends Fragment {

    private RecyclerView notesListRecycler;
    @Nullable private FragmentChangeListener FragmentChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_notes, container, false);
        Toolbar fragmentToolbar = v.findViewById(R.id.toolbarList);
        this.FragmentChangeListener = (MainActivity) inflater.getContext();
        ((AppCompatActivity) requireActivity()).setSupportActionBar(fragmentToolbar);
        setHasOptionsMenu(true); // Enable menu for this fragment
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.notesListRecycler = view.findViewById(R.id.notesListRecycler);

        Bundle args = getArguments();
        if (args != null) {
            User loggedInUser = (User) args.getSerializable("loggedInUser");
            if (loggedInUser != null) {
                String username = loggedInUser.getUsername();
            }
        }

        List<Note> notes = new ArrayList<Note>();
        notes.add(new Note("Title Note 1", "Body Note 1","Owner"));
        notes.add(new Note("Title Note 2", "Body Note 2","Owner"));
        notes.add(new Note("Title Note 3", "Body Note 3","Owner"));
        notes.add(new Note("Title Note 4", "Body Note 4","Owner"));
        notes.add(new Note("Title Note 5", "Body Note 5","Owner"));
        notes.add(new Note("Title Note 6", "Body Note 6","Owner"));
        notes.add(new Note("Title Note 7", "Body Note 7","Owner"));
        notes.add(new Note("Title Note 8", "Body Note 8","Owner"));
        notes.add(new Note("Title Note 9", "Body Note 9","Owner"));
        notes.add(new Note("Title Note 10", "Body Note 10","Owner"));
        notes.add(new Note("Title Note 11", "Body Note 11","Owner"));
        notes.add(new Note("Title Note 12", "Body Note 12","Owner"));
        notes.add(new Note("Title Note 13", "Body Note 13","Owner"));
        notes.add(new Note("Title Note 14", "Body Note 14","Owner"));
        notes.add(new Note("Title Note 15", "Body Note 15","Owner"));
        notes.add(new Note("Title Note 16", "Body Note 16","Owner"));


        //notesListRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        notesListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        notesListRecycler.setAdapter(new NotesAdapter(requireContext(),notes));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_note) {

            Bundle bundle = new Bundle();
            EditNoteFragment fragment = new EditNoteFragment();
            fragment.setArguments(bundle);
            FragmentChangeListener.replaceFragment(fragment);

            //Toast.makeText(getContext(), "New note", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}