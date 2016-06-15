package com.shpp.sv.online_notepad;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by SV on 28.05.2016.
 */
public class NoteAdapter extends ArrayAdapter<Note> {

    private ArrayList<Note> notesArray;

    public NoteAdapter(Context context, int resource, ArrayList<Note> notesArray) {
        super(context, resource, notesArray);
        this.notesArray = notesArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView tv = (TextView) super.getView(position, convertView, parent);
        if (tv != null) {
            tv.setText(notesArray.get(position).getText());
            tv.setId(notesArray.get(position).getId());
        }
        return tv;
    }


}
