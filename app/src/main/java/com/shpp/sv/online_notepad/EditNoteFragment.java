package com.shpp.sv.online_notepad;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;


public class EditNoteFragment extends Fragment {
    private EditText edtNoteEditor;
    private static final int SAVE_BUTTON_ID = 101;
    private static final int DELETE_BUTTON_ID = 102;
    private static final String SAVED_BUTTONS_STATE = "buttonsState";
    private static final String SAVED_CURRENT_ID = "savedID";
    private boolean showButtons = false;
    private int currentId = -1;
    private MenuItem saveButton;
    private MenuItem deleteButton;
    private RestHelper restHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);
        edtNoteEditor = (EditText) view.findViewById(R.id.edtEditeNote);
        restHelper = RestHelper.getInstance(getActivity());
        setHasOptionsMenu(true);
        return view;
    }

    private boolean connectionIsAvailable() {
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public void addNote(){
        edtNoteEditor.setText("");
        edtNoteEditor.requestFocus();
        setControlsActive(true);
        currentId = -1;
    }

    public void editNote(int id) {
        currentId = id;
        setControlsActive(false);
        if (connectionIsAvailable()) {
            new GetNoteTextTask().execute(id);
        } else {
            showErrorMessage();
        }
    }

    private class GetNoteTextTask extends AsyncTask<Integer, String, String>{
        @Override
        protected String doInBackground(Integer... params) {
            String text = "";
            try {
                text = restHelper.getNote(params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return text;
        }

        @Override
        protected void onPostExecute(String text) {
            edtNoteEditor.setText(text);
            edtNoteEditor.requestFocus();
            setControlsActive(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        saveButton = menu.add(0, SAVE_BUTTON_ID, 1, getResources().getString(R.string.save));
        saveButton.setIcon(android.R.drawable.ic_menu_save);
        saveButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        deleteButton = menu.add(0, DELETE_BUTTON_ID, 0, getResources().getString(R.string.delete));
        deleteButton.setIcon(android.R.drawable.ic_menu_delete);
        deleteButton.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        setButtonsVisible(showButtons);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_BUTTONS_STATE, showButtons);
        outState.putInt(SAVED_CURRENT_ID, currentId);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            showButtons = savedInstanceState.getBoolean(SAVED_BUTTONS_STATE);
            currentId = savedInstanceState.getInt(SAVED_CURRENT_ID);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SAVE_BUTTON_ID:
                saveText();
                return true;
            case DELETE_BUTTON_ID:
                deleteText();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setControlsActive(boolean active) {
        if (!active) {
            edtNoteEditor.setText("");
        }
        edtNoteEditor.setEnabled(active);
        showButtons = active;
        getActivity().invalidateOptionsMenu();
    }

    private void saveText() {
        if (currentId != -1) {
            restHelper.updateNote(currentId, edtNoteEditor.getText().toString());
        } else {
            restHelper.addNote(edtNoteEditor.getText().toString());
        }

        editFinish();
    }

    private void deleteText() {
        /*Delete from DB*/

        restHelper.deleteNote(currentId);
        editFinish();
    }

    private void editFinish() {
        /*Disable buttons*/
        setControlsActive(false);

        /*Say activity all is done*/
        ((onEditRequestListener) getActivity()).editFinish();
    }

    private void setButtonsVisible(boolean visible) {
        edtNoteEditor.setEnabled(visible);
            deleteButton.setVisible(visible);
            saveButton.setVisible(visible);
    }

    @Override
    public void onPause() {
        super.onPause();
        setControlsActive(false);
    }

    private void showErrorMessage(){
        Toast.makeText(getActivity(),
                getActivity().getResources().getString(R.string.connUnavailable),
                Toast.LENGTH_SHORT).show();
    }
}
