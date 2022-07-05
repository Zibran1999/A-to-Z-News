package dailynews.localandglobalnews.models.BreakingNews;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NewsModel implements Serializable {
    @SerializedName("id")
    private final String id;
    @SerializedName("catId")
    private final String catId;
    @SerializedName("news_img")
    private final String newsImg;
    @SerializedName("title")
    private final String title;
    @SerializedName("engTitle")
    private final String engTitle;
    @SerializedName("url")
    private final String url;
    @SerializedName("desc")
    private final String hinDesc;

    @SerializedName("engDesc")
    private final String engDesc;

    @SerializedName("date")
    private final String date;
    @SerializedName("time")
    private final String time;

    public NewsModel(String id, String catId, String newsImg, String title, String engTitle, String url, String hinDesc, String engDesc, String date, String time) {
        this.id = id;
        this.catId = catId;
        this.newsImg = newsImg;
        this.title = title;
        this.engTitle = engTitle;
        this.url = url;
        this.hinDesc = hinDesc;
        this.engDesc = engDesc;
        this.date = date;
        this.time = time;
    }

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

    public String getEngTitle() {
        return engTitle;
    }
}


//    DELETE FROM cat_news
//        WHERE id NOT IN (
//        SELECT id
//        FROM (
//        SELECT id
//        FROM `cat_news`
//        WHERE catId = 2 ORDER BY id DESC
//        LIMIT 25
//        ) foo
//        )


