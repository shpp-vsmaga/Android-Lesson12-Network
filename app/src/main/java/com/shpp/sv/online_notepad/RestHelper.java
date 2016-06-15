package com.shpp.sv.online_notepad;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by SV on 28.05.2016.
 */
public class RestHelper {

    private static RestHelper savedInstance;
    private static final String BASE_URL = "http://193.238.36.17/api.php/";
    private static final String TABLE_NAME = "notes";
    private static final String DB_ID = "id";
    private static final String DB_TEXT = "text";
    private Retrofit retrofit;
    private RestService restService;
    private final ArrayList<Note> notesList;
    private int endId = -1;
    private Context context;


    public RestHelper(Context context) {

        this.context = context;
        notesList = new ArrayList<>();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restService = retrofit.create(RestService.class);
    }

    public static synchronized RestHelper getInstance(Context context) {
        if (savedInstance == null) {
            savedInstance = new RestHelper(context);
        }
        return savedInstance;
    }

    private boolean connectionIsAvailable() {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public ArrayList<Note> getNotesList() {
        return notesList;
    }

    public String getNote(int id) throws IOException {
        String text = "";
        Call<Note> call = restService.getNote(id);
        Note note = call.execute().body();
        text = note.getText();

        return text;
    }

    public void addNote(String text) {

        if (connectionIsAvailable()) {
            final Call<Object> call = restService.addNote(text);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        call.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            addNoteInArray(text);
        } else {
            showErrorMessage();
        }
    }

    private void addNoteInArray(String text) {
        notesList.add(new Note(++endId, text));
    }

    public void updateNote(int id, String text) {
        if (connectionIsAvailable()) {
            final Call<Object> call = restService.editNote(id, text);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        call.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            updateNoteInArray(id, text);
        } else {
            showErrorMessage();
        }
    }

    private void updateNoteInArray(int id, String text) {
        for (Note note : notesList) {
            if (note.getId() == id) {
                note.setText(text);
            }
        }
    }


    public void deleteNote(int id) {
        if (connectionIsAvailable()) {
            final Call<Object> call = restService.delNote(id);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        call.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            deleteNoteInArray(id);
        } else {
            showErrorMessage();
        }
    }

    private void deleteNoteInArray(int id) {
        for (int i = 0; i < notesList.size(); i++) {
            if (notesList.get(i).id == id) {
                notesList.remove(i);
                break;
            }
        }
    }

    private void updateEndId(int id) {
        if (id > endId) {
            endId = id;
        }
    }


    public void fillNotesArray() throws IOException {
        restService = retrofit.create(RestService.class);
        Call<JsonElement> call = restService.getNotesList();
        JsonElement jsonElement = call.execute().body();
        notesList.clear();

        try {
            JSONObject json = new JSONObject(jsonElement.toString());
            JSONArray jsonArray = json.getJSONArray(TABLE_NAME);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject noteJson = jsonArray.getJSONObject(i);
                int id = Integer.parseInt(noteJson.getString(DB_ID));
                String text = noteJson.getString(DB_TEXT);
                notesList.add(new Note(id, text));
                updateEndId(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showErrorMessage() {
        Toast.makeText(context,
                context.getResources().getString(R.string.connUnavailable),
                Toast.LENGTH_SHORT).show();
    }

}
