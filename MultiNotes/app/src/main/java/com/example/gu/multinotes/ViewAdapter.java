package com.example.gu.multinotes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by gu on 2018/1/31.
 */

public class ViewAdapter extends RecyclerView.Adapter<ViewHolder> {
    private ArrayList<Note> noteList;
    private MainActivity mainActivity;

    public ViewAdapter(MainActivity mainActivity,ArrayList<Note> noteList){
        this.noteList = noteList;
        this.mainActivity = mainActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//bind the layout design
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_item, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {//bind the value of class Note
        Note note = noteList.get(position);
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        holder.date.setText(note.getDate());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
