package com.example.kamin.thinkercodeart.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Idea {
    private String ideaId;
    private String name;
    private String BodyIdea;
    public Author author;
    private List<String> files = new ArrayList<String>();
    private List<String> tags = new ArrayList<String>();

    public String getIdeaId(){
        return ideaId;
    }
    public void setIdeaId(String ideaId){
        this.ideaId= ideaId;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name= name;
    }
    public String getBodyIdea(){
        return BodyIdea;
    }
    public void setBodyIdea(String BodyIdea){
        this.BodyIdea= BodyIdea;
    }
    public Author getAuthor(){
            return author;
    }
    public void setAuthor(Author author){
        this.author= author;
    }
    public List<String> getFiles(){
        return files;
    }
    public void setFiles(List<String> files){
        this.files= files;
    }
    public List<String> getTags(){
        return tags;
    }
    public void setTags(List<String> tags){
        this.tags= tags;
    }
    public Date getDate(){
        return new Date(Long.valueOf(ideaId.substring(0, 8 ), 16) * 1000);
    }
}
