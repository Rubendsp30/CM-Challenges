package com.example.challenge2;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotesViewHolder extends RecyclerView.ViewHolder {
    public final TextView noteTitleCard;
    public final TextView noteBodyCard;

    public NotesViewHolder(@NonNull View view) {
        super(view);
        this.noteTitleCard = view.findViewById(R.id.noteTitleCard);
        this.noteBodyCard = view.findViewById(R.id.noteBodyCard);
    }

    /*public TextView getTitle() {
        return title;
    }

    public View getView() {
        return this.itemView;
    }*/
}
