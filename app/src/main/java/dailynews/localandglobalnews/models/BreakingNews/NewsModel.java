package dailynews.localandglobalnews.models.BreakingNews;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NewsModel implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("catId")
    private String catId;
    @SerializedName("news_img")
    private String newsImg;
    @SerializedName("title")
    private String title;
    @SerializedName("url")
    private String url;
    @SerializedName("desc")
    private String hinDesc;

    @SerializedName("engDesc")
    private String engDesc;

    @SerializedName("date")
    private String date;
    @SerializedName("time")
    private String time;


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

    public String getHinDesc() {
        return hinDesc;
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
}