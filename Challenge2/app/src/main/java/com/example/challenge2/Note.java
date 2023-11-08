package com.example.challenge2;

import com.google.firebase.firestore.PropertyName;

public class Note {

    private String title;
    private String body;
    private String owner;
    private String noteID;

    public Note(String title, String body, String owner) {
        this.title = title;
        this.body = body;
        this.owner = owner;
    }

    public Note(String title, String body, String owner, String noteID) {
        this.title = title;
        this.body = body;
        this.owner = owner;
        this.noteID = noteID;
    }

    public Note() {
        // Required by Firestore to create instances of the class
    }

    @PropertyName("owner")
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @PropertyName("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    @PropertyName("body")
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNoteId() {
        return noteID;
    }

    public void setNoteId(String noteID) {
        this.noteID = noteID;
    }
}
