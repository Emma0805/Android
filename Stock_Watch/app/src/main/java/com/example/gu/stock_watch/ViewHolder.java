package com.example.gu.stock_watch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by gu on 2018/2/19.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView symbol;
    public TextView latestPrice;
    public TextView change;
    public TextView item_name;

    public ViewHolder(View itemView) {
        super(itemView);
        symbol = (TextView)itemView.findViewById(R.id.symbol);
        latestPrice = (TextView)itemView.findViewById(R.id.latestPrice);
        change = (TextView)itemView.findViewById(R.id.change);
        item_name = (TextView)itemView.findViewById(R.id.name);
    }
}
