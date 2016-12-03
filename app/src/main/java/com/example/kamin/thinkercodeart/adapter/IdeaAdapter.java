package com.example.kamin.thinkercodeart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.model.Idea;

import java.util.List;

/**
 * Created by Kamin on 03.12.2016.
 */

public class IdeaAdapter extends RecyclerView.Adapter<IdeaAdapter.IdeaViewHolder> {
    private List<Idea> ideas;
    private int rowLayout;
    private Context context;

    public IdeaAdapter(List<Idea> ideas, int rowLayout, Context context) {
        this.ideas = ideas;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    public class IdeaViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ideaLayout;
        ImageView thumbnail;
        TextView nameIdea;
        TextView bodyIdea;
        TextView author;
        TextView date;
        TextView tags;
        public IdeaViewHolder(View itemView) {
            super(itemView);
            ideaLayout = (LinearLayout) itemView.findViewById(R.id.ideaLayout);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            nameIdea = (TextView) itemView.findViewById(R.id.nameIdea);
            //bodyIdea = (TextView) itemView.findViewById(R.id.bodyIdea);
            author = (TextView) itemView.findViewById(R.id.author);
            date = (TextView) itemView.findViewById(R.id.date);
            tags = (TextView) itemView.findViewById(R.id.tags);
        }
    }


    @Override
    public IdeaAdapter.IdeaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new IdeaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IdeaAdapter.IdeaViewHolder holder, int position) {
        holder.nameIdea.setText(ideas.get(position).getName());
      //  holder.bodyIdea.setText(ideas.get(position).getBodyIdea());
        holder.author.setText(ideas.get(position).getAuthor().getUsername());
        holder.date.setText(DateFormat.format("dd.MM.yyyy", ideas.get(position).getDate()).toString());
        holder.tags.setText(ideas.get(position).getTags().toString());

    }

    @Override
    public int getItemCount() {
        return ideas.size();
    }


}
