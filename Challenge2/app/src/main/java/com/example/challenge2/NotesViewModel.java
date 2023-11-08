package com.example.challenge2;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotesViewModel extends ViewModel {

    private FirebaseFirestore firestore;
    private MutableLiveData<List<Note>> notesLiveData = new MutableLiveData<>();
    private ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
    private Handler uiHandler = new Handler(Looper.getMainLooper());

    public NotesViewModel() {
        try {
            firestore = FirebaseFirestore.getInstance();
        } catch (Exception e) {
            Log.e("NotesViewModel", "Error initializing NotesViewModel: " + e.getMessage());
        }
    }


    public LiveData<List<Note>> getNotes(String username) {
        Log.e("GetNotes", "Enter getNotes"); // Log when entering the method
        // Offload Firestore query to the background thread
        networkExecutor.execute(() -> {
            firestore.collection("notes")
                    .document(username)
                    .collection("my_notes")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Note> notes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Note note = document.toObject(Note.class);
                            notes.add(note);

                            Log.d("GetNotes", "Retrieved note: " + note.getTitle());
                        }

                        // Update the UI with the retrieved notes on the main (UI) thread
                        uiHandler.post(() -> {
                            notesLiveData.setValue(notes);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure, e.g., log the error or show a message
                        Log.e("GetNotes", "Failed to retrieve notes: " + e.getMessage());
                    });
        });

        return notesLiveData;
    }

    public void addOrUpdateNote(String username, Note note, String docId) {
        if (docId == null || docId.isEmpty()) {
            Log.e("Add note", "Adding note " );
            addNote(username, note);
        } else {
            Log.e("Udpate note", "Updating note " );
            updateNote(username, docId, note);
        }
    }
    public void addNote(String username, Note note) {
        // Offload Firestore write operation to the background thread
        networkExecutor.execute(() -> {
            DocumentReference documentReference;
            documentReference = FirebaseFirestore.getInstance().collection("notes").document(username).collection("my_notes").document();
            String newNoteId = documentReference.getId();
            note.setNoteId(newNoteId);

           documentReference.set(note).addOnSuccessListener(aVoid -> {
                       // Handle success, e.g., log the reference or show a success message
                   })
                   .addOnFailureListener(e -> {
                       // Handle failure, e.g., log the error or show an error message
                   });
        });
    }

    private void updateNote(String username, String docId, Note note) {
        // Offload Firestore update operation to the background thread
        networkExecutor.execute(() -> {
            DocumentReference documentReference;
            documentReference = FirebaseFirestore.getInstance().collection("notes").document(username).collection("my_notes").document(docId);
            note.setNoteId(docId);
            firestore.collection("notes")
                    .document(username)
                    .collection("my_notes")
                    .document(docId)
                    .set(note)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success, e.g., log the reference or show a success message
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure, e.g., log the error or show an error message
                    });
        });
    }

    //Maybe mix updateNote and updateNoteTitle in one function
    public void updateNoteTitle(String username, String docId, String newTitle) {
        // Offload Firestore update operation to the background thread
        networkExecutor.execute(() -> {
            DocumentReference documentReference;
            documentReference = FirebaseFirestore.getInstance().collection("notes").document(username).collection("my_notes").document(docId);

            firestore.collection("notes")
                    .document(username)
                    .collection("my_notes")
                    .document(docId)
                    .update("title", newTitle)
                    .addOnSuccessListener(aVoid -> {
                        //Temporary Fix
                        getNotes(username);
                        // Handle success, e.g., log the reference or show a success message
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure, e.g., log the error or show an error message
                    });
        });
    }

    public void deleteNote(String username, String docId) {
        // Offload Firestore delete operation to the background thread
        networkExecutor.execute(() -> {
            firestore.collection("notes")
                    .document(username)
                    .collection("my_notes")
                    .document(docId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Handle success, e.g., log the reference or show a success message
                        // Update notesLiveData after deleting the note
                        List<Note> currentNotes = notesLiveData.getValue();
                        if (currentNotes != null) {
                            // Find and remove the deleted note from the list
                            for (Note note : currentNotes) {
                                if (note.getNoteId().equals(docId)) {
                                    currentNotes.remove(note);
                                    break; // Assuming docId is unique
                                }
                            }
                            // Update the LiveData with the modified list
                            notesLiveData.postValue(currentNotes);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure, e.g., log the error or show an error message
                    });
        });
    }


    public void clearNotes() {
        // Clear the notes data. You can implement this based on how you store and manage notes in your ViewModel.
        // For example, if you have a LiveData<List<Note>> to store notes, you can set it to an empty list.
        List<Note> emptyList = new ArrayList<>();
        notesLiveData.setValue(emptyList);
    }
}
