package com.example.gu.multinotes;

import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by gu on 2018/2/8.
 */

public class LoadFileAsyncTask extends AsyncTask<Integer, ArrayList<Note>, String> {
    private static final String TAG = "LoadFileAsyncTask";
    private MainActivity mainActivity;
    public static boolean running = false;
    private ArrayList<Note> noteList = new ArrayList<Note>();



    public LoadFileAsyncTask(MainActivity ma) {
        mainActivity = ma;
    }

    /********************************************************
     *
     *                  Load file(Step 3)
     ********************************************************
     * It is responsible for loading and refresh the list for users
     * But it also can delete a note after the loading with pos
     * pos = -1, load the file
     * pos >= 0, delete the note after loading
     * go to see loadFile();
     *******************************************************/
    @Override
    protected String doInBackground(Integer... params) {
        Log.d(TAG, "doInBackground: Starting background execution");

        loadFile(params[0]);

        return "Y";
    }

    @Override
    protected void onPostExecute(String string) {
        // This method is almost always used
        super.onPostExecute(string);
        Log.d(TAG, "onPostExecute: " + string);

        mainActivity.whenAsyncIsDone(noteList);

        running = false;
        Log.d(TAG, "onPostExecute: AsyncTask terminating successfully");
    }

    /********************************************************
     *
     *                  Load file(Step 4)
     ********************************************************
     * It is responsible for loading and refresh the list for users
     * But it also can delete a note after the loading with pos
     * pos = -1, load the file
     * pos >= 0, delete the note after loading
     *******************************************************/
    private void loadFile(int pos) {
        Log.d(TAG, "loadFile: Loading JSON File");
        Note note = new Note();
        ;
        try {
            InputStream is = mainActivity.getApplicationContext().openFileInput(mainActivity.getString(R.string.filename));
            JsonReader reader = new JsonReader(new InputStreamReader(is, mainActivity.getString(R.string.encoding)));
            noteList.clear();
            noteList.addAll(mainActivity.getNoteList());
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("date")) {
                    note = new Note();//start a new note
                    note.setDate(reader.nextString());
                } else if (name.equals("title")) {
                    note.setTitle(reader.nextString());
                } else if (name.equals("content")) {
                    note.setContent(reader.nextString());
                    noteList.add(note);//note is completed, add to list
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            /********************************************************
             *
             *                  Edit a note(Step 7)
             *
             *******************************************************/
            if (pos >= 0) {
                noteList.remove(pos+1);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "loadFileResult: Can't find a file!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
