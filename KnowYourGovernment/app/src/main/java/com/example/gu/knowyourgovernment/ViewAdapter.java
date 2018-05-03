package com.example.gu.knowyourgovernment;

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
    private ArrayList<Official> officialList;
    private MainActivity mainActivity;

    public ViewAdapter(MainActivity mainActivity, ArrayList<Official> officialList) {
        this.officialList = officialList;
        this.mainActivity = mainActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_list_item, parent, false);

        itemView.setOnClickListener(mainActivity);
       itemView.setOnLongClickListener(mainActivity);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Official official = officialList.get(position);
        holder.title.setText(official.getTitle());
        holder.name.setText(official.getName()+" ("+(official.getParty()==null?"Unknown":official.getParty())+")");
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}
