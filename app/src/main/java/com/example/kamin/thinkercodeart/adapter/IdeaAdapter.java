package com.example.kamin.thinkercodeart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.model.Idea;

import java.util.List;


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
        ImageView thumbnail;
        TextView nameIdea;
        TextView bodyIdea;
        TextView author;
        TextView date;
        TextView tags;
        LinearLayout parentLLforTags;
        public IdeaViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            nameIdea = (TextView) itemView.findViewById(R.id.nameIdea);
            bodyIdea = (TextView) itemView.findViewById(R.id.bodyIdea);
            author = (TextView) itemView.findViewById(R.id.author);
            date = (TextView) itemView.findViewById(R.id.date);
            parentLLforTags = (LinearLayout) itemView.findViewById(R.id.parentLLforTags);
        }
    }


    @Override
    public void onViewDetachedFromWindow(IdeaViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Log.d("",""+holder.nameIdea);
    }

    @Override
    public void onViewAttachedToWindow(IdeaViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public IdeaAdapter.IdeaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new IdeaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IdeaAdapter.IdeaViewHolder holder, int position) {
        holder.nameIdea.setText(ideas.get(position).getName());
        holder.bodyIdea.setText(ideas.get(position).getBodyIdea());
        holder.author.setText(ideas.get(position).getAuthor().getUsername());
        holder.date.setText(DateFormat.format("dd.MM.yyyy", ideas.get(position).getDate()).toString());
        List<String> tags = ideas.get(position).getTags();
        for (int i = 0;i<tags.size();i++){
            final TextView tvTag = new TextView(context);
            holder.parentLLforTags.addView(tvTag);
            tvTag.setText(tags.get(i));
            tvTag.setPadding((int)context.getResources().getDimension(R.dimen.tag_padding),0,0,0);
            tvTag.setTextColor(context.getResources().getColor(R.color.colorHashTag));
            tvTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,""+tvTag.getText(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ideas.size();
    }


}
