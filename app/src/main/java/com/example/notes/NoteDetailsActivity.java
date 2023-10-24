package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView,deleteNoteTextViewBtn;
    String title, content, docId;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewBtn = findViewById(R.id.de_note_text_view_btn);

        // Receive data
        title = getIntent().getStringExtra("Title");
        content = getIntent().getStringExtra("Content");
        docId = getIntent().getStringExtra("docId");

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }

        if (isEditMode) {
            // In edit mode, set the text fields with the received data
            titleEditText.setText(title);
            contentEditText.setText(content);
            pageTitleTextView.setText("Edit Note");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener(v -> saveNote());

        deleteNoteTextViewBtn.setOnClickListener(v -> deleteNoteFromFirebase(docId));

    }

    void saveNote() {
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();

        if (noteTitle.isEmpty()) {
            titleEditText.setError("Title is Required");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;
        if(isEditMode){
            //update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else {
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Note is added
                    Utility.showToast(NoteDetailsActivity.this, "Note added Successfully");
                    finish();
                } else {
                    // Note is not added
                    Utility.showToast(NoteDetailsActivity.this, "Failed while adding Note");
                }
            }
        });
    }


    void deleteNoteFromFirebase(String docId) {
        DocumentReference documentReference = Utility.getCollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Note is deleted
                    Utility.showToast(NoteDetailsActivity.this, "Note Deleted Successfully");
                    finish();
                } else {
                    // Unable to Delete Note
                    Utility.showToast(NoteDetailsActivity.this, "Failed while Deleting Note");
                }
            }
        });
    }


}
