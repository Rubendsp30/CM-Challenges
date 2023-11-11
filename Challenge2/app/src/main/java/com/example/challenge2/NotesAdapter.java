package com.example.challenge2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

// This class represents an adapter for a RecyclerView that displays a list of notes.
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> implements Filterable {

    // FragmentChangeListener and FragmentManager are used for handling fragment changes and pop-ups.
    @Nullable private final FragmentChangeListener FragmentChangeListener;
    @Nullable private final FragmentManager fragmentManager;
    private final NotesViewModel viewModel;

    private final User loggedInUser;// The logged-in user for whom the notes are being displayed.
    private LiveData<List<Note>> notesLiveData ;
    private List<Note> filteredNotesList;

    // Constructor for the NotesAdapter class.
    public NotesAdapter(@Nullable FragmentChangeListener FragmentChangeListener, User loggedInUser, @Nullable FragmentManager fragmentManager,  LiveData<List<Note>> notesLiveData, NotesViewModel viewModel) {
        this.notesLiveData = notesLiveData;
        this.FragmentChangeListener = FragmentChangeListener;
        this.loggedInUser = loggedInUser;
        this.fragmentManager = fragmentManager;
        this.viewModel = viewModel;

        // Add an Observer to the LiveData to listen for dataset changes.
        notesLiveData.observeForever(new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                filteredNotesList = notes;
                notifyDataSetChanged(); // Notify the adapter that the dataset has changed.
            }
        });
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Note> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(notesLiveData.getValue());
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Note note : notesLiveData.getValue()) {
                        if (note.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(note);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredNotesList = (List<Note>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    // Called when binding data to a ViewHolder in the RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NotesViewHolder holder, int position) {

        // Get the note at the specified position from the notesLiveData list
        List<Note> notesList = notesLiveData.getValue();

        if (notesList != null && position >= 0 && position < notesList.size()) {
            Note itemNote = filteredNotesList.get(position);

            // Use the itemNote to populate the ViewHolder views.
            holder.noteTitleCard.setText(itemNote.getTitle());
            holder.noteBodyCard.setText(itemNote.getBody());

            holder.itemView.setOnClickListener((v) -> {
                // Create a bundle to pass data to a fragment.
                Bundle bundle = new Bundle();
                bundle.putSerializable("loggedInUser", loggedInUser);
                bundle.putSerializable("title", itemNote.getTitle());
                bundle.putSerializable("body", itemNote.getBody());
                bundle.putSerializable("docId", itemNote.getNoteId());
                EditNoteFragment fragment = new EditNoteFragment();
                fragment.setArguments(bundle);

                // Replace the current fragment with the EditNoteFragment.
                if (FragmentChangeListener != null) {
                    FragmentChangeListener.replaceFragment(fragment);
                }
            });

            holder.itemView.setOnLongClickListener((v) -> {
                // Create a bundle to pass data to a pop-up fragment.
                Bundle bundle = new Bundle();
                bundle.putSerializable("loggedInUser", loggedInUser);
                bundle.putSerializable("title", itemNote.getTitle());
                bundle.putSerializable("docId", itemNote.getNoteId());
                PopUpFragment fragment = new PopUpFragment(viewModel);
                fragment.setArguments(bundle);

                // Show the pop-up fragment using the FragmentManager.
                if (fragmentManager != null) {
                    fragment.show(fragmentManager, "PopUpFragment");
                }

                return true;
            });
        }

    }

    //Verificar isto
    @Override
    public int getItemCount() {

        if (filteredNotesList != null) {
            return filteredNotesList.size();
        } else {
            return 0;
        }
    }

    // Create a new ViewHolder for the RecyclerView items.
    @NonNull
    @Override
    public NotesAdapter.NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
