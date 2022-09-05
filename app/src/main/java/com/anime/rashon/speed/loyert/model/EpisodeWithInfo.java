package com.anime.rashon.speed.loyert.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EpisodeWithInfo extends Episode {

    @SerializedName("story")
    @Expose
    private String story;

    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("world_rate")
    @Expose
    private String world_rate;

    @SerializedName("view_date")
    @Expose
    private String view_date;

    @SerializedName("age_rate")
    @Expose
    private Integer age_rate;

    @SerializedName("status")
    @Expose
    private Integer status;

    public EpisodeWithInfo() {

    }

    public EpisodeWithInfo(int id, String title, String thumb, String video, int playlistId, String story, String category, String world_rate, String view_date, Integer age_rate, Integer status) {
        super(id, title, thumb, video, playlistId);
        this.story = story;
        this.category = category;
        this.world_rate = world_rate;
        this.view_date = view_date;
        this.age_rate = age_rate;
        this.status = status;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWorld_rate() {
        return world_rate;
    }

    public void setWorld_rate(String world_rate) {
        this.world_rate = world_rate;
    }

    public String getView_date() {
        return view_date;
    }

    public void setView_date(String view_date) {
        this.view_date = view_date;
    }

    public Integer getAge_rate() {
        return age_rate;
    }

    public void setAge_rate(Integer age_rate) {
        this.age_rate = age_rate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


}
