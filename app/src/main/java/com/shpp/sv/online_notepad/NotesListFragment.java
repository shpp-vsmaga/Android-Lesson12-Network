package com.shpp.sv.online_notepad;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by SV on 07.04.2016.
 */
public class NotesListFragment extends ListFragment {
    private NoteAdapter adapter;
    private static final int EMPTY_ITEM_ID = -1;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fillDbFromServer();
        FloatingActionButton fab = getFab();
        fab.hide();
    }

    private void fillDbFromServer(){
        if (connectionIsAvailable()) {
            new LoadNotesTask().execute();
        } else {
            showErrorMessage();
        }
    }

    private boolean connectionIsAvailable() {
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private class LoadNotesTask extends AsyncTask<Nullable, Nullable, Nullable>{
        @Override
        protected Nullable doInBackground(Nullable... params) {
            RestHelper restHelper = RestHelper.getInstance(getActivity());
            try {
                restHelper.fillNotesArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Nullable nullable) {
            RestHelper restHelper = RestHelper.getInstance(getActivity());
            adapter = new NoteAdapter(getActivity(),
                    R.layout.list_item, restHelper.getNotesList());

            setListAdapter(adapter);
            getFab().show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes_list, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (connectionIsAvailable()) {
            super.onListItemClick(l, v, position, id);
            Note note = adapter.getItem(position);
            ((onEditRequestListener) getActivity()).editRequest(note.getId());
        } else {
            showErrorMessage();
        }
    }

    private FloatingActionButton getFab(){
        FloatingActionButton fab = null;
        if (getActivity() instanceof MainActivity){
            fab = ((MainActivity) getActivity()).getFab();
        }
        return fab;
    }
    public void updateList(boolean activateLastItem) {
        adapter.notifyDataSetChanged();
        ListView listView = getListView();
        if (activateLastItem){
            int lastItemID = listView.getCount() - 1;
            listView.setItemChecked(lastItemID, true);
            listView.setSelection(lastItemID);
        } else {
            listView.setItemChecked(EMPTY_ITEM_ID, true);
        }
    }

    private void showErrorMessage(){
        Toast.makeText(getActivity(),
                getActivity().getResources().getString(R.string.connUnavailable),
                Toast.LENGTH_SHORT).show();
    }
}
