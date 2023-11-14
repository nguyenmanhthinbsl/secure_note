package com.thinnm00.securenotes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thinnm00.securenotes.adapter.NoteListAdapter;
import com.thinnm00.securenotes.databases.RoomDatabase;
import com.thinnm00.securenotes.models.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private int backPressedCount = 0;
    public final int RECYCLER_SPAN_COUNT = 1;
    private static final int REQUEST_ADD_NOTE = 101;
    private static final int REQUEST_EDIT_NOTE = 102;
    RecyclerView recyclerView;
    NoteListAdapter noteListAdapter;
    List<Note> noteList = new ArrayList<>();
    RoomDatabase database;
    SearchView searchView;
    Note longClickNote;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycle_home);
        searchView = findViewById(R.id.search_view);

        database = RoomDatabase.getInstance(this);
        noteList = database.noteDAO().getAll();
        updateRecycle(noteList);
        setupBottomNavigationBar();

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


    private void setupBottomNavigationBar() {
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        // Handle navigation item clicks here
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                reloadMain();
                                break;
                            case R.id.action_add:
                                Intent intent = new Intent(MainActivity.this, AddEditNote.class);
                                startActivityForResult(intent, REQUEST_ADD_NOTE);
                                break;
                            case R.id.action_setting:

                                Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
                                startActivity(intentSetting);
                                break;
                        }
                        return true;
                    }
                });

        // Set the default selected item
        bottomNav.setSelectedItemId(R.id.action_home);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void reloadMain() {
        noteList.clear();
        noteList.addAll(database.noteDAO().getAll());
        noteListAdapter.notifyDataSetChanged();
    }

    private void filter(String keySearch) {
        List<Note> filteredNoteList = new ArrayList<>();
        /*
        if search only space -> trim
         */
        if (keySearch.trim().isEmpty()) {
            noteListAdapter.fiteredNoteList(noteList);
            return;
        }
        for (Note note : noteList) {
            if (note.getTitle().toLowerCase().contains(keySearch.toLowerCase())
                    || note.getContent().toLowerCase().contains(keySearch.toLowerCase())) {
                filteredNoteList.add(note);
            }
        }
        noteListAdapter.fiteredNoteList(filteredNoteList);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Deprecated
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Note note = (Note) data.getSerializableExtra("note");
                database.noteDAO().insertNote(note);

                //sync data
                reloadMain();
            }
        }

        if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Note note = (Note) data.getSerializableExtra("note");
                database.noteDAO().update(note.getId(), note.getTitle(), note.getContent());

                //sync data again
                reloadMain();
            }
        }
    }

    private void updateRecycle(List<Note> noteList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(RECYCLER_SPAN_COUNT, LinearLayoutManager.VERTICAL));
        noteListAdapter = new NoteListAdapter(MainActivity.this, noteList, noteClickListener);
        recyclerView.setAdapter(noteListAdapter);
    }

    private final NoteClickListener noteClickListener = new NoteClickListener() {
        @Override
        public void onClick(Note note) {
            Intent intent = new Intent(MainActivity.this, AddEditNote.class);
            intent.putExtra("edit_note", note);
            startActivityForResult(intent, REQUEST_EDIT_NOTE);
        }

        @Override
        public void onLongClicK(Note note, CardView cardView) {
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

    @SuppressLint("NotifyDataSetChanged")
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
            reloadMain();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (backPressedCount == 1) {
            finish();
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            backPressedCount++;
            // Thiết lập một thời gian để reset biến đếm sau mỗi lần nhấn nút "Back"
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressedCount = 0;
                }
            }, 2000); // 2 giây
        }
    }
}