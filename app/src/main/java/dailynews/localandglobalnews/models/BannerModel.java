package dailynews.localandglobalnews.models;

public class BannerModel {

    String id, image,title, url;

    public BannerModel(String id, String image, String title, String url) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
