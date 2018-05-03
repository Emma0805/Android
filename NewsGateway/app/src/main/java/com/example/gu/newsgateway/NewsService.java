package com.example.gu.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewsService extends Service {
    private static final String TAG = "NewsService";
    private boolean isRunning = true;

    private ServiceReceiver serviceReceiver;
    static final String ACTION_NEWS_STORY = "service to main";
    private ArrayList<Article> storyList = new ArrayList();

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        IntentFilter filter = new IntentFilter(MainActivity.ACTION_MSG_TO_SVC);
        serviceReceiver = new ServiceReceiver();
        registerReceiver(serviceReceiver, filter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        if(storyList.isEmpty()) {
                            Thread.sleep(250);
                        }else{
                            Intent intent = new Intent();
                            intent.setAction(ACTION_NEWS_STORY);
                            intent.putExtra("storyList", storyList);
                            sendBroadcast(intent);
                            storyList.clear();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                Log.i(TAG, "NewsService was properly stopped");
            }
        }).start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(serviceReceiver);
        Log.d(TAG, "onDestroy: ");

        isRunning = false;
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
    }

    public void setArticles(ArrayList<Article> articleList) {
        if(articleList == null || articleList.size() == 0){
            Toast.makeText(this, "We have no article in this resource", Toast.LENGTH_SHORT).show();
            return;
        }
        storyList.clear();
        for (Article a: articleList) {
            storyList.add(a);
        }
    }

    class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(MainActivity.ACTION_MSG_TO_SVC.equals(intent.getAction())){
                String sourceId = "";
                if (intent.hasExtra("sourceId")){
                    sourceId = intent.getStringExtra("sourceId");
                }
                new AsyncNewsArticleDownloader(NewsService.this).execute(sourceId);
            }
        }
    }
}
