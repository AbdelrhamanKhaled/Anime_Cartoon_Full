package com.anime.rashon.speed.loyert.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feedbacks {
    @SerializedName("cartoonId")
    @Expose
    private int cartoonId ;
    @SerializedName("userId")
    @Expose
    private int userID ;
    @SerializedName("_feedback")
    @Expose
    private String feedback ;
    @SerializedName("name")
    @Expose
    private String username ;
    @SerializedName("photo_Uri")
    @Expose
    private String photo_Uri ;

    public Feedbacks() {
    }

    public Feedbacks(int cartoonId, int userID, String feedback, String username, String photo_Uri) {
        this.cartoonId = cartoonId;
        this.userID = userID;
        this.feedback = feedback;
        this.username = username;
        this.photo_Uri = photo_Uri;
    }

    public int getCartoonId() {
        return cartoonId;
    }

    public void setCartoonId(int cartoonId) {
        this.cartoonId = cartoonId;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto_Uri() {
        return photo_Uri;
    }

    public void setPhoto_Uri(String photo_Uri) {
        this.photo_Uri = photo_Uri;
    }
}
