package com.example.gu.knowyourgovernment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnLongClickListener{
    private static final String TAG = "MainActivity";

    private ArrayList<Official> officialList = new ArrayList<Official>();

    private ViewAdapter mAdapter;
    private RecyclerView recyclerView;

    private Locator locator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new ViewAdapter(this, officialList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(!doNetCheck()){
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("No Network Connection").setMessage("Data cannot be accessed/loaded without an internet connection.").create();
            dialog.show();
        }else {
            locator = new Locator(this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_official, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                Log.d(TAG, "onOptionsItemSelected: info");
                Intent intentShowInfo = new Intent(this, SystemInformation.class);
                startActivity(intentShowInfo);
                break;
            case R.id.input_location:
                Log.d(TAG, "onOptionsItemSelected: input_location");
                final EditText text = new EditText(this);
                text.setSingleLine(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Enter a City, State or a Zip Code: ");
                builder.setView(text);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(MainActivity.this, text.getText(), Toast.LENGTH_SHORT).show();
                        new CivicInfoDownloader(MainActivity.this).execute(text.getText().toString());
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void doLocationWork(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = null;

            addresses = geocoder.getFromLocation(latitude,longitude,1);
            if(addresses.size() == 0){
                Toast.makeText(this, "Address can't be acquired from provided latitude/longitude", Toast.LENGTH_LONG).show();
                return;
            }else{
                Address address = addresses.get(0);
                String zip = address.getPostalCode();
                new CivicInfoDownloader(MainActivity.this).execute(zip);
            }
        } catch (IOException e) {
            Toast.makeText(this, "Address can't be acquired from provided latitude/longitude", Toast.LENGTH_LONG).show();
        }
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers were available", Toast.LENGTH_LONG).show();
    }

    public void noInfoAvailable() {
        Toast.makeText(this, "Civic Info service is unavailable", Toast.LENGTH_LONG).show();
    }

    public void noDataAvailable() {
        Toast.makeText(this, "No data is available for the specified location", Toast.LENGTH_LONG).show();
    }

    public void setOfficialList(Object[] res) {
        TextView location = (TextView)findViewById(R.id.location);
        if(res == null){
            location.setText("No Data For Location");
            officialList.clear();
        }else{
            location.setText(res[0].toString());
            ArrayList<Official> officialListBackup = (ArrayList<Official>) res[1];
            officialList.clear();
            for(Official official:officialListBackup){
                officialList.add(official);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        TextView location = (TextView)findViewById(R.id.location);
        int pos = recyclerView.getChildLayoutPosition(view);
        Official official = officialList.get(pos);
        Intent intentShowInfo = new Intent(this, OfficialActivity.class);
        intentShowInfo.putExtra("location",location.getText().toString());
        intentShowInfo.putExtra("official",official);
        startActivity(intentShowInfo);
    }

    @Override
    public boolean onLongClick(View view) {
        onClick(view);
        return false;
    }

    private boolean doNetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
