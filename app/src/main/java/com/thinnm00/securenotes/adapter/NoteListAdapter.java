package com.thinnm00.securenotes.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.thinnm00.securenotes.NoteClickListener;
import com.thinnm00.securenotes.R;
import com.thinnm00.securenotes.models.Note;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class NoteViewHolder extends RecyclerView.ViewHolder {

    CardView container;
    TextView tv_title, tv_note, tv_date;
    ImageView imgv_pin;

    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);

        container = itemView.findViewById(R.id.note_container);
        tv_title = itemView.findViewById(R.id.textview_title);
        tv_note = itemView.findViewById(R.id.textview_notes);
        tv_date = itemView.findViewById(R.id.textview_date);
        imgv_pin = itemView.findViewById(R.id.imgview_pin);
    }
}

public class NoteListAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    Context context;
    List<Note> listNote;

    NoteClickListener listener;

    public NoteListAdapter(Context context, List<Note> listNote, NoteClickListener listener) {
        this.context = context;
        this.listNote = listNote;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        return new NoteViewHolder(LayoutInflater.from(context).inflate(R.layout.note_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.tv_title.setText(listNote.get(position).getTitle());
        holder.tv_title.setSelected(true);

        holder.tv_note.setText(listNote.get(position).getContent());

        holder.tv_date.setText(listNote.get(position).getCreateDate());
        holder.tv_date.setSelected(true);

        if (listNote.get(position).isPinned()) {
            holder.imgv_pin.setImageResource(R.drawable.pin);
        } else {
            holder.imgv_pin.setImageResource(0);
        }

        int noteBackgroundColor = getRandomNoteBackgroundColor();
        holder.container.setCardBackgroundColor(holder.itemView.getResources().getColor(noteBackgroundColor, null));

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(listNote.get(holder.getAdapterPosition()));

            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClicK(listNote.get(holder.getAdapterPosition()), holder.container);
                return true;
            }
        });
    }


    private int getRandomNoteBackgroundColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.bgcolor1);
        colorCode.add(R.color.bgcolor2);
        colorCode.add(R.color.bgcolor3);
        colorCode.add(R.color.bgcolor4);
        colorCode.add(R.color.bgcolor5);
        colorCode.add(R.color.bgcolor6);
        colorCode.add(R.color.bgcolor7);

        Random random = new Random();
        int randomColorCode = random.nextInt(colorCode.size());
        return colorCode.get(randomColorCode);
    }

    @Override
    public int getItemCount() {
        return listNote.size();
    }

    public void fiteredNoteList(List<Note> fiteredNoteList) {
        listNote = fiteredNoteList;
        notifyDataSetChanged();
    }
}

