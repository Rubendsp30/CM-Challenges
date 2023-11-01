package com.example.challenge2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
    private User loggedInUser;
    private String title;
    private String docId;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the dialog as you did before

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.note_pop_up, null);

        Button editPopUpButton = dialogView.findViewById(R.id.editPopUpButton);
        Button erasePopUpButton = dialogView.findViewById(R.id.erasePopUpButton);

        Bundle args = getArguments();
        if (args != null) {
            loggedInUser = (User) args.getSerializable("loggedInUser");
            title = (String) args.getSerializable("title");
            docId = (String) args.getSerializable("docId");
        }

        builder.setView(dialogView);

        erasePopUpButton.setOnClickListener( (v)-> eraseNote());

        // Show the overlay when the dialog is displayed
        ((ListNotesFragment) getParentFragment()).showOverlay();

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        // Hide the overlay when the dialog is dismissed
        ((ListNotesFragment) getParentFragment()).hideOverlay();
    }

    void eraseNote(){
        DocumentReference documentReference;
        documentReference = FirebaseFirestore.getInstance().collection("notes").document(loggedInUser.getUsername()).collection("my_notes").document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Note deleted successfully", Toast.LENGTH_SHORT).show();
                    refreshAdapter();
                    dismiss();
                }else{
                    Toast.makeText(getContext(), "Failed while deleting note", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    void refreshAdapter() {
        FirestoreRecyclerAdapter<Note, NotesAdapter.NotesViewHolder> adapter = ((ListNotesFragment) getParentFragment()).getNotesAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}