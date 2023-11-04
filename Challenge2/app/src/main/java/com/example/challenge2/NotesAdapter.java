package com.example.challenge2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

// This class represents an adapter for a RecyclerView that displays a list of notes.
public class NotesAdapter extends FirestoreRecyclerAdapter<Note,NotesAdapter.NotesViewHolder> {
    Context context;

    // FragmentChangeListener and FragmentManager are used for handling fragment changes and pop-ups.
    @Nullable private final FragmentChangeListener FragmentChangeListener;
    @Nullable private final FragmentManager fragmentManager;

    // The logged-in user for whom the notes are being displayed.
    private final User loggedInUser;

    // Constructor for the NotesAdapter class.
    public NotesAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context, @Nullable FragmentChangeListener FragmentChangeListener, User loggedInUser, @Nullable FragmentManager fragmentManager) {
        super(options);
        this.context = context;
        this.FragmentChangeListener = FragmentChangeListener;
        this.loggedInUser = loggedInUser;
        this.fragmentManager = fragmentManager;
    }

    // Called when binding data to a ViewHolder in the RecyclerView.
    @Override
    protected void onBindViewHolder(@NonNull NotesViewHolder holder, int position, @NonNull Note note) {
        // Set the note title and body in the ViewHolder.
        holder.noteTitleCard.setText(note.getTitle());
        holder.noteBodyCard.setText(note.getBody());

        // Define click behavior for the RecyclerView items.
        holder.itemView.setOnClickListener((v) -> {
            // Create a bundle to pass data to a fragment.
            Bundle bundle = new Bundle();
            bundle.putSerializable("loggedInUser", loggedInUser);
            bundle.putSerializable("title", note.getTitle());
            bundle.putSerializable("body", note.getBody());
            String docId = this.getSnapshots().getSnapshot(position).getId();
            bundle.putSerializable("docId", docId);
            EditNoteFragment fragment = new EditNoteFragment();
            fragment.setArguments(bundle);

            // Replace the current fragment with the EditNoteFragment.
            if (FragmentChangeListener != null) {
                FragmentChangeListener.replaceFragment(fragment);
            }
        });

        // Define long click behavior for the RecyclerView items.
        holder.itemView.setOnLongClickListener((v) -> {
            // Create a bundle to pass data to a pop-up fragment.
            Bundle bundle = new Bundle();
            bundle.putSerializable("loggedInUser", loggedInUser);
            bundle.putSerializable("title", note.getTitle());
            String docId = this.getSnapshots().getSnapshot(position).getId();
            bundle.putSerializable("docId", docId);
            PopUpFragment fragment = new PopUpFragment();
            fragment.setArguments(bundle);

            // Show the pop-up fragment using the FragmentManager.
            if (fragmentManager != null) {
                fragment.show(fragmentManager, "PopUpFragment");
            }

            return true;
        });
    }

    // Create a new ViewHolder for the RecyclerView items.
    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for the note card.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card, parent, false);
        return new NotesViewHolder(view);
    }

    // ViewHolder class for holding the views of each note card.
    public static class NotesViewHolder extends RecyclerView.ViewHolder {
        public final TextView noteTitleCard;
        public final TextView noteBodyCard;

        public NotesViewHolder(@NonNull View noteView) {
            super(noteView);
            // Initialize the TextViews for the title and body of the note.
            this.noteTitleCard = noteView.findViewById(R.id.noteTitleCard);
            this.noteBodyCard = noteView.findViewById(R.id.noteBodyCard);
        }
    }
}
