package com.shpp.sv.online_notepad;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SV on 28.05.2016.
 */
public class Note {
    @SerializedName("id")
    int id;

    @SerializedName("text")
    String text;

    public Note (int id, String text){
        this.id = id;
        this.text = text;
    }

    public int getId(){
        return id;
    }

    public String getText(){
        return text;
    }

    public void setText(String newText){
        text = newText;
    }
}
