package com.app.cartoons.anime.newappcartoons.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Episode implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("thumb")
    @Expose
    private String thumb;

    @SerializedName("video")
    @Expose
    private String video;

    @SerializedName("video1")
    @Expose
    private String video1;

    @SerializedName("video2")
    @Expose
    private String video2;

    @SerializedName("video3")
    @Expose
    private String video3;

    @SerializedName("video4")
    @Expose
    private String video4;

    @SerializedName("video5")
    @Expose
    private String video5;

    @SerializedName("xgetter")
    @Expose
    private Integer xGetter;

    @SerializedName("xgetter1")
    @Expose
    private Integer xGetter1;

    @SerializedName("xgetter2")
    @Expose
    private Integer xGetter2;

    @SerializedName("xgetter3")
    @Expose
    private Integer xGetter3;

    @SerializedName("xgetter4")
    @Expose
    private Integer xGetter4;

    @SerializedName("xgetter5")
    @Expose
    private Integer xGetter5;

    @SerializedName("playlistId")
    @Expose
    private int playlistId;

    public Episode() {
    }

    public Episode(int id, String title, String thumb, String video, int playlistId) {
        this.id = id;
        this.title = title;
        this.thumb = thumb;
        this.video = video;
        this.playlistId = playlistId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideo1() {
        return video1;
    }

    public void setVideo1(String video1) {
        this.video1 = video1;
    }

    public String getVideo2() {
        return video2;
    }

    public void setVideo2(String video2) {
        this.video2 = video2;
    }

    public String getVideo3() {
        return video3;
    }

    public void setVideo3(String video3) {
        this.video3 = video3;
    }

    public String getVideo4() {
        return video4;
    }

    public void setVideo4(String video4) {
        this.video4 = video4;
    }

    public String getVideo5() {
        return video5;
    }

    public void setVideo5(String video5) {
        this.video5 = video5;
    }

    public Integer getxGetter() {
        return xGetter;
    }

    public void setxGetter(Integer xGetter) {
        this.xGetter = xGetter;
    }

    public Integer getxGetter1() {
        return xGetter1;
    }

    public void setxGetter1(Integer xGetter1) {
        this.xGetter1 = xGetter1;
    }

    public Integer getxGetter2() {
        return xGetter2;
    }

    public void setxGetter2(Integer xGetter2) {
        this.xGetter2 = xGetter2;
    }

    public Integer getxGetter3() {
        return xGetter3;
    }

    public void setxGetter3(Integer xGetter3) {
        this.xGetter3 = xGetter3;
    }

    public Integer getxGetter4() {
        return xGetter4;
    }

    public void setxGetter4(Integer xGetter4) {
        this.xGetter4 = xGetter4;
    }

    public Integer getxGetter5() {
        return xGetter5;
    }

    public void setxGetter5(Integer xGetter5) {
        this.xGetter5 = xGetter5;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }
}
