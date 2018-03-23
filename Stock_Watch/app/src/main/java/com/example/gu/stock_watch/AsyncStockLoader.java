package com.example.gu.stock_watch;

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
import java.util.ArrayList;

/**
 * Created by gu on 2018/2/25.
 */

public class AsyncStockLoader extends AsyncTask<String, Stock, String> {
    private static final String TAG = "AsyncStockLoader";
    private MainActivity mainActivity;
    public static boolean running = false;

    private String stockURL = "https://api.iextrading.com/1.0/stock/";

    private String op;

    public AsyncStockLoader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... params) {
        op = params[1];
        String input = params[0];
        input = input.substring(0,input.indexOf("-"));

        Log.d(TAG, "doInBackground: " + input);

        stockURL = stockURL + input + "/quote";

        Log.d(TAG, "doInBackground: " + stockURL);
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(stockURL);

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
            mainActivity.addNewStock(null,null);
        }else {
            Stock stock = parseJSON(s);
            mainActivity.addNewStock(stock,op);
        }
        running = false;
        super.onPostExecute(s);
    }

    private Stock parseJSON(String s) {

        try {
            JSONObject jObjMain = new JSONObject(s);

            String symbol = jObjMain.getString("symbol");
            if(symbol != null && !("").equals(symbol)){
                Stock stock = new Stock();
                stock.setSymbol(symbol);
                stock.setName(jObjMain.getString("companyName"));
                stock.setLastPrice(Double.parseDouble(jObjMain.getString("latestPrice")));
                stock.setChange(Double.parseDouble(jObjMain.getString("change")));
                stock.setChangePercentage(Double.parseDouble(jObjMain.getString("changePercent")));
                return stock;
            }
            return null;

        } catch (Exception e) {
            Log.e(TAG, "parseJSON: ", e);
            String a = e.getMessage();
            return null;
        }
    }
}