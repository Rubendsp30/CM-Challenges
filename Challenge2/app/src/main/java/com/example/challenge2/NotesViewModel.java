package com.example.challenge2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotesViewModel extends ViewModel {

    private FirebaseFirestore firestore;
    private final MutableLiveData<List<Note>> notesLiveData = new MutableLiveData<>();
    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private static final String NOTES_COLLECTION = "notes";
    private static final String MY_NOTES_COLLECTION = "my_notes";

    public NotesViewModel() {
        try {
            firestore = FirebaseFirestore.getInstance();
            // Disable Firestore cache
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                    .build();
            firestore.setFirestoreSettings(settings);
        } catch (Exception e) {
            Log.e("NotesViewModel", "Error initializing NotesViewModel: " + e.getMessage());
        }
    }


    public LiveData<List<Note>> getNotes(String username) {
        // Offload Firestore query to the background thread
        networkExecutor.execute(() -> {
            firestore.collection(NOTES_COLLECTION)
                    .document(username)
                    .collection(MY_NOTES_COLLECTION)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Note> notes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Note note = document.toObject(Note.class);
                            notes.add(note);
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

    public void addNote(String username, Note note) {
        // Offload Firestore write operation to the background thread
        networkExecutor.execute(() -> {
            DocumentReference documentReference;
            documentReference = firestore.collection(NOTES_COLLECTION)
                    .document(username)
                    .collection(MY_NOTES_COLLECTION)
                    .document();
            String newNoteId = documentReference.getId();
            note.setNoteId(newNoteId);

           documentReference.set(note).addOnSuccessListener(aVoid -> {
                       // Handle success, e.g., log the reference or show a success message
                   })
                   .addOnFailureListener(e -> {
                       Log.e("AddNote", "Failed to add note: " + e.getMessage());
                   });
        });
    }

    public void updateNote(String username, String docId, String newTitle, String newBody) {
        // Offload Firestore update operation to the background thread
        networkExecutor.execute(() -> {
            DocumentReference noteRef = firestore.collection(NOTES_COLLECTION)
                    .document(username)
                    .collection(MY_NOTES_COLLECTION)
                    .document(docId);

            Map<String, Object> updates = new HashMap<>();
            updates.put("title", newTitle);

            if (newBody != null) {
                updates.put("body", newBody);
            }

            noteRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        // Temporary Fix
                        getNotes(username);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UpdateNote", "Failed to update note: " + e.getMessage());
                    });
        });
    }


    public void deleteNote(String username, String docId) {
        // Offload Firestore delete operation to the background thread
        networkExecutor.execute(() -> {
            firestore.collection(NOTES_COLLECTION)
                    .document(username)
                    .collection(MY_NOTES_COLLECTION)
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
                            uiHandler.post(() -> {
                                notesLiveData.postValue(currentNotes);
                            });

                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("DeleteNote", "Failed to delete note: " + e.getMessage());
                    });
        });
    }

    public void clearNotes() {
        // Clear the notes data stored in the View Model when doing LogOut
        List<Note> emptyList = new ArrayList<>();
        notesLiveData.setValue(emptyList);
    }
}
