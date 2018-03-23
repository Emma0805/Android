package com.example.gu.multinotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "MainActivity";
    private static final int ADD_NOTE_RESULT_CODE = 1;
    private ArrayList<Note> noteList = new ArrayList<Note>();

    private ViewAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new ViewAdapter(this, noteList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /********************************************************
         *
         *                  Load file(Step 1)
         *
         *******************************************************/
        loadFileAsync(-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info://When click on information bottom, go to see SystemInformation activity(show the information)
                Log.d(TAG, "onOptionsItemSelected: info");
                Intent intentShowInfo = new Intent(this, SystemInformation.class);
                startActivity(intentShowInfo);//the information activity needn't a result
                break;
            /********************************************************
             *
             *                  Add a note(Step 1)
             *
             *      go to see AddNote activity(add a note)
             *******************************************************/
            case R.id.menu_add://When click on add note bottom, go to see AddNote activity(add a note)
                Log.d(TAG, "onOptionsItemSelected: add");
                Intent intentAddNote = new Intent(this, AddNote.class);
                setResult(ADD_NOTE_RESULT_CODE);
                intentAddNote.putExtra();
                startActivityForResult(intentAddNote, ADD_NOTE_RESULT_CODE);//add note activity needs a result to deal with the data, go to see onActivityResult()
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_NOTE_RESULT_CODE) {//from onOptionsItemSelected(), after finish the AddNote activity
            if (resultCode == RESULT_OK) {
                /*This function deal all the result from AddNote activity
                * Both creating and modifying*/
                String title = data.getStringExtra(getString(R.string.title));
                String content = data.getStringExtra(getString(R.string.content));
                int pos = data.getIntExtra(getString(R.string.position), -1);
                if (!title.equals("")) {//check the title of note
                    if (pos < 0) {//check the note is created or modified,<0 means new note
                        /********************************************************
                         *
                         *                  Add a note(Step 4)
                         *
                         *******************************************************/
                        Note note = new Note(title, content);
                        noteList.clear();//refresh the list
                        noteList.add(note);//add note to the top of the list
                    } else {//these are all modified note
                        /********************************************************
                         *
                         *                  Edit a note(Step 5)
                         *
                         *******************************************************/
                        Note note = noteList.get(pos);
                        if (!title.equals(note.getTitle()) || !content.equals(note.getContent())) {//check the note is modified or not
                            note = new Note(title, content);
                            noteList.clear();
                            noteList.add(note);//add note to the top of the list
                            //Here the pos must be >= 0, it tells loadFileAsync(pos) to delete the old note from the list
                        } else {//the note is not modified
                            pos = -1;//to tell loadFileAsync(pos), it hasn't been modified,don't delete anything
                            noteList.clear();//just refresh the list and waiting for loading from JSON file
                            //Don't add any note to the list
                        }
                    }
                } else {
                    Toast.makeText(this, "Note without a title can't be saved!", Toast.LENGTH_LONG).show();
                    pos = -1;
                    noteList.clear();
                }
                //if pos >= 0, delete the old note from list, if pos < 0, just load note from JSON file
                /********************************************************
                 *
                 *                  Add a note(Step 5)
                 *                  Edit a note(Step 6)
                 *
                 *******************************************************/
                loadFileAsync(pos);

                Log.d(TAG, "onActivityResult: ok ");
            } else {
                Log.d(TAG, "onActivityResult: result Code: " + resultCode);
            }
        } else {
            Log.d(TAG, "onActivityResult: Request Code " + requestCode);
        }
    }

    /********************************************************
     *
     *                  Edit a note(Step 1)
     *
     *      go to see AddNote activity(edit a note)
     *******************************************************/
    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Note m = noteList.get(pos);
        Intent intentAddNote = new Intent(this, AddNote.class);

        intentAddNote.putExtra(getString(R.string.position), pos);
        intentAddNote.putExtra(getString(R.string.title), m.getTitle());
        intentAddNote.putExtra(getString(R.string.content), m.getContent());

        startActivityForResult(intentAddNote, ADD_NOTE_RESULT_CODE);
    }

    @Override
    public boolean onLongClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Note m = noteList.get(pos);
        ConfirmDelete(pos, m);
        return false;
    }

    private void ConfirmDelete(final int pos, Note m) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                noteList.remove(pos);
                //refresh the application for user
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setMessage("Delete Note '" + m.getTitle() + "'?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /********************************************************
     *
     *                  Load file(Step 2)
     *                  Add a note(Step 5)
     *
     ********************************************************
     * It is responsible for loading all notes
     * But it also can delete a note after the loading with pos
     * pos = -1, load the file
     * pos >= 0, delete the note after loading
     * go to see LoadFileAsyncTask.doInBackground();
     *******************************************************/
    public void loadFileAsync(int pos) {
        if (LoadFileAsyncTask.running) {
            Toast.makeText(this, "loading...", Toast.LENGTH_SHORT).show();
            return;
        }
        LoadFileAsyncTask.running = true;
        new LoadFileAsyncTask(this).execute(pos);
    }

    /********************************************************
     *
     *                  Load file(Step 5)
     *
     *******************************************************/
    public void whenAsyncIsDone(ArrayList<Note> noteList) {
        this.noteList.clear();
        this.noteList.addAll(noteList);
        mAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Load Notes Complete", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<Note> getNoteList() {
        return noteList;
    }

    @Override
    protected void onPause() {
        saveNote();
        super.onPause();
    }


    private void saveNote() {

        Log.d(TAG, "saveNote: Saving JSON File");

        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.filename), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginObject();
            for (Note note : noteList) {
                writer.name("date").value(note.getCurrentDate());
                writer.name("title").value(note.getTitle());
                writer.name("content").value(note.getContent());
            }
            writer.endObject();
            writer.close();
            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
