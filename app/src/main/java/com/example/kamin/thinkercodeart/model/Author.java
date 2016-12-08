package com.example.kamin.thinkercodeart.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Author implements Serializable {
    public String userId;
    public String email;
    public Boolean enabled;
    public String username;
    private List<String> roles = new ArrayList<String>();

    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public Boolean getEnabled(){
        return enabled;
    }
    public void setEnabled(Boolean enabled){
        this.enabled=enabled;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public List<String> getRoles(){
        return roles;
    }
    public void setRoles(List<String> roles){
        this.roles = roles;
    }
}
