package com.example.kamin.thinkercodeart.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Idea {
    @SerializedName("ideaId")
    private String ideaId;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("author")
    public Author author;
    @SerializedName("files")
    private List<String> files = new ArrayList<String>();
    @SerializedName("tags")
    private List<String> tags = new ArrayList<String>();

    public String getIdeaId(){
        return ideaId;
    }
    public String getName(){
        return name;
    }
    public String getBodyIdea(){
        return description;
    }
    public Author getAuthor(){
            return author;
    }
    public List<String> getFiles(){
        return files;
    }
    public List<String> getTags(){
        return tags;
    }
    public Date getDate(){
        return new Date(Long.valueOf(ideaId.substring(0, 8 ), 16) * 1000);
    }
}
