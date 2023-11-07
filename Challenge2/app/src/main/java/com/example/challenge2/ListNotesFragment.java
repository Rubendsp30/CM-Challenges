package com.example.challenge2;

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
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ListNotesFragment extends Fragment {

    private RecyclerView notesListRecycler; // RecyclerView for displaying notes
    private User loggedInUser; // Currently logged-in user
    private NotesAdapter notesAdapter; // Adapter for managing notes data
    @Nullable private FragmentChangeListener FragmentChangeListener; // Listener for fragment changes
    private View overlayView; // Overlay view for UI interaction

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_notes, container, false); // Inflate the fragment's layout

        overlayView = v.findViewById(R.id.overlayView); // Initialize the overlay view

        Toolbar fragmentToolbar = v.findViewById(R.id.toolbarList); // Initialize the toolbar
        this.FragmentChangeListener = (MainActivity) inflater.getContext(); // Set the fragment change listener
        ((AppCompatActivity) requireActivity()).setSupportActionBar(fragmentToolbar); // Set the toolbar as the action bar for this fragment
        setHasOptionsMenu(true); // Enable menu for this fragment
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.notesListRecycler = view.findViewById(R.id.notesListRecycler); // Initialize the RecyclerView

        Bundle args = getArguments();
        if (args != null) {
            loggedInUser = (User) args.getSerializable("loggedInUser"); // Retrieve the logged-in user from arguments
        }

        setupRecyclerView(); // Initialize and set up the RecyclerView to display notes
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_bar, menu); // Inflate the menu for this fragment
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_note) {
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
            if (FragmentChangeListener != null) {
                FragmentChangeListener.replaceFragment(new LoginFragment()); // Replace the current fragment with the LoginFragment
            } else {
                Log.e("ListNotesFragment-Log Out", "FragmentChangeListener is null. Unable to replace the fragment.");
            }

        }
        return true;
    }

    public void setupRecyclerView(){
        Query query = FirebaseFirestore.getInstance().collection("notes").document(loggedInUser.getUsername()).collection("my_notes");
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note.class).build();
        notesListRecycler.setLayoutManager(new LinearLayoutManager(getContext())); // Set the layout manager for the RecyclerView
        notesAdapter = new NotesAdapter(options, getContext(), FragmentChangeListener, loggedInUser, getChildFragmentManager()); // Initialize the notes adapter
        notesListRecycler.setAdapter(notesAdapter); // Set the adapter for the RecyclerView
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (notesAdapter != null) {
            notesAdapter.startListening(); // Start listening for changes in Firestore data
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (notesAdapter != null) {
            notesAdapter.stopListening(); // Stop listening for changes when the fragment is not visible
        }
    }

    // Methods to show and hide overlay when pop up is active
    public void showOverlay() {
        overlayView.setVisibility(View.VISIBLE);
    }

    public void hideOverlay() {
        overlayView.setVisibility(View.GONE);
    }

    // Getter method to retrieve the notes adapter
    public FirestoreRecyclerAdapter<Note, NotesAdapter.NotesViewHolder> getNotesAdapter() {
        return notesAdapter;
    }
}
