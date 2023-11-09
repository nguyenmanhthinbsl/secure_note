package com.thinnm00.securenotes;

import androidx.cardview.widget.CardView;
import com.thinnm00.securenotes.models.Note;

public interface NoteClickListener {
    void onClick(Note note);

    void onLongClicK(Note note, CardView cardView);

}
