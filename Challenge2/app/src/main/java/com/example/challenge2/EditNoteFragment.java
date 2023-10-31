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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class EditNoteFragment extends Fragment {


    private EditText noteTitleEdit;
    private EditText noteBodyEdit;
    @Nullable private FragmentChangeListener FragmentChangeListener;

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
        this.noteTitleEdit = view.findViewById(R.id.noteTitleEdit);
        this.noteBodyEdit = view.findViewById(R.id.noteBodyEdit);
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
            saveNote();
            //Toast.makeText(getContext(), "Save note", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void saveNote(){
        String newNoteTitle = noteTitleEdit.getText().toString();
        String newNoteBody = noteBodyEdit.getText().toString();
        if(newNoteTitle==null || newNoteTitle.isEmpty()){
            noteTitleEdit.setError("Title is required");
            return;
        }

        Note newNote = new Note(newNoteTitle, newNoteBody, "Miguel");
        uploadNoteToFirebase(newNote);

    }

    public void uploadNoteToFirebase(Note note){

        DocumentReference documentReference;
        documentReference= FirebaseFirestore.getInstance().collection("notes").document("Miguel").collection("my_notes").document();

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                }else{
                    Exception e = task.getException();
                    Toast.makeText(getContext(), "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


}