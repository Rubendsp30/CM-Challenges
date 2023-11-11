package com.example.challenge2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class PopUpFragment extends DialogFragment {
    private EditText editTitlePopUp;
    private String loggedInUser;
    private String docId;
    private final NotesViewModel notesViewModel;

    public PopUpFragment(NotesViewModel notesViewModel) {
        this.notesViewModel = notesViewModel;
    }

    // This method is called to create and configure the dialog when this fragment is displayed.
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create an AlertDialog instance and set its appearance style.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.note_pop_up, null);

        // Find buttons in the dialog layout.
        this.editTitlePopUp = dialogView.findViewById(R.id.editTitlePopUp);
        Button erasePopUpButton = dialogView.findViewById(R.id.erasePopUpButton);
        Button savePopUpButton = dialogView.findViewById(R.id.savePopUpButton);
        savePopUpButton.setEnabled(false);

        loggedInUser = notesViewModel.getLogedUser();
        Note selectedNote = notesViewModel.getSelectedNote();
        docId = selectedNote.getNoteId();

        // Set the view for the AlertDialog.
        builder.setView(dialogView);

        editTitlePopUp.setText(selectedNote.getTitle());

        editTitlePopUp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                savePopUpButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Set a click listener for the erase button.
        erasePopUpButton.setOnClickListener((v) -> eraseNote());

        // Set a click listener for the save changes button.
        savePopUpButton.setOnClickListener((v) -> saveNewTitle());

        // Show an overlay in the parent fragment when the dialog is displayed.
        ((ListNotesFragment) getParentFragment()).showOverlay();

        return builder.create(); // Return the configured dialog.
    }

    // This method is called when the dialog is dismissed.
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        notesViewModel.setSelectedNote(new Note());
        // Hide the overlay in the parent fragment when the dialog is dismissed.
        ((ListNotesFragment) getParentFragment()).hideOverlay();
    }

    // Method to delete a note.
    void eraseNote() {
        // Check if docId is not empty
        if (docId != null && !docId.isEmpty()) {
            notesViewModel.deleteNote(loggedInUser, docId, requireContext());
            Toast.makeText(getContext(), "Note deleted successfully", Toast.LENGTH_SHORT).show();
            dismiss(); // Dismiss the dialog.
        } else {
            Toast.makeText(getContext(), "Invalid note ID", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveNewTitle() {
        String newNoteTitle = editTitlePopUp.getText().toString();
        if (newNoteTitle.isEmpty()) {
            editTitlePopUp.setError("Title is required");
            return;
        }

        notesViewModel.updateNote(loggedInUser, docId, newNoteTitle, null,requireContext());
        dismiss();
    }
}
