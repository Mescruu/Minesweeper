package com.example.minesweeper;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.zip.Inflater;

public class CustomAdapter extends BaseAdapter {
    Context context;
    String LoginList[];
    String rankList[];
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext,String[] rankList, String[] LoginList) {
        this.context = context;
        this.LoginList = LoginList;
        this.rankList = rankList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return rankList.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.rank_list, null);
        TextView loginListText = (TextView) view.findViewById(R.id.LoginView);
        TextView rankListText = (TextView) view.findViewById(R.id.RankView);
        TextView positionText = (TextView) view.findViewById(R.id.postition);

        positionText.setText(String.valueOf(i+1));
        loginListText.setText(LoginList[i]);
        rankListText.setText(rankList[i]);
        return view;
    }
}