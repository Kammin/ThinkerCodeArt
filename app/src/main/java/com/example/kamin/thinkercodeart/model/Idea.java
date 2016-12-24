package com.example.kamin.thinkercodeart.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Idea {
    private String ideaId;
    private String name;
    private String BodyIdea;
    private String userId;
    private String username;
    private List<String> files = new ArrayList<String>();
    private List<String> tags = new ArrayList<String>();
    private int likes;
    private int dislikes;
    private int userLikeStatus;

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

    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId= userId;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username= username;
    }

    public String getBodyIdea(){
        return BodyIdea;
    }
    public void setBodyIdea(String BodyIdea){
        this.BodyIdea= BodyIdea;
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

    public int getLikes(){
        return likes;
    }
    public void setLikes(int likes){
        this.likes= likes;
    }

    public int getDislikes(){
        return dislikes;
    }
    public void setDislikes(int dislikes){
        this.dislikes= dislikes;
    }

    public int getUserLikeStatus(){
        return userLikeStatus;
    }
    public void setUserLikeStatus(int userLikeStatus){
        this.userLikeStatus= userLikeStatus;
    }

    public Date getDate(){
        return new Date(Long.valueOf(ideaId.substring(0, 8 ), 16) * 1000);
    }

}
