package com.atoz.atoznewsadmin.models;

public class AdsModel {

    private final String id;
    private final String appId;
    private final String appLovinAppKey;
    private final String bannerTop;
    private final String bannerTopAdNetwork;
    private final String bannerBottom;
    private final String bannerBottomAdNetwork;
    private final String interstitial;
    private final String interstitalAdNetwork;
    private final String nativeAd;
    private final String nativeAdNetwork;
    private final String nativeType;
    private final String appOpenAdNetwork;
    private final String appOpenAd;

    public AdsModel(String id, String appId, String appLovinAppKey, String bannerTop, String bannerTopAdNetwork, String bannerBottom, String bannerBottomAdNetwork, String interstitial, String interstitalAdNetwork, String nativeAd, String nativeAdNetwork, String nativeType, String appOpenAdNetwork, String appOpenAd) {
        this.id = id;
        this.appId = appId;
        this.appLovinAppKey = appLovinAppKey;
        this.bannerTop = bannerTop;
        this.bannerTopAdNetwork = bannerTopAdNetwork;
        this.bannerBottom = bannerBottom;
        this.bannerBottomAdNetwork = bannerBottomAdNetwork;
        this.interstitial = interstitial;
        this.interstitalAdNetwork = interstitalAdNetwork;
        this.nativeAd = nativeAd;
        this.nativeAdNetwork = nativeAdNetwork;
        this.nativeType = nativeType;
        this.appOpenAdNetwork = appOpenAdNetwork;
        this.appOpenAd = appOpenAd;
    }

    public String getId() {
        return id;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppLovinAppKey() {
        return appLovinAppKey;
    }

    public String getBannerTop() {
        return bannerTop;
    }

    public String getBannerTopAdNetwork() {
        return bannerTopAdNetwork;
    }

    public String getBannerBottom() {
        return bannerBottom;
    }

    public String getBannerBottomAdNetwork() {
        return bannerBottomAdNetwork;
    }

    public String getInterstitial() {
        return interstitial;
    }

    public String getInterstitalAdNetwork() {
        return interstitalAdNetwork;
    }

    public String getNativeAd() {
        return nativeAd;
    }

    public String getNativeAdNetwork() {
        return nativeAdNetwork;
    }

    public String getNativeType() {
        return nativeType;
    }

    public String getAppOpenAdNetwork() {
        return appOpenAdNetwork;
    }

    public String getAppOpenAd() {
        return appOpenAd;
    }
}
