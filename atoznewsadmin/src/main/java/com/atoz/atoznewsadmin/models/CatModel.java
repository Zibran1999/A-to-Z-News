package com.atoz.atoznewsadmin.models;

import com.google.gson.annotations.SerializedName;

public class CatModel {
    private final String id;
    private final String banner;
    private final String title;
    private final String date;
    private final String time;
    @SerializedName("parent_id")
    private final Object parentId;
    private final Object subCat;
    private final Object news;

    public CatModel(String id, String banner, String title, String date, String time, Object parentId, Object subCat, Object news) {
        this.id = id;
        this.banner = banner;
        this.title = title;
        this.date = date;
        this.time = time;
        this.parentId = parentId;
        this.subCat = subCat;
        this.news = news;
    }

    public String getId() {
        return id;
    }

    public String getBanner() {
        return banner;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Object getParentId() {
        return parentId;
    }

    public Object getSubCat() {
        return subCat;
    }

    public Object getNews() {
        return news;
    }
}
