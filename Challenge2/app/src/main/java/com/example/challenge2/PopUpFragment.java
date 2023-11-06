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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PopUpFragment extends DialogFragment {
    private EditText editTitlePopUp;
    private User loggedInUser;
    private String title;
    private String docId;

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

        // Get arguments passed to this fragment.
        Bundle args = getArguments();
        if (args != null) {
            loggedInUser = (User) args.getSerializable("loggedInUser");
            title = args.getString("title");
            docId = args.getString("docId");
        }

        // Set the view for the AlertDialog.
        builder.setView(dialogView);

        editTitlePopUp.setText(title);

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

        // Hide the overlay in the parent fragment when the dialog is dismissed.
        ((ListNotesFragment) getParentFragment()).hideOverlay();
    }

    // Method to delete a note.
    void eraseNote() {
        DocumentReference documentReference;
        documentReference = FirebaseFirestore.getInstance().collection("notes").document(loggedInUser.getUsername()).collection("my_notes").document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Note deleted successfully", Toast.LENGTH_SHORT).show();
                    refreshAdapter(); // Refresh the adapter after deleting a note.
                    dismiss(); // Dismiss the dialog.
                } else {
                    Toast.makeText(getContext(), "Failed while deleting note", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to refresh the FirestoreRecyclerAdapter after deleting a note.
    void refreshAdapter() {
        FirestoreRecyclerAdapter<Note, NotesAdapter.NotesViewHolder> adapter = ((ListNotesFragment) getParentFragment()).getNotesAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void saveNewTitle() {
        String newNoteTitle = editTitlePopUp.getText().toString();
        if (newNoteTitle.isEmpty()) {
            editTitlePopUp.setError("Title is required");
            return;
        }

        // Upload the note data to Firestore
        DocumentReference documentReference;
        documentReference = FirebaseFirestore.getInstance().collection("notes").document(loggedInUser.getUsername()).collection("my_notes").document(docId);

        documentReference.update("title", newNoteTitle).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Exception e = task.getException();
                if (e != null) {
                    Toast.makeText(getContext(), "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Upload Failed with no error message.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
