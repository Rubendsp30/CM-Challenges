package com.example.challenge2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class ListNotesFragment extends Fragment {

    private RecyclerView notesListRecycler;
    private User loggedInUser;
    private NotesAdapter notesAdapter;
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
            loggedInUser = (User) args.getSerializable("loggedInUser");
        }

        setupRecyclerView();
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
            bundle.putSerializable("loggedInUser", loggedInUser);
            EditNoteFragment fragment = new EditNoteFragment();
            fragment.setArguments(bundle);
            FragmentChangeListener.replaceFragment(fragment);

        }
        return true;
    }

    public void setupRecyclerView(){
        Query query = FirebaseFirestore.getInstance().collection("notes").document(loggedInUser.getUsername()).collection("my_notes");
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query,Note.class).build();
        notesListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        notesAdapter= new NotesAdapter(options,getContext(),FragmentChangeListener,loggedInUser);
        notesListRecycler.setAdapter(notesAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (notesAdapter != null) {
            notesAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (notesAdapter != null) {
            notesAdapter.stopListening();
        }
    }

}