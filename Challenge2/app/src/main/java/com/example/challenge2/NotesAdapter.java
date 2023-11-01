package com.example.challenge2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class NotesAdapter extends FirestoreRecyclerAdapter<Note,NotesAdapter.NotesViewHolder> {
    Context context;
    @Nullable
    private FragmentChangeListener FragmentChangeListener;
    private User loggedInUser;

    public NotesAdapter(@NonNull FirestoreRecyclerOptions<Note> options,Context context, FragmentChangeListener FragmentChangeListener, User loggedInUser) {
        super(options);
        this.context= context;
        this.FragmentChangeListener = FragmentChangeListener;
        this.loggedInUser = loggedInUser;

    }

    @Override
    protected void onBindViewHolder(@NonNull NotesViewHolder holder, int position, @NonNull Note note) {
        holder.noteTitleCard.setText(note.getTitle());
        holder.noteBodyCard.setText(note.getBody());

        holder.itemView.setOnClickListener((v)->{
            Bundle bundle = new Bundle();
            bundle.putSerializable("loggedInUser", loggedInUser);
            bundle.putSerializable("title", note.getTitle());
            bundle.putSerializable("body", note.getBody());
            String docId = this.getSnapshots().getSnapshot(position).getId();
            bundle.putSerializable("docId", docId);
            EditNoteFragment fragment = new EditNoteFragment();
            fragment.setArguments(bundle);
            if (FragmentChangeListener != null) {
                FragmentChangeListener.replaceFragment(fragment);
            } else {
                Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();
            }
        });
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

        public NotesViewHolder(@NonNull View noteView) {
            super(noteView);
            this.noteTitleCard = noteView.findViewById(R.id.noteTitleCard);
            this.noteBodyCard = noteView.findViewById(R.id.noteBodyCard);
        }

    }

}
