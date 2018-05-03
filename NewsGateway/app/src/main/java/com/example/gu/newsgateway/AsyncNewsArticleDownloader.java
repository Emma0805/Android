package com.example.gu.newsgateway;

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
import java.util.HashSet;

/**
 * Created by gu on 2018/4/8.
 */

public class AsyncNewsArticleDownloader   extends AsyncTask<String, Integer, String> {
    private NewsService newsService;
    private final String dataURL = "https://newsapi.org/v1/articles?source=";
    private final String APIKey = "&apiKey=8decee5854c44224bbe9cd0149d555eb";

    public AsyncNewsArticleDownloader(NewsService ns) {
        newsService = ns;
    }

    @Override
    protected String doInBackground(String... strings) {
        String sourceName = strings[0];
        String apiUrl = "";

        apiUrl = dataURL + sourceName + APIKey;
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

    private ArrayList<Article> parseJSON(String s) {
        ArrayList<Article> articleList = new ArrayList<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray articles = jObjMain.getJSONArray("articles");
            for (int i = 0; i < articles.length(); i++) {
                JSONObject articleObj = (JSONObject) articles.get(i);
                Article article = new Article();
                article.setAuthor(articleObj.getString("author"));
                article.setTitle(articleObj.getString("title"));
                article.setDescription(articleObj.getString("description"));
                article.setUrlToImage(articleObj.getString("urlToImage"));
                article.setPublishedAt(articleObj.getString("publishedAt"));
                article.setUrl(articleObj.getString("url"));
                articleList.add(article);
            }
            return articleList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        ArrayList<Article> articleList = parseJSON(s);
        newsService.setArticles(articleList);
    }
}
