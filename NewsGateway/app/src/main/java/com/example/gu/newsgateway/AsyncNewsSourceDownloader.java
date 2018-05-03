package com.example.gu.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gu on 2018/4/7.
 */

public class AsyncNewsSourceDownloader  extends AsyncTask<String, Integer, String> {
    private MainActivity mainActivity;

    private final String dataURL = "https://newsapi.org/v1/sources?language=en&country=us";
    private final String APIKey = "&apiKey=8decee5854c44224bbe9cd0149d555eb";

    private Set<String> categoryList;

    public AsyncNewsSourceDownloader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        String category = strings[0];
        String apiUrl = "";
        if(category == null||category.equals("all") || category.equals("")){
            apiUrl = dataURL + APIKey;
        }else{
            apiUrl = dataURL + "&category="+ category + APIKey;
        }
        Uri dataUri = Uri.parse(apiUrl);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return sb.toString();
    }

    private ArrayList<Source> parseJSON(String s) {
        ArrayList<Source> sourcesList = new ArrayList<>();
        categoryList = new HashSet<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray sources = jObjMain.getJSONArray("sources");
            for (int i = 0; i < sources.length(); i++) {
                JSONObject sourceObj = (JSONObject) sources.get(i);
                Source source = new Source();
                source.setName(sourceObj.getString("name"));
                source.setId(sourceObj.getString("id"));
                source.setCategory(sourceObj.getString("category"));
                source.setUrl(sourceObj.getString("url"));
                sourcesList.add(source);
                categoryList.add(sourceObj.getString("category"));
            }
            return sourcesList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        ArrayList<Source> sourcesList = parseJSON(s);
        mainActivity.setSources(sourcesList,categoryList);
    }
}