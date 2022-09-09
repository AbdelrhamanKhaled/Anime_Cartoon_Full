package com.anime.rashon.speed.loyert.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponse{

    @SerializedName("user_info")
    @Expose
    private User user;
    @SerializedName("message")
    @Expose
    private String message ;
    @SerializedName("code")
    @Expose
    private int code ;
    @SerializedName("error")
    @Expose
    private boolean error ;

    public UserResponse(String message, int code, boolean error) {
        this.message = message;
        this.code = code;
        this.error = error;
    }



    public UserResponse() {
    }


    public UserResponse(User user, String message, int code, boolean error) {
        this.user = user;
        this.message = message;
        this.code = code;
        this.error = error;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
