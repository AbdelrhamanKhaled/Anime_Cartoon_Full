
package com.app.cartoons.anime.newappcartoons.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Cartoon implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id = 0;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("thumb")
    @Expose
    private String thumb;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("visibility")
    @Expose
    private Boolean visibility;

    public Cartoon() {
    }

    public Cartoon(Integer id, String title, String thumb, int type) {
        this.id = id;
        this.title = title;
        this.thumb = thumb;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

}
