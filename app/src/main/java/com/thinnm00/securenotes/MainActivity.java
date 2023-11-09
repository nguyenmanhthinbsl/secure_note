package com.thinnm00.securenotes;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thinnm00.securenotes.adapter.NoteListAdapter;
import com.thinnm00.securenotes.databases.RoomDatabase;
import com.thinnm00.securenotes.models.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    RecyclerView recyclerView;
    NoteListAdapter noteListAdapter;
    List<Note> noteList = new ArrayList<>();
    RoomDatabase database;
    FloatingActionButton floatingActionButtonAdd;
    SearchView searchView;
    Note longClickNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycle_home);
        floatingActionButtonAdd = findViewById(R.id.float_add);
        searchView = findViewById(R.id.search_view);

        database = RoomDatabase.getInstance(this);
        noteList = database.noteDAO().getAll();
        Log.e("DEBUG:", String.valueOf("current size: " + noteList.size()));

        updateRecycle(noteList);

        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNote.class);
                startActivityForResult(intent, 101);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String newText) {
        List<Note> filteredNoteList = new ArrayList<>();
        for (Note note : noteList) {
            if (note.getTitle().toLowerCase().contains(newText.toLowerCase())
                    || note.getContent().toLowerCase().contains(newText.toLowerCase())) {
                filteredNoteList.add(note);
            }
        }

        noteListAdapter.fiteredNoteList(filteredNoteList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                Note note = (Note) data.getSerializableExtra("note");
                database.noteDAO().insertNote(note);

                //sync data
                noteList.clear();
                noteList.addAll(database.noteDAO().getAll());

                Log.e("DEBUG:", String.valueOf("after add 1, size: " + noteList.size()));
                noteListAdapter.notifyDataSetChanged();
            }
        }

        if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) {
                Note note = (Note) data.getSerializableExtra("note");
                database.noteDAO().update(note.getId(), note.getTitle(), note.getContent());

                //sync data
                noteList.clear();
                noteList.addAll(database.noteDAO().getAll());

                Log.e("DEBUG:", String.valueOf("after edit note success, size: " + noteList.size()));
                noteListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRecycle(List<Note> noteList) {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        noteListAdapter = new NoteListAdapter(MainActivity.this, noteList, noteClickListener);
        recyclerView.setAdapter(noteListAdapter);
    }

    private final NoteClickListener noteClickListener = new NoteClickListener() {
        @Override
        public void onClick(Note note) {
            Intent intent = new Intent(MainActivity.this, AddEditNote.class);
            intent.putExtra("edit_note", note);
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongClicK(Note note, CardView cardView) {
            longClickNote = new Note();
            longClickNote = note;

            showPopupMenu(cardView);
        }
    };

    private void showPopupMenu(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.long_click_pop_up);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.pin) {
            if (longClickNote.isPinned()) {
                database.noteDAO().pin(longClickNote.getId(), false);
                Toast.makeText(MainActivity.this, "Unpin Success!", Toast.LENGTH_SHORT).show();
            } else {
                database.noteDAO().pin(longClickNote.getId(), true);
                Toast.makeText(MainActivity.this, "Pin success!", Toast.LENGTH_SHORT).show();
            }
            noteList.clear();
            noteList.addAll(database.noteDAO().getAll());
            noteListAdapter.notifyDataSetChanged();
            return true;
        } else if (item.getItemId() == R.id.movetotrash) {
            database.noteDAO().moveToTrash(longClickNote.getId());
            Toast.makeText(MainActivity.this, "Move to trash success!", Toast.LENGTH_SHORT).show();
            noteList.clear();
            noteList.addAll(database.noteDAO().getAll());
            noteListAdapter.notifyDataSetChanged();
        }
        return false;
    }

}