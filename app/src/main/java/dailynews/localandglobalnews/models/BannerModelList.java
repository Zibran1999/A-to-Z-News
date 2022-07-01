package dailynews.localandglobalnews.models;

import java.util.List;

public class BannerModelList {

    List<BannerModel> data = null;

    public BannerModelList(List<BannerModel> data) {
        this.data = data;
    }

    public List<BannerModel> getData() {
        return data;
    }
}
