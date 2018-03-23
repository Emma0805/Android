package com.example.gu.stock_watch;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gu on 2018/2/19.
 */

public class AsyncSymbolLoader extends AsyncTask<String, ArrayList<Stock>, String> {
    private static final String TAG = "AsyncSymbolLoader";
    private MainActivity mainActivity;
    public static boolean running = false;

    private final String symbolURL = "http://d.yimg.com/aq/autoc";

    private String input;

    public AsyncSymbolLoader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... params) {
        Uri.Builder buildURL = Uri.parse(symbolURL).buildUpon();

        input = params[0];
        buildURL.appendQueryParameter("region", "US");
        buildURL.appendQueryParameter("lang", "en-US");
        buildURL.appendQueryParameter("query", params[0]);
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            Log.d(TAG, "doInBackground: " + sb.toString());
            return sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if(s == null){
            mainActivity.showSymbols(null,null);
        }else {
            ArrayList<String> stockList = parseJSON(s);
            mainActivity.showSymbols(stockList, input);
        }
        running = false;
        super.onPostExecute(s);
    }

    private ArrayList<String> parseJSON(String s) {

        try {
            JSONObject jObjMain = new JSONObject(s);

            JSONObject resultSet = jObjMain.getJSONObject("ResultSet");
            JSONArray res = resultSet.getJSONArray("Result");

            ArrayList<String> stockList = new ArrayList<String>();

            for (int i = 0; i < res.length(); i++) {
                JSONObject stock = (JSONObject) res.get(i);
                if(!stock.getString("symbol").contains(".") && ("S").equals(stock.getString("type"))){
                    stockList.add(stock.getString("symbol")+"-"+stock.getString("name"));
                }
            }
            Log.d(TAG, "parseJSON: " + res.toString());
            return stockList;

        } catch (Exception e) {
            Log.e(TAG, "parseJSON: ", e);
            String a = e.getMessage();
            return null;
        }
    }
}
