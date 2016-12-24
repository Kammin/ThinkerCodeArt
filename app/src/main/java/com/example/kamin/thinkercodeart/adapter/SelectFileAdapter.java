package com.example.kamin.thinkercodeart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.activity.FileManagerActivity;

import java.io.File;



public class SelectFileAdapter extends RecyclerView.Adapter<SelectFileAdapter.MainViewHolder> {
    final static public String TAG = FileManagerActivity.class.getSimpleName();
    File[] fileArray;
    Context context;
    int itemWidth;
    int padding = 3;


    public SelectFileAdapter(Context context, File[] fileArray, int itemWidth) {
        this.fileArray = fileArray;
        this.context = context;
        this.itemWidth = itemWidth;
    }


    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new MainViewHolder(LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false));

    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        holder.ivIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.emptyphoto2));
        Glide.with(context)
                .load(fileArray[position])
                .crossFade(5)
                .override(itemWidth, itemWidth)
                .into(holder.ivIcon);
    }

    @Override
    public int getItemCount() {
        return fileArray.length;
    }


    public class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;

        public MainViewHolder(View v) {
            super(v);
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.width = itemWidth - (padding);
            params.height = itemWidth - (padding*2);
            v.setLayoutParams(params);
            v.setPadding(padding, padding, padding, padding);
            this.ivIcon = (ImageView) v.findViewById(R.id.ivIcon);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Redraw the old selection and the new
                }
            });
        }
    }

}