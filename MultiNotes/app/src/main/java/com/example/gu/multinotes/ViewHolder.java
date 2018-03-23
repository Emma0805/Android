package com.example.gu.multinotes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by gu on 2018/1/31.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView content;
    public TextView date;

    public ViewHolder(View itemView) {//bind the items in the layout design
        super(itemView);
        title = (TextView)itemView.findViewById(R.id.item_title);
        content = (TextView)itemView.findViewById(R.id.item_content);
        date = (TextView)itemView.findViewById(R.id.item_date);
    }
}
