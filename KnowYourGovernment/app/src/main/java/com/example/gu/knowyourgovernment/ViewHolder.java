package com.example.gu.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by gu on 2018/2/19.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView name;

    public ViewHolder(View itemView) {
        super(itemView);
        title = (TextView)itemView.findViewById(R.id.title);
        name = (TextView)itemView.findViewById(R.id.name);
    }
}
