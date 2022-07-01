package dailynews.localandglobalnews.models;

import com.google.gson.annotations.SerializedName;

public class OwnAdsModel {

    @SerializedName("id")
    private final String id;
    @SerializedName("app_id")
    private final String appId;
    @SerializedName("banner_img")
    private final String bannerImg;
    @SerializedName("banner_url")
    private final String bannerUrl;
    @SerializedName("banner_clicks")
    private final String bannerClicks;
    @SerializedName("interstitial_img")
    private final String interstitialImg;
    @SerializedName("interstitial_url")
    private final String interstitialUrl;
    @SerializedName("interstitial_clicks")
    private final String interstitialClicks;
    @SerializedName("native_img")
    private final String nativeImg;
    @SerializedName("native_url")
    private final String nativeUrl;
    @SerializedName("native_clicks")
    private final String nativeClicks;
    @SerializedName("native_size")
    private final String nativeSize;
    @SerializedName("ads_turn_on_or_off")
    private final String adsTurnOnOrOff;

    public OwnAdsModel(String id, String appId, String bannerImg, String bannerUrl, String bannerClicks, String interstitialImg, String interstitialUrl, String interstitialClicks, String nativeImg, String nativeUrl, String nativeClicks, String nativeSize, String adsTurnOnOrOff) {
        this.id = id;
        this.appId = appId;
        this.bannerImg = bannerImg;
        this.bannerUrl = bannerUrl;
        this.bannerClicks = bannerClicks;
        this.interstitialImg = interstitialImg;
        this.interstitialUrl = interstitialUrl;
        this.interstitialClicks = interstitialClicks;
        this.nativeImg = nativeImg;
        this.nativeUrl = nativeUrl;
        this.nativeClicks = nativeClicks;
        this.nativeSize = nativeSize;
        this.adsTurnOnOrOff = adsTurnOnOrOff;
    }

    public String getId() {
        return id;
    }

    public String getAppId() {
        return appId;
    }

    public String getBannerImg() {
        return bannerImg;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public String getBannerClicks() {
        return bannerClicks;
    }

    public String getInterstitialImg() {
        return interstitialImg;
    }

    public String getInterstitialUrl() {
        return interstitialUrl;
    }

    public String getInterstitialClicks() {
        return interstitialClicks;
    }

    public String getNativeImg() {
        return nativeImg;
    }

    public String getNativeUrl() {
        return nativeUrl;
    }

    public String getNativeClicks() {
        return nativeClicks;
    }

    public String getNativeSize() {
        return nativeSize;
    }

    public String getAdsTurnOnOrOff() {
        return adsTurnOnOrOff;
    }
}
