package com.example.gu.newsgateway;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyFragment extends Fragment {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    public static final MyFragment newInstance(String message, Article article)
    {
        MyFragment f = new MyFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        bdl.putSerializable("article",article);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String message = getArguments().getString(EXTRA_MESSAGE);
        Article article = (Article) getArguments().getSerializable("article");
        View v = inflater.inflate(R.layout.fragment_layout, container, false);
        TextView pageNumber = (TextView)v.findViewById(R.id.pageNumber);
        pageNumber.setText(message);

        TextView title = (TextView)v.findViewById(R.id.title);
        title.setText(article.getTitle());
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openArticleActivity();
            }
        });
        TextView publishedAt = (TextView)v.findViewById(R.id.publishedAt);
        publishedAt.setText(article.getPublishedAt());
        TextView author = (TextView)v.findViewById(R.id.author);
        author.setText(article.getAuthor());
        TextView description = (TextView)v.findViewById(R.id.description);
        description.setText(article.getDescription());
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openArticleActivity();
            }
        });
        ImageView image = (ImageView)v.findViewById(R.id.image) ;
        Picasso.get().load(article.getUrlToImage()).into(image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openArticleActivity();
            }
        });
        return v;
    }

    public void openArticleActivity(){
        Article article = (Article) getArguments().getSerializable("article");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(article.getUrl()));
        startActivity(i);
    }
}