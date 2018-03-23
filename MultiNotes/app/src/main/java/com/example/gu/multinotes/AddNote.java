package com.example.gu.multinotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.JsonWriter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

public class AddNote extends AppCompatActivity {
    private static final String TAG = "AddNote";

    private EditText title;
    private EditText content;

    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        title = (EditText) findViewById(R.id.add_note_title);
        content = (EditText) findViewById(R.id.add_note_content);
        loadExistedNote();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /********************************************************
             *
             *                  Add a note(Step 2)
             *                  Edit a note(Step 3)
             *
             *******************************************************/
            case R.id.save_note:
                Log.d(TAG, "onOptionsItemSelected: save_note");
                saveNote2List();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /********************************************************
     *
     *                  Edit a note(Step 2)
     *
     *******************************************************/
    private void loadExistedNote() {
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.title))) {
            String text = intent.getStringExtra(getString(R.string.title));
            title.setText(text);
        }
        if (intent.hasExtra(getString(R.string.content))) {
            String text = intent.getStringExtra(getString(R.string.content));
            content.setText(text);
        }
        pos = intent.getIntExtra(getString(R.string.position), -1);
    }

    /********************************************************
     *
     *                  Add a note(Step 3)
     *                  Edit a note(Step 4)
     *
     *      go to see MainActivity.onActivityResult()
     *******************************************************/
    private void saveNote2List(){
        Intent data = new Intent();
        data.putExtra(getString(R.string.title), title.getText().toString());
        data.putExtra(getString(R.string.content), content.getText().toString());
        data.putExtra(getString(R.string.position), pos);
        setResult(RESULT_OK, data);
        finish();
    }


    /********************************************************
     *
     *                  Add a note(Step 2)
     *                  Edit a note(Step 3)
     *
     *       the same as SAVE menu, just add a key listener
     *
     *******************************************************/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    saveNote2List();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.setMessage("Your note is not saved!\nSave note '" + title.getText().toString() + "'?");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return false;
    }
}
