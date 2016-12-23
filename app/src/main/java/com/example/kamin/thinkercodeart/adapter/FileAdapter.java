package com.example.kamin.thinkercodeart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.activity.FileManagerActivity;

import java.io.File;

/**
 * Created by Anton on 22.12.2016.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.MainViewHolder> {
    final static public String TAG = FileManagerActivity.class.getSimpleName();
    File[] fileArray;
    Context context;
    int itemWidth;
    int padding = 3;
    boolean hasParent;

    private static final int TYPE_DIRECTORY = 0;
    private static final int TYPE_PHOTO = 1;

    public FileAdapter(Context context, File[] fileArray, int itemWidth, boolean hasParent) {
        this.fileArray = fileArray;
        this.context = context;
        this.itemWidth = itemWidth;
        this.hasParent = hasParent;
    }

    @Override
    public int getItemViewType(int position) {
        return (fileArray[position].isDirectory() ? TYPE_DIRECTORY : TYPE_PHOTO);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_DIRECTORY: {
                return new DirectoryHolder(LayoutInflater.from(context).inflate(R.layout.item_dir, parent, false));
            }
            case TYPE_PHOTO: {
                return new PhotoHolder(LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false));
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_DIRECTORY) {
            DirectoryHolder dirHolder = (DirectoryHolder) holder;
            if (hasParent&&(position==0)) {
                dirHolder.ivDir.setImageDrawable(context.getResources().getDrawable(R.mipmap.folderparent));
            } else
                dirHolder.ivDir.setImageDrawable(context.getResources().getDrawable(R.mipmap.folder));
            dirHolder.tvDirName.setText(fileArray[position].getName());
            Log.d(TAG, "getName " + fileArray[position].getName() + " getAbsolutePath " + fileArray[position].getAbsolutePath());
        }
        if (holder.getItemViewType() == TYPE_PHOTO) {
            PhotoHolder fileHolder = (PhotoHolder) holder;
            fileHolder.ivIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.emptyphoto2));
        }
    }

    @Override
    public int getItemCount() {
        return fileArray.length;
    }


    public class PhotoHolder extends MainViewHolder {
        ImageView ivIcon;

        public PhotoHolder(View v) {
            super(v);
            ViewGroup.LayoutParams params = v.getLayoutParams();
            //Log.d(TAG,"v.getLayoutParams().width "+v.getWidth());
            params.width = itemWidth - padding;
            params.height = itemWidth - padding;
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

    public class DirectoryHolder extends MainViewHolder {
        TextView tvDirName;
        ImageView ivDir;

        public DirectoryHolder(View v) {
            super(v);
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.width = itemWidth - padding;
            params.height = itemWidth - padding;
            v.setLayoutParams(params);
            v.setPadding(padding, padding, padding, padding);
            this.tvDirName = (TextView) v.findViewById(R.id.tvDirName);
            this.ivDir = (ImageView) v.findViewById(R.id.ivDir);

        }
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        public MainViewHolder(View v) {
            super(v);
        }
    }

}