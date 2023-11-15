package com.thinnm00.securenotes.databases;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.thinnm00.securenotes.models.Note;

@Database(entities = {Note.class}, version = 1, exportSchema = true)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {

    private static RoomDatabase database;
    private static String DB_NAME = "db_secure_note";

    public synchronized static RoomDatabase getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDatabase.class, DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public abstract NoteDAO noteDAO();

}
