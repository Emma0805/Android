package com.example.gu.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    static final String ACTION_MSG_TO_SVC = "main to service";
    private ArrayList<String> sourcesNameList = new ArrayList<>();
    private ArrayList<String> categoryList = new ArrayList<>();
    private HashMap<String, Source> sourcesData = new HashMap<>();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private NewsReceiver newsReceiver;
    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments = new ArrayList<>();
    private ViewPager pager;
    private String sourceName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);

        IntentFilter filter = new IntentFilter(NewsService.ACTION_NEWS_STORY);
        newsReceiver = new NewsReceiver();
        registerReceiver(newsReceiver, filter);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        sourceName = sourcesNameList.get(position);
                        Intent intent = new Intent();
                        intent.setAction(ACTION_MSG_TO_SVC);
                        intent.putExtra("sourceId", sourcesData.get(sourceName).getId());
                        sendBroadcast(intent);
                        mDrawerLayout.closeDrawer(mDrawerList);
                    }
                }
        );

        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_item, sourcesNameList));

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        if (sourcesNameList.isEmpty())
            new AsyncNewsSourceDownloader(MainActivity.this).execute("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        Log.d(TAG, "onOptionsItemSelected: menu");
        switch (item.getItemId()) {
            case R.id.menu_all:
                new AsyncNewsSourceDownloader(MainActivity.this).execute("all");
                break;
            case R.id.menu_business:
                new AsyncNewsSourceDownloader(MainActivity.this).execute("business");
                break;
            case R.id.menu_entertainment:
                new AsyncNewsSourceDownloader(MainActivity.this).execute("entertainment");
                break;
            case R.id.menu_general:
                new AsyncNewsSourceDownloader(MainActivity.this).execute("general");
                break;
            case R.id.menu_science:
                new AsyncNewsSourceDownloader(MainActivity.this).execute("science");
                break;
            case R.id.menu_sports:
                new AsyncNewsSourceDownloader(MainActivity.this).execute("sports");
                break;
            case R.id.menu_technology:
                new AsyncNewsSourceDownloader(MainActivity.this).execute("technology");
                break;
            default:
                new AsyncNewsSourceDownloader(MainActivity.this).execute("");
        }
        return super.onOptionsItemSelected(item);
    }

    public void setSources(ArrayList<Source> sourcesList, Set<String> cList) {
        sourcesData.clear();
        sourcesNameList.clear();

        for (Source s : sourcesList) {
            sourcesNameList.add(s.getName());
            sourcesData.put(s.getName(),s);
        }
        /*if(categoryList.isEmpty()){
            categoryList.add("all");
            for (String c : cList) {
                categoryList.add(c);
            }
        }*/
        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        stopService(intent);
        super.onDestroy();
    }



    class NewsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(NewsService.ACTION_NEWS_STORY.equals(intent.getAction())){
                ArrayList<Article> storyList = new ArrayList();
                if (intent.hasExtra("storyList")){
                    storyList = (ArrayList<Article>) intent.getSerializableExtra("storyList");
                    reDoFragments(storyList);
                }
            }
        }

        private void reDoFragments(ArrayList<Article> storyList) {
            int num = storyList.size();
            pager.setBackgroundResource(0);
            setTitle(sourceName);
            for (int i = 0; i < pageAdapter.getCount(); i++)
                pageAdapter.notifyChangeInPosition(i);
            fragments.clear();
            for (int i = 0; i < num; i++) {
                fragments.add(MyFragment.newInstance("Fragment " + (i + 1) + " of " + num,storyList.get(i)));
            }

            pageAdapter.notifyDataSetChanged();
            pager.setCurrentItem(0);

            pager.setBackground(null);
        }
    }


    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }
    }
}