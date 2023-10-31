package com.example.challenge2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class NotesAdapter extends FirestoreRecyclerAdapter<Note,NotesAdapter.NotesViewHolder> {
    Context context;

    public NotesAdapter(@NonNull FirestoreRecyclerOptions<Note> options,Context context) {
        super(options);
        this.context= context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NotesViewHolder holder, int position, @NonNull Note note) {
        holder.noteTitleCard.setText(note.getTitle());
        holder.noteBodyCard.setText(note.getBody());
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card,parent,false);
        return new NotesViewHolder(view);
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {
        public final TextView noteTitleCard;
        public final TextView noteBodyCard;

        public NotesViewHolder(@NonNull View view) {
            super(view);
            this.noteTitleCard = view.findViewById(R.id.noteTitleCard);
            this.noteBodyCard = view.findViewById(R.id.noteBodyCard);
        }

    }

}
