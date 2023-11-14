package com.thinnm00.securenotes.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "tb_note")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "create_date")
    private String createDate;


    //SQLite boolean not stored TRUE FALSE,  instead SQLite using 0,1
    //booleanCol BOOLEAN NOT NULL CHECK (mycolumn IN (0, 1))
    @ColumnInfo(name = "is_pinned")
    private boolean isPinned;

    @ColumnInfo(name = "is_trash")
    private boolean isTrash;

//    @ColumnInfo(name = "bg_hex_color_code")
//    private int bgHexColorCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isTrash() {
        return isTrash;
    }

    public void setTrash(boolean trash) {
        isTrash = trash;
    }

    public Note(int id, String title, String content, String createDate, boolean isPinned, boolean isTrash) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createDate = createDate;
//        this.bgHexColorCode = bgHexColorCode;
        this.isPinned = isPinned;
        this.isTrash = isTrash;
    }

    public Note() {
    }
}
