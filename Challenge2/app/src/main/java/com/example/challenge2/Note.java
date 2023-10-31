package com.example.challenge2;

public class Note {

    private String title;
    private String body;
    private String owner;

    public Note(String title, String body, String owner) {
        this.title = title;
        this.body = body;
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
