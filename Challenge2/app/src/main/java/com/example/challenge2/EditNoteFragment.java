package com.example.challenge2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class EditNoteFragment extends Fragment {

    private EditText noteTitleEdit;
    private EditText noteBodyEdit;
    private String loggedInUser;
    private String docId;
    @Nullable private FragmentChangeListener FragmentChangeListener;
    private NotesViewModel notesViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment's layout, set the toolbar, and enable the menu
        View v = inflater.inflate(R.layout.fragment_edit_note, container, false);
        Toolbar fragmentToolbar = v.findViewById(R.id.toolbarEdit);
        this.FragmentChangeListener = (MainActivity) inflater.getContext();
        ((AppCompatActivity) requireActivity()).setSupportActionBar(fragmentToolbar);
        setHasOptionsMenu(true); // Enable menu for this fragment

        notesViewModel = new ViewModelProvider(requireActivity()).get(NotesViewModel.class);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.noteTitleEdit = view.findViewById(R.id.noteTitleEdit);
        this.noteBodyEdit = view.findViewById(R.id.noteBodyEdit);

        // Retrieve arguments passed to the fragment
        Note selectedNote = notesViewModel.getSelectedNote();

        loggedInUser = notesViewModel.getLogedUser();
        docId = selectedNote.getNoteId();

        // Set the text fields with the retrieved data
        if(docId != null){
            noteTitleEdit.setText(selectedNote.getTitle());
            noteBodyEdit.setText(selectedNote.getBody());
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        // Inflate the menu for this fragment
        inflater.inflate(R.menu.edit_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item selections
        if (item.getItemId() == R.id.action_back) {
            goBackToListNotes();
        } else if (item.getItemId() == R.id.action_save_note) {
            saveNote();
        }
        return true;
    }

    private void goBackToListNotes() {
        // Navigate back to the list of notes
        if (FragmentChangeListener != null) {
            ListNotesFragment fragment = new ListNotesFragment();
            FragmentChangeListener.replaceFragment(fragment);
        } else {
            Log.e("EditNotesFragment", "FragmentChangeListener is null. Unable to replace the fragment.");
        }
    }

    public void saveNote() {
        // Save the note to Firebase after validation
        String newNoteTitle = noteTitleEdit.getText().toString();
        String newNoteBody = noteBodyEdit.getText().toString();
        if (newNoteTitle.isEmpty()) {
            noteTitleEdit.setError("Title is required");
            return;
        }
        Note newNote = new Note(newNoteTitle, newNoteBody, loggedInUser);

        if (docId == null || docId.isEmpty()) {
            notesViewModel.addNote(loggedInUser, newNote, requireContext());
        } else {
            notesViewModel.updateNote(loggedInUser, docId, newNoteTitle, newNoteBody,requireContext());
        }
        goBackToListNotes();
    }

}
