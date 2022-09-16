package com.anime.rashon.speed.loyert.model;
import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Keep
public class CartoonWithInfo extends Cartoon {

    @SerializedName("world_rate")
    @Expose
    private String world_rate;

    @SerializedName("view_date")
    @Expose
    private String view_date;

    @SerializedName("status")
    @Expose
    private Integer status;

    public CartoonWithInfo() {

    }

    public CartoonWithInfo(Integer id, String title, String thumb, int type, Boolean visibility, int rate, String world_rate, String view_date, Integer status) {
        super(id, title, thumb, type, visibility, rate);
        this.world_rate = world_rate;
        this.view_date = view_date;
        this.status = status;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(obj instanceof CartoonWithInfo)) {
            return false;
        }
        return Objects.equals(((CartoonWithInfo) obj).getId(), this.getId());
    }
}
