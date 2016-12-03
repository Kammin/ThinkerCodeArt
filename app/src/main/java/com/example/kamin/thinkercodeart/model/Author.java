package com.example.kamin.thinkercodeart.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Author implements Serializable {
    @SerializedName("userId")
    public String userId;
    @SerializedName("email")
    public String email;
    @SerializedName("enabled")
    public Boolean enabled;
    @SerializedName("username")
    public String username;
    @SerializedName("roles")
    private List<String> roles = new ArrayList<String>();

    public String getUserId(){
        return userId;
    }
    public String getEmail(){
        return email;
    }
    public Boolean getEnabled(){
        return enabled;
    }
    public String getUsername(){
        return username;
    }
    public List<String> getRoles(){
        return roles;
    }
}
