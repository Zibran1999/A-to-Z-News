package dailynews.localandglobalnews.utils;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.applovin.mediation.ads.MaxInterstitialAd;
import com.ironsource.mediationsdk.IronSource;

import java.util.Objects;

import io.paperdb.Paper;

public class ShowAds implements LifecycleObserver {

    Ads ads = new Ads();
    RelativeLayout topAdview, bottomAdview;
    Activity context;
    String networkName, bannerId;
    String nativeAds, nativeNetwork;

    public ShowAds(Activity activity, RelativeLayout AdViewTop, RelativeLayout AdViewBottom) {
        context = activity;
        topAdview = AdViewTop;
        bottomAdview = AdViewBottom;
    }

    public ShowAds() {

    }

    public void showInterstitialAds(Activity context) {
        if ("AdmobWithMeta".equals(Paper.book().read(Prevalent.interstitialNetwork))) {
            Log.d(TAG, "AdmobWithMeta");
            ads.admobInterstitialAd(context, Paper.book().read(Prevalent.interstitialAds));

        } else if ("IronSourceWithMeta".equals(Paper.book().read(Prevalent.interstitialNetwork))) {
            Log.d(TAG, "IronSourceWithMeta");
            ads.ironSourceInterstitialAd(context, Paper.book().read(Prevalent.interstitialAds));


        } else if ("AppLovinWithMeta".equals(Paper.book().read(Prevalent.interstitialNetwork))) {
            Log.d(TAG, "AppLovinWithMeta");
            MaxInterstitialAd maxInterstitialAd = ads.appLovinAdInterstitialAd(context, Paper.book().read(Prevalent.interstitialAds));

            new Handler().postDelayed(() -> {
                if (maxInterstitialAd.isReady()) {
                    maxInterstitialAd.showAd();
                    Log.d(TAG, "ads ready");
                } else {
                    ads.appLovinAdInterstitialAd(context, Paper.book().read(Prevalent.interstitialAds));
                }
            }, 3000);
        } else if ("Meta".equals(Paper.book().read(Prevalent.interstitialNetwork))) {

            ads.metaInterstitialAd(context, Paper.book().read(Prevalent.interstitialAds));
        }

    }

    public void showTopBanner(Activity context, RelativeLayout topAdView) {
        networkName = Paper.book().read(Prevalent.bannerTopNetworkName);
        bannerId = Paper.book().read(Prevalent.bannerTop);
        ads.showBannerAd(context, topAdView, networkName, bannerId);

    }

    public void showBottomBanner(Activity context, RelativeLayout bottomAdView) {
        networkName = Paper.book().read(Prevalent.bannerBottomNetworkName);
        bannerId = Paper.book().read(Prevalent.bannerBottom);
        ads.showBannerAd(context, bottomAdView, networkName, bannerId);

    }

    public void showNativeAds(Activity context, FrameLayout frameLayout) {
        nativeAds = Paper.book().read(Prevalent.nativeAds);
        nativeNetwork = Paper.book().read(Prevalent.nativeAdsNetworkName);
        frameLayout.setVisibility(View.VISIBLE);
        switch (Objects.requireNonNull(nativeNetwork)) {
            case "AdmobWithMeta":
                ads.loadAdmobNativeAd(context, frameLayout);
                break;
            case "IronSourceWithMeta":
                ads.IronSourceMRECBanner(context, frameLayout);

                break;
            case "AppLovinWithMeta":
                ads.appLovinNativeAds(context, frameLayout);

                break;
            case "Meta":
                ads.metaNativeAds(context, frameLayout);
                break;
        }
    }

    public void showMrec(Activity context, FrameLayout frameLayout) {
        nativeAds = Paper.book().read(Prevalent.nativeAds);
        nativeNetwork = Paper.book().read(Prevalent.nativeAdsNetworkName);
        frameLayout.setVisibility(View.VISIBLE);
        switch (Objects.requireNonNull(nativeNetwork)) {
            case "AppLovinWithMeta":
                ads.appLovinMREC(context, frameLayout);
                break;
            case "Meta":
                ads.metaMREC(context, frameLayout);
                break;
        }
    }

    public void destroyBanner() {
        ads.destroyBanner();
        Log.d("ContentValue", "destroyBanner");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        IronSource.onResume(context);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        IronSource.onPause(context);
    }
}