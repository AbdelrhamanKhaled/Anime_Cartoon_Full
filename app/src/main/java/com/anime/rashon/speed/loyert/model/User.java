package com.anime.rashon.speed.loyert.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    @Expose
    private int id ;
    @SerializedName("name")
    @Expose
    private String name ;
    @SerializedName("photo_url")
    @Expose
    private String photo_url ;
    private String token ;
    private String email ;


    public User(int id, String name, String photo_url, String token, String email) {
        this.id = id;
        this.name = name;
        this.photo_url = photo_url;
        this.token = token;
        this.email = email;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
