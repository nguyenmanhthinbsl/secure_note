package com.thinnm00.securenotes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.thinnm00.securenotes.models.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class AddEditNote extends AppCompatActivity {

    private static final String MSG_EMPTY_TITLE = "Title can be empty!";
    EditText edtTitle;
    EditText edtContent;
    ImageView imgviewSave;
    Note note;

    boolean isEditNoteActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add_edit);

        imgviewSave = findViewById(R.id.imgv_save);
        edtTitle = findViewById(R.id.edt_title);
        edtContent = findViewById(R.id.edt_content);

        //when click specific note in main -> edit
        //try  catch use only when editnote
        note = new Note();
        try {
            note = (Note) getIntent().getSerializableExtra("edit_note");
            edtTitle.setText(note.getTitle());
            edtContent.setText(note.getContent());
            isEditNoteActivity = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        imgviewSave.setOnClickListener(view -> {
            String title = edtTitle.getText().toString().trim();
            String content = edtContent.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(AddEditNote.this, MSG_EMPTY_TITLE, Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEditNoteActivity) {
                note.setTitle(title);
                note.setContent(content);
                Intent intent = new Intent();
                intent.putExtra("note", note);
                setResult(RESULT_OK, intent);
                finish();
            }
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
            Date date = new Date();

            note = new Note();
            note.setTitle(title);
            note.setContent(content);
            note.setCreateDate(formatter.format(date));
            note.setTrash(false);

            Intent intent = new Intent();
            intent.putExtra("note", note);
            setResult(Activity.RESULT_OK, intent);

            finish();

        });

    }

}