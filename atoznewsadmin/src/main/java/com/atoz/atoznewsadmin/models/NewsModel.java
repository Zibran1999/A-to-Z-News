package com.atoz.atoznewsadmin.models;

import com.google.gson.annotations.SerializedName;

public class NewsModel {
    @SerializedName("id")
    private String id;
    @SerializedName("catId")
    private String catId;
    @SerializedName("news_img")
    private String newsImg;
    @SerializedName("title")
    private String title;
    @SerializedName("engTitle")
    private String engTitle;
    @SerializedName("url")
    private String url;
    @SerializedName("desc")
    private String desc;
    @SerializedName("engDesc")
    private String engDesc;
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    private String time;
    private String trending;
    private String gadgets;
    private String breaking;


    public String getId() {
        return id;
    }

    public String getCatId() {
        return catId;
    }

    public String getNewsImg() {
        return newsImg;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getDesc() {
        return desc;
    }

    public String getEngDesc() {
        return engDesc;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getEngTitle() {
        return engTitle;
    }

    public String getTrending() {
        return trending;
    }

    public String getGadgets() {
        return gadgets;
    }

    public String getBreaking() {
        return breaking;
    }
}
