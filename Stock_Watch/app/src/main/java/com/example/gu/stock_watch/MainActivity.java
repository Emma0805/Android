package com.example.gu.stock_watch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.ReplacementTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "MainActivity";

    private ArrayList<Stock> stockList = new ArrayList<Stock>();

    private ViewAdapter mAdapter;
    private RecyclerView recyclerView;

    private SwipeRefreshLayout swiper;
    private DatabaseHandler databaseHandler;

    private final String webUrl = "http://www.marketwatch.com/investing/stock/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new ViewAdapter(this, stockList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        databaseHandler = new DatabaseHandler(this);

        if(!doNetCheck()){
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("No Network Connection").setMessage("Stocks can't be loaded without a network connection").create();
            dialog.show();
        }else {
            loadStocks();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_stock:
                Log.d(TAG, "onOptionsItemSelected: add_stock");
                if(!doNetCheck()){
                    AlertDialog dialog = new AlertDialog.Builder(this).setTitle("No Network Connection").setMessage("Stocks can't be added without a network connection").create();
                    dialog.show();
                }else {
                    add_a_stock_click();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Stock s = stockList.get(pos);
        String url = webUrl+s.getSymbol();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public boolean onLongClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        Stock s = stockList.get(pos);
        ConfirmDelete(pos, s);
        return false;
    }

    private void ConfirmDelete(final int pos, Stock s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Delete Stock").setIcon(android.R.drawable.ic_menu_delete);
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                databaseHandler.deleteStock(stockList.get(pos).getSymbol());
                stockList.remove(pos);
                //refresh the application for user
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setMessage("Delete Stock Symbol '" + s.getSymbol() + "'?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void add_a_stock_click() {
        final EditText text = new EditText(this);
        text.setSingleLine(true);
        text.setTransformationMethod(new ReplacementTransformationMethod() {
            @Override
            protected char[] getOriginal() {
                char[] originalCharArr = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z' };
                return originalCharArr;
            }

            @Override
            protected char[] getReplacement() {
                char[] replacementCharArr = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z' };
                return replacementCharArr;
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please enter a Stock Symbol");
        builder.setView(text);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!checkDuplicate(text.getText().toString().toUpperCase())) {
                    return;
                }
                if (AsyncSymbolLoader.running) {
                    Toast.makeText(MainActivity.this, "Still running", Toast.LENGTH_SHORT).show();
                    return;
                }
                AsyncSymbolLoader.running = true;
                new AsyncSymbolLoader(MainActivity.this).execute(text.getText().toString());
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doRefresh() {
        if(!doNetCheck()){
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("No Network Connection").setMessage("Stocks can't be updated without a network connection").create();
            dialog.show();
        }else {
            ArrayList<Stock> temp = new ArrayList<Stock>();
            temp.addAll(stockList);
            stockList.clear();
            for (Stock cur : temp) {
                new AsyncStockLoader(MainActivity.this).execute(cur.getSymbol() + "-" + cur.getName(), "start");
            }
        }
        swiper.setRefreshing(false);
    }

    public void showSymbols(final ArrayList<String> stockList, String input) {
        if (stockList == null) {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("404 Not Found").setMessage("Can't find the stock").create();
            dialog.show();
        } else if (stockList.size() == 0) {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Symbol Not Found:" + input.toUpperCase()).setMessage("Data for stock symbol").create();
            dialog.show();
        } else if (stockList.size() == 1){
            String symbol = stockList.get(0);
            symbol = symbol.substring(0, symbol.indexOf("-"));
            if (!checkDuplicate(symbol)) {
                return;
            }
            if (AsyncStockLoader.running) {
                Toast.makeText(MainActivity.this, "Still running", Toast.LENGTH_SHORT).show();
                return;
            }
            AsyncStockLoader.running = true;
            new AsyncStockLoader(MainActivity.this).execute(stockList.get(0), "add");
        } else{
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stockList);
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Make a selection").setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int pos) {
                    String symbol = stockList.get(pos);
                    symbol = symbol.substring(0, symbol.indexOf("-"));
                    if (!checkDuplicate(symbol)) {
                        return;
                    }
                    if (AsyncStockLoader.running) {
                        Toast.makeText(MainActivity.this, "Still running", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    AsyncStockLoader.running = true;
                    new AsyncStockLoader(MainActivity.this).execute(stockList.get(pos), "add");
                    dialog.dismiss();
                }
            }).setPositiveButton("NEVERMIND", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            }).create();
            dialog.show();
        }
    }

    public void addNewStock(Stock stock, String op) {
        if (op == null || stock == null) {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("404 Not Found").setMessage("Can't find Data for the stock symbol").create();
            dialog.show();
        } else if (op.equals("add")) {
            stockList.add(stock);
            sortList();
            databaseHandler.addStock(stock);
            mAdapter.notifyDataSetChanged();
        } else if (op.equals("start")) {
            stockList.add(stock);
            sortList();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }

    private void loadStocks() {
        ArrayList<String[]> temp = databaseHandler.loadStocks();
        for (String[] cur : temp) {
            new AsyncStockLoader(MainActivity.this).execute(cur[0] + "-" + cur[1], "start");
        }
    }

    public boolean checkDuplicate(String symbol) {
        for (Stock cur : stockList) {
            if (cur.getSymbol().equals(symbol)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(android.R.drawable.ic_menu_info_details);
                builder.setTitle("Duplicate Stock");
                builder.setMessage("Stock Symbol " + symbol + " is already displayed");
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        }
        return true;
    }

    private void sortList(){
        if (stockList.size() < 2){
            return;
        }
        Stock end = stockList.remove(stockList.size()-1);
        int count = 0;
        for(Stock stock:stockList){
            if(stock.getSymbol().compareTo(end.getSymbol())>0){
                stockList.add(count,end);
                count = -1;
                break;
            }
            count++;
        }
        if(count >= 0){
            stockList.add(end);
        }
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
