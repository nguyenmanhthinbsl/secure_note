package com.thinnm00.securenotes.databases;

import androidx.room.*;
import com.thinnm00.securenotes.models.Note;

import java.util.List;

@Dao
public interface NoteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    @Query("SELECT * FROM tb_note WHERE NOT is_trash ORDER BY is_pinned DESC, id DESC")
    List<Note> getAll();

    @Query("UPDATE tb_note SET title= :title, content= :content WHERE id= :id")
    void update(int id, String title, String content);

    // not suggest use
    @Delete
    void delete(Note note);

    @Query("UPDATE tb_note SET  is_trash= 1 WHERE id= :id")
    void moveToTrash(int id);

    @Query("UPDATE tb_note SET is_pinned =:pin WHERE id= :id")
    void pin(int id, boolean pin);
}
