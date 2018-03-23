package com.example.gu.stock_watch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by gu on 2018/2/19.
 */

public class ViewAdapter extends RecyclerView.Adapter<ViewHolder> {
    private ArrayList<Stock> stockList;
    private MainActivity mainActivity;

    public ViewAdapter(MainActivity mainActivity, ArrayList<Stock> stockList) {
        this.stockList = stockList;
        this.mainActivity = mainActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_item, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("#.##");

        Stock stock = stockList.get(position);
        if (stock.getChange() >= 0) {
            holder.symbol.setTextColor(Color.parseColor("#37d12e"));
            holder.latestPrice.setTextColor(Color.parseColor("#37d12e"));
            holder.change.setTextColor(Color.parseColor("#37d12e"));
            holder.item_name.setTextColor(Color.parseColor("#37d12e"));
            holder.change.setText("▲"+ df.format(stock.getChange()) + "(" + df.format(stock.getChangePercentage()) + "%)");
        } else {
            holder.symbol.setTextColor(Color.parseColor("#a00419"));
            holder.latestPrice.setTextColor(Color.parseColor("#a00419"));
            holder.change.setTextColor(Color.parseColor("#a00419"));
            holder.item_name.setTextColor(Color.parseColor("#a00419"));
            holder.change.setText("▼"+ df.format(stock.getChange()) + "(" + df.format(stock.getChangePercentage()) + "%)");
        }
        holder.symbol.setText(stock.getSymbol());
        holder.latestPrice.setText(stock.getLastPrice() + "");

        holder.item_name.setText(stock.getName());
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
