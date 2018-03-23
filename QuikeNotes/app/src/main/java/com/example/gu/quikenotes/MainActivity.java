package com.example.gu.quikenotes;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Note note;

    private TextView date;
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        date = findViewById(R.id.dateShow);
        text = (EditText) findViewById(R.id.note);
    }

    private String getCurrentDate(){
        SimpleDateFormat df = new SimpleDateFormat("E MMM dd, hh:mm a");
        return df.format(new Date());
    }

    @Override
    protected void onResume() {
        note = loadFile();  // Load the JSON containing the product data - if it exists
        if (note.getDate() != null) { // null means no file was loaded
            date.setText(note.getDate());
            text.setText(note.getText());
        }else{
            date.setText(getCurrentDate());
        }
        super.onResume();
    }

    private Note loadFile() {
        Log.d(TAG, "loadFile: Loading JSON File");
        Note note = new Note();
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.filename));
            JsonReader reader = new JsonReader(new InputStreamReader(is, getString(R.string.encoding)));

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("date")) {
                    note.setDate(reader.nextString());
                } else if (name.equals("text")) {
                    note.setText(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return note;
    }

    @Override
    protected void onPause() {
        note.setDate(getCurrentDate());
        note.setText(text.getText().toString());
        super.onPause();
    }

    @Override
    protected void onStop() {
        saveNote();
        super.onStop();
    }

    private void saveNote() {

        Log.d(TAG, "saveProduct: Saving JSON File");
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.filename), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("date").value(note.getDate());
            writer.name("text").value(note.getText());
            writer.endObject();
            writer.close();

            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
