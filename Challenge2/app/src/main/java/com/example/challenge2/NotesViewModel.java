package com.example.challenge2;

import android.content.Context;
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

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotesViewModel extends ViewModel {

    private FirebaseFirestore firestore;
    private final MutableLiveData<List<Note>> notesLiveData = new MutableLiveData<>();
    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private static final String NOTES_COLLECTION = "notes";
    private static final String MY_NOTES_COLLECTION = "my_notes";
    private boolean isNetworkAvailable;

    private Note selectedNote;
    private String loggedUser;

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

    public void setNetworkAvailable(boolean isNetworkAvailable,Context context) throws IOException, ClassNotFoundException {
        this.isNetworkAvailable = isNetworkAvailable;
        if (isNetworkAvailable) {
            uploadFromFile(context);
            Log.e("NotesViewModel", "Network is available");
        } else {
            Log.e("NotesViewModel", "Network is not available");
        }
    }

    public void setSelectedNote(Note note) {
        this.selectedNote = note;
    }
    public Note getSelectedNote() {
        return selectedNote;
    }

    public void setLogedUser(String username) {
        this.loggedUser = username;
    }
    public String getLogedUser() {
        return loggedUser;
    }
    private void uploadFromFile( Context context) throws IOException, ClassNotFoundException {

        File file = new File(context.getFilesDir(), "notes.txt");
        if (file.exists()) {
            List<Note> notes = new ArrayList<>();

            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                while (true) {
                    try {
                        Note note = (Note) inputStream.readObject();
                            notes.add(note);
                    } catch (EOFException e) {
                        break; // Reached the end of the file
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            for(Note note: notes){
                addNote(note.getOwner(), note, context);
            }
            file.delete();
        }
    }

    public LiveData<List<Note>> getNotes(String username, Context context) {
        if (isNetworkAvailable) {
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
                            Log.e("GetNotes", "Failed to retrieve notes: " + e.getMessage());
                        });
            });
        }else {
                // Handle the case when the network is not available
                List<Note> emptyList = new ArrayList<>();
                notesLiveData.setValue(emptyList);
            try {
                readFromFile(username, context);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            }
        return notesLiveData;
    }
    private void readFromFile(String username, Context context) throws IOException, ClassNotFoundException {

        File file = new File(context.getFilesDir(), "notes.txt");
        if (file.exists()) {
            List<Note> notes = new ArrayList<>();

            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                while (true) {
                    try {
                        Note note = (Note) inputStream.readObject();

                        if(note.getOwner().equals(username)){
                            notes.add(note);
                        }
                    } catch (EOFException e) {
                        break; // Reached the end of the file
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            notesLiveData.setValue(notes);
        }
    }

    public void addNote(String username, Note note, Context context) {
        if (isNetworkAvailable) {
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

                   })
                   .addOnFailureListener(e -> {
                       Log.e("AddNote", "Failed to add note: " + e.getMessage());
                   });
        });
        }else{
            try {
                writeToFile(note,context);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void writeToFile(Note newNote, Context context) throws IOException {
        File file = null;

        if (context != null) {
            file = new File(context.getFilesDir(), "notes.txt");
        }

        // Check if the file exists, and create it if it doesn't
        if (file != null && !file.exists()) {
            boolean fileCreated = file.createNewFile();
            if (!fileCreated) {
                Log.e("writeToFile", "File creation failed for 'notes.txt'");
            }
        }


        List<Note> existingNotes = new ArrayList<>();

        // Read the existing user data from the file
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    Note note = (Note) inputStream.readObject();
                    existingNotes.add(note);
                } catch (EOFException e) {
                    break; // Reached the end of the file
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        final int randomDocId = new Random().nextInt(99999999) + 999;
        newNote.setNoteId(Integer.toString(randomDocId));
        existingNotes.add(newNote);

        // Write the updated user data back to the file
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file, false))) {
            for (Note note : existingNotes) {
                outputStream.writeObject(note);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateNote(String username, String docId, String newTitle, String newBody,Context context) {
        if (isNetworkAvailable) {
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

                        uiHandler.post(() -> {
                            getNotes(username,context);
                        });

                    })
                    .addOnFailureListener(e -> {
                        Log.e("UpdateNote", "Failed to update note: " + e.getMessage());
                    });
        });
        } else {

            try {
                updateNoteToFile(username, docId, newTitle, newBody, context);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void updateNoteToFile(String username, String docId, String newTitle, String newBody, Context context)
            throws IOException, ClassNotFoundException {
        File file = new File(context.getFilesDir(), "notes.txt");

        if (file.exists()) {
            List<Note> updatedNotes = new ArrayList<>();

            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                while (true) {
                    try {
                        Note note = (Note) inputStream.readObject();
                        if (note.getOwner().equals(username) && note.getNoteId().equals(docId)) {
                            // Update the note locally
                            note.setTitle(newTitle);
                            if (newBody != null) {
                                note.setBody(newBody);
                            }
                        }
                        updatedNotes.add(note);
                    } catch (EOFException e) {
                        break; // Reached the end of the file
                    }
                }
            }

            // Write the updated user data back to the file
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file, false))) {
                for (Note note : updatedNotes) {
                    outputStream.writeObject(note);
                }
            }
                notesLiveData.postValue(updatedNotes);
        }
    }


    public void deleteNote(String username, String docId, Context context) {
        if(isNetworkAvailable){
        // Offload Firestore delete operation to the background thread
        networkExecutor.execute(() -> {
            firestore.collection(NOTES_COLLECTION)
                    .document(username)
                    .collection(MY_NOTES_COLLECTION)
                    .document(docId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
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
        } else {

            try {
                deleteNoteToFile(username, docId, context);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void deleteNoteToFile(String username, String docId, Context context)
            throws IOException, ClassNotFoundException {
        File file = new File(context.getFilesDir(), "notes.txt");

        if (file.exists()) {
            List<Note> updatedNotes = new ArrayList<>();

            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                while (true) {
                    try {
                        Note note = (Note) inputStream.readObject();
                        if (note.getOwner().equals(username) && note.getNoteId().equals(docId)) {
                            // Skip the note to delete it
                            continue;
                        }
                        updatedNotes.add(note);
                    } catch (EOFException e) {
                        break; // Reached the end of the file
                    }
                }
            }

            // Write the updated user data back to the file
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file, false))) {
                for (Note note : updatedNotes) {
                    outputStream.writeObject(note);
                }
            }

            // Update LiveData with the modified list
            uiHandler.post(() -> {
                notesLiveData.postValue(updatedNotes);
            });
        }
    }

    public void clearNotes() {
        // Clear the notes data stored in the View Model when doing LogOut
        List<Note> emptyList = new ArrayList<>();
        notesLiveData.setValue(emptyList);
    }
}
