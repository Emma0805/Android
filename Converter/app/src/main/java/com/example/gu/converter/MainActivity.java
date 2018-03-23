package com.example.gu.converter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void convertFC(View v){

        RadioButton rd1 = (RadioButton) findViewById(R.id.F2C);

        //get the input value
        EditText v1 = findViewById(R.id.input);
        double input = Double.parseDouble(v1.getText().toString());
        Log.d(TAG, "convertFC: input value is " + v1);

        //calcuate the result
        double answer = 0;
        String historyConvert = "F to C: ";
        if(rd1.isChecked()){
            answer = (input - 32.0) * 5.0 / 9.0;
        }else{
            answer = (input * 9.0 / 5.0) + 32.0;
            historyConvert = "C to F: ";
        }
        //print the result
        TextView output = findViewById(R.id.output);
        output.setText(String.format("%.1f",answer));
        Log.d(TAG, "convertFC: ");

        //print the history
        TextView history = findViewById(R.id.history);
        String historyText = historyConvert +input + "->" + String.format("%.1f",answer) + "\n" + history.getText().toString();
        history.setText(historyText);
    }
}
