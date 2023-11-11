package com.example.challenge2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListNotesFragment extends Fragment {

    private NotesViewModel notesViewModel;
    private User loggedInUser; // Currently logged-in user
    private NotesAdapter notesAdapter; // Adapter for managing notes data
    @Nullable private FragmentChangeListener FragmentChangeListener; // Listener for fragment changes
    private View overlayView; // Overlay view for UI interaction

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_notes, container, false); // Inflate the fragment's layout
        this.FragmentChangeListener = (MainActivity) inflater.getContext(); // Set the fragment change listener

        Toolbar fragmentToolbar = v.findViewById(R.id.toolbarList); // Initialize the toolbar

        ((AppCompatActivity) requireActivity()).setSupportActionBar(fragmentToolbar); // Set the toolbar as the action bar for this fragment
        setHasOptionsMenu(true); // Enable menu for this fragment

        try {
            notesViewModel = new ViewModelProvider(requireActivity()).get(NotesViewModel.class);
        } catch (Exception e) {
            Log.e("ListNotesFragment", "Error creating NotesViewModel: " + e.getMessage());
        }


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle args = getArguments();
        if (args != null) {
            loggedInUser = (User) args.getSerializable("loggedInUser"); // Retrieve the logged-in user from arguments
        }


        notesAdapter = new NotesAdapter(FragmentChangeListener, loggedInUser, getChildFragmentManager(),notesViewModel.getNotes(loggedInUser.getUsername(),requireContext()),notesViewModel);

        setupRecyclerView(view); // Initialize and set up the RecyclerView to display notes
    }

    public void setupRecyclerView(@NonNull View view){
        // Recycler View Layout Manager
        LinearLayoutManager notesLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );

        // RecyclerView for displaying notes
        RecyclerView notesListRecycler = view.findViewById(R.id.notesListRecycler); // Initialize the RecyclerView
        notesListRecycler.setLayoutManager(notesLayoutManager);
        this.overlayView = view.findViewById(R.id.overlayView); // Initialize the overlay view
        notesListRecycler.setAdapter(notesAdapter);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_bar, menu); // Inflate the menu for this fragment
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Set up a listener for the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search when submit button is pressed
                notesAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the notes when the search text changes
                notesAdapter.getFilter().filter(newText);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_note) {
            //Create new Note
            if (FragmentChangeListener != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("loggedInUser", loggedInUser);
                EditNoteFragment fragment = new EditNoteFragment();
                fragment.setArguments(bundle);
                FragmentChangeListener.replaceFragment(fragment); // Replace the current fragment with the EditNoteFragment
            } else {
                Log.e("ListNotesFragment-New Note", "FragmentChangeListener is null. Unable to replace the fragment.");
            }

        } else if (item.getItemId() == R.id.action_log_out) {
            //Log Out
            if (notesViewModel != null) {
                notesViewModel.clearNotes(); //clear the notes
            }
            if (FragmentChangeListener != null) {
                FragmentChangeListener.replaceFragment(new LoginFragment()); // Replace the current fragment with the LoginFragment
            } else {
                Log.e("ListNotesFragment-Log Out", "FragmentChangeListener is null. Unable to replace the fragment.");
            }

        }
        return true;
    }

    // Methods to show and hide overlay when pop up is active
    public void showOverlay() {
        overlayView.setVisibility(View.VISIBLE);
    }

    public void hideOverlay() {
        overlayView.setVisibility(View.GONE);
    }

    /*public RecyclerView getNotesRecyclerView() {
        return notesListRecycler;
    }*/

}
