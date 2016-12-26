package com.example.kamin.thinkercodeart.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.activity.MainActivity;
import com.example.kamin.thinkercodeart.model.Idea;
import com.example.kamin.thinkercodeart.util.URLs;
import com.example.kamin.thinkercodeart.volley.Singleton;

import java.util.List;


public class IdeaAdapter extends RecyclerView.Adapter<IdeaAdapter.IdeaViewHolder> {
    private List<Idea> ideas;
    private int rowLayout;
    private Context context;
    String curentUserId;
    Boolean adminRoot = false;
    private static final String TAG = IdeaAdapter.class.getSimpleName();
    MainActivity mainActivity;

    public IdeaAdapter(List<Idea> ideas, int rowLayout, Context context, MainActivity mainActivity) {
        this.ideas = ideas;
        this.rowLayout = rowLayout;
        this.context = context;
        this.mainActivity = mainActivity;
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(context);
        curentUserId = sPref.getString(context.getResources().getString(R.string.ACTIVE_USER_ID), "");
        adminRoot = sPref.getString(context.getResources().getString(R.string.ACTIVE_ROLES), "").contains("ROLE_ADMIN");
    }

    public class IdeaViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        ImageView cover;
        TextView nameIdea;
        TextView bodyIdea;
        TextView author;
        TextView date;
        TextView delete;
        LinearLayout parentLLforTags;
        ProgressBar progressBarPhoto;
        TextView like;
        TextView dislike;

        public IdeaViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            cover = (ImageView) itemView.findViewById(R.id.cover);
            nameIdea = (TextView) itemView.findViewById(R.id.nameIdea);
            bodyIdea = (TextView) itemView.findViewById(R.id.bodyIdea);
            author = (TextView) itemView.findViewById(R.id.author);
            date = (TextView) itemView.findViewById(R.id.date);
            delete = (TextView) itemView.findViewById(R.id.deleteIdea);
            parentLLforTags = (LinearLayout) itemView.findViewById(R.id.parentLLforTags);
            progressBarPhoto = (ProgressBar) itemView.findViewById(R.id.progressBarPhoto);
            like = (TextView) itemView.findViewById(R.id.likeCount);
            dislike = (TextView) itemView.findViewById(R.id.dislikeCount);
        }
    }


    @Override
    public IdeaAdapter.IdeaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "viewType " + viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new IdeaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IdeaAdapter.IdeaViewHolder holder, int position) {
        final int pos = position;
        final String ideaID = ideas.get(position).getIdeaId();
        final String userID = ideas.get(position).getUserId();
        Log.d(TAG, "onBindViewHolder " + position);
        holder.nameIdea.setText(ideas.get(position).getName());
        holder.bodyIdea.setText(ideas.get(position).getBodyIdea());
        holder.author.setText(ideas.get(position).getUsername());
        holder.date.setText(DateFormat.format("dd.MM.yyyy", ideas.get(position).getDate()).toString());
        if (curentUserId.equals(ideas.get(position).getUserId()) || (adminRoot))
            holder.delete.setVisibility(View.VISIBLE);
        else
            holder.delete.setVisibility(View.GONE);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.deletIdea(ideaID, pos);
            }
        });
        holder.date.setText(DateFormat.format("dd.MM.yyyy", ideas.get(position).getDate()).toString());
        List<String> tags = ideas.get(position).getTags();
        holder.parentLLforTags.removeAllViews();
        holder.cover.setImageBitmap(null);
        for (int i = 0; i < tags.size(); i++) {
            final TextView tvTag = new TextView(context);
            holder.parentLLforTags.addView(tvTag);
            tvTag.setText(tags.get(i));
            tvTag.setTextSize(16);
            tvTag.setPadding((int) context.getResources().getDimension(R.dimen.tag_padding), 0, 0, 0);
            tvTag.setTextColor(context.getResources().getColor(R.color.colorHashTag));
            tvTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "" + tvTag.getText(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        List<String> photos = ideas.get(position).getFiles();
        holder.like.setText(""+ideas.get(position).getLikes());
        holder.dislike.setText(""+ideas.get(position).getDislikes());


        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.search("author", userID, "");
            }
        });

        String avatarURL = URLs.USERS.concat("/" + ideas.get(position).getUserId() + "/avatar");
        final ImageView imageViewavatar = holder.avatar;
        ImageLoader.ImageContainer containerAvatar = Singleton.getInstance(context).getImageLoader().get(avatarURL, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();
                if (bitmap != null) {
                    imageViewavatar.setImageBitmap(bitmap);
                    imageViewavatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    bitmap.getWidth();
                    Log.d(TAG, "bitmap.getWidth() " + bitmap.getWidth());
                    Log.d(TAG, "bitmap.getHeight() " + bitmap.getHeight());
                    imageViewavatar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                        }
                    });
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.d(TAG, "error avatar load " + error.networkResponse.statusCode);
            }
        });
        final ImageView imageView = holder.cover;
        if (photos.size() != 0) {
            final ProgressBar progressBarPhoto = holder.progressBarPhoto;
            progressBarPhoto.setVisibility(View.VISIBLE);
            final String coverURL = URLs.IDEAS.concat("/" + ideas.get(position).getIdeaId() + "/cover");
            final ImageLoader.ImageContainer container = Singleton.getInstance(context).getImageLoader().get(coverURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap = response.getBitmap();
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        progressBarPhoto.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBarPhoto.setVisibility(View.GONE);
                    Log.d(TAG, "ErrorResponse " + coverURL);

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return ideas.size();
    }

}