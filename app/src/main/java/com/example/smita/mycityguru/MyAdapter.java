package com.example.smita.mycityguru;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    ArrayList mData;
    LayoutInflater mInflater;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;

        public ViewHolder(View v) {
            super(v);

            textView = v.findViewById(R.id.placeText) ;
        }
    }

    public MyAdapter(Context context, ArrayList data) {

        this.mInflater=LayoutInflater.from(context);
        this.mData=data;

    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.search_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {

        String line = (String) mData.get(position);
        holder.textView.setText(line);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
