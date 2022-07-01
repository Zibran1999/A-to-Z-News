package dailynews.localandglobalnews.utils;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import dailynews.localandglobalnews.R;
import dailynews.localandglobalnews.databinding.BannerAdsLayoutBinding;
import dailynews.localandglobalnews.databinding.InterstitialAdsLayoutBinding;
import dailynews.localandglobalnews.databinding.NativeAdsLayoutBinding;
import io.paperdb.Paper;

public class Ads implements LifecycleObserver {

    public static InterstitialAd mInterstitialAd;
    private static MaxAd nativeAd;
    public IronSourceBannerLayout banner;
    public NativeAd nativeAds;
    MaxAdView maxAdView;
    MaxInterstitialAd interstitialAd;
    Dialog dialog;
    FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences preferences;
    private com.facebook.ads.NativeAd fbNativeAd;

    public void destroyBanner() {
        if (Objects.equals(Paper.book().read(Prevalent.bannerBottomNetworkName), "IronSourceWithMeta")) {
            IronSource.destroyBanner(banner);
            Log.d("destroy", "banner");
        } else if (Objects.equals(Paper.book().read(Prevalent.bannerTopNetworkName), "IronSourceWithMeta")) {
            IronSource.destroyBanner(banner);
            Log.d("destroy", "banner");
        }

    }

    public void showBannerAd(Activity context, RelativeLayout container, String networkName, String bannerAdId) {

        switch (networkName) {
            case "AdmobWithMeta":
                MobileAds.initialize(context);
                AudienceNetworkAds.initialize(context);
                AdRequest adRequest = new AdRequest.Builder().build();
                AdView adView = new AdView(context);
                container.addView(adView);
                adView.setAdUnitId(bannerAdId);
                adView.setAdSize(AdSize.BANNER);
                adView.loadAd(adRequest);
                container.setVisibility(View.VISIBLE);
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        Log.d("ContentValues", "bannerAds Failed");
                        int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                        if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                            setOwnBannerAds(context, container, pos);
                            Toast.makeText(context, MyApp.ownAdsModels.get(pos).getBannerImg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                    }
                });

                break;
            case "IronSourceWithMeta":
                IronSource.init(context, bannerAdId);
                AudienceNetworkAds.initialize(context);
                IronSource.setMetaData("Facebook_IS_CacheFlag", "IMAGE");
                banner = IronSource.createBanner(context, ISBannerSize.BANNER);
                container.addView(banner);

                if (banner != null) {
                    // set the banner listener
                    Log.d("banner", String.valueOf(banner.getId()));
                    IronSource.loadBanner(banner);

                    banner.setBannerListener(new BannerListener() {
                        @Override
                        public void onBannerAdLoaded() {
                            Log.d(TAG, "onBannerAdLoaded");
                            container.setVisibility(View.VISIBLE);
                            // since banner container was "gone" by default, we need to make it visible as soon as the banner is ready
                        }

                        @Override
                        public void onBannerAdLoadFailed(IronSourceError error) {
                            Log.d(TAG, "onBannerAdLoadFailed" + " " + error);
                            int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                            if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                                setOwnBannerAds(context, container, pos);
                                container.setVisibility(View.VISIBLE);

                            }

                        }

                        @Override
                        public void onBannerAdClicked() {
                            Log.d(TAG, "onBannerAdClicked");
                        }

                        @Override
                        public void onBannerAdScreenPresented() {
                            Log.d(TAG, "onBannerAdScreenPresented");
                        }

                        @Override
                        public void onBannerAdScreenDismissed() {
                            Log.d(TAG, "onBannerAdScreenDismissed");
                        }

                        @Override
                        public void onBannerAdLeftApplication() {
                            Log.d(TAG, "onBannerAdLeftApplication");
                        }
                    });

                    // load ad into the created banner
                } else {
                    Toast.makeText(context, "IronSource.createBanner returned null", Toast.LENGTH_LONG).show();
                }

                break;
            case "AppLovinWithMeta":
                AudienceNetworkAds.initialize(context);
                AppLovinSdk.getInstance(context).setMediationProvider("max");
                AppLovinSdk.initializeSdk(context);
                maxAdView = new MaxAdView(bannerAdId, context);
                maxAdView.setListener(new MaxAdViewAdListener() {
                    @Override
                    public void onAdExpanded(MaxAd ad) {

                    }

                    @Override
                    public void onAdCollapsed(MaxAd ad) {

                    }

                    @Override
                    public void onAdLoaded(MaxAd ad) {
                        maxAdView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {

                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {

                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {

                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, MaxError error) {
                        int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                        if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                            setOwnBannerAds(context, container, pos);
                        }
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                        int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                        if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                            setOwnBannerAds(context, container, pos);
                        }
                    }
                });

                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = context.getResources().getDimensionPixelSize(R.dimen.banner_height);
                maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
                maxAdView.setBackgroundColor(Color.WHITE);
                container.addView(maxAdView);
                maxAdView.loadAd();
                //To show a banner, make the following calls:
                maxAdView.startAutoRefresh();


                break;

            case "Meta":
                AudienceNetworkAds.initialize(context);
                com.facebook.ads.AdView adView1 = new com.facebook.ads.AdView(context, bannerAdId, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                container.addView(adView1);

                com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                    @Override
                    public void onError(Ad ad, AdError adError) {
                        Log.e(TAG, "Banner ad finished downloading all assets.");
                        int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                        if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                            setOwnBannerAds(context, container, pos);
                            Log.e(TAG, "Banner ads finished downloading all assets.");

                        }
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {

                    }

                    @Override
                    public void onAdClicked(Ad ad) {

                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {

                    }
                };
                adView1.loadAd(adView1.buildLoadAdConfig().withAdListener(adListener).build());
                break;
        }
    }

    public void admobInterstitialAd(Activity context, String interstitialId) {
        MobileAds.initialize(context);
        AudienceNetworkAds.initialize(context);
        // Initialize an InterstitialAd.
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context, interstitialId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.show(context);
                        Log.i(TAG, "onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                preferences = PreferenceManager.getDefaultSharedPreferences(context);
                                if (preferences.getString("action", "").equals("")) {
                                } else {
//                                    preferences.edit().clear().apply();
                                }
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                        int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                        if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                            context.runOnUiThread(() -> showOwnInterstitialAds(context, pos));
                        }
                    }

                });


    }

    public MaxInterstitialAd appLovinAdInterstitialAd(Activity context, String interstitialId) {
        AppLovinSdk.getInstance(context).setMediationProvider("max");
        AppLovinSdk.initializeSdk(context);
        AudienceNetworkAds.initialize(context);

        interstitialAd = new MaxInterstitialAd(interstitialId, context);
        interstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {
                preferences = PreferenceManager.getDefaultSharedPreferences(context);
                if (preferences.getString("action", "").equals("")) {
                } else {
//                    preferences.edit().clear().apply();
                }
            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                    context.runOnUiThread(() -> showOwnInterstitialAds(context, pos));
                }
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                    context.runOnUiThread(() -> showOwnInterstitialAds(context, pos));
                }
            }
        });
        // Load the first ad
        interstitialAd.loadAd();
        if (interstitialAd.isReady()) {
            interstitialAd.showAd();
            Toast.makeText(context, "ads  ready", Toast.LENGTH_SHORT).show();

        }
        return interstitialAd;
    }

    public void ironSourceInterstitialAd(Activity context, String interstitialId) {
        IronSource.init(context, interstitialId);
        AudienceNetworkAds.initialize(context);
        IronSource.loadInterstitial();
        IronSource.setMetaData("Facebook_IS_CacheFlag", "IMAGE");
        IronSource.showInterstitial();
        IronSource.setInterstitialListener(new InterstitialListener() {
            @Override
            public void onInterstitialAdReady() {
                IronSource.showInterstitial();

            }

            @Override
            public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                Log.d(TAG, "checkAds: " + ironSourceError.getErrorMessage());

                int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                    context.runOnUiThread(() -> showOwnInterstitialAds(context, pos));
                }

            }

            @Override
            public void onInterstitialAdOpened() {
            }

            @Override
            public void onInterstitialAdClosed() {
                preferences = PreferenceManager.getDefaultSharedPreferences(context);
                if (preferences.getString("action", "").equals("")) {
                } else {
//                    preferences.edit().clear().apply();
                }
            }

            @Override
            public void onInterstitialAdShowSucceeded() {

            }

            @Override
            public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
//                showOwnInterstitialAds(context);
            }

            @Override
            public void onInterstitialAdClicked() {

            }
        });
    }

    public void metaInterstitialAd(Activity context, String interstitialId) {
        AudienceNetworkAds.initialize(context);
        com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(context, interstitialId);
        // Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
                preferences = PreferenceManager.getDefaultSharedPreferences(context);
                if (preferences.getString("action", "").equals("")) {
                } else {
//                    preferences.edit().clear().apply();
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                if (MyApp.ownAdsModels != null) {
                    int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                    if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                        context.runOnUiThread(() -> showOwnInterstitialAds(context, pos));
                    }
                }
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    //  Admob Native Ads
    public void loadAdmobNativeAd(final Activity context, final FrameLayout frameLayout) {
        refreshAd(context, frameLayout);
    }

    public void refreshAd(final Activity context, final FrameLayout frameLayout) {

        AdLoader.Builder builder = new AdLoader.Builder(context, Objects.requireNonNull(Paper.book().read(Prevalent.nativeAds)));

        builder.forNativeAd(nativeAd -> {


            if (nativeAds != null) {
                nativeAds.destroy();
            }
            nativeAds = nativeAd;
            NativeAdView adView = (NativeAdView) context.getLayoutInflater()
                    .inflate(R.layout.ad_unified, null);
            populateUnifiedNativeAdView(nativeAd, adView);
            frameLayout.removeAllViews();
            frameLayout.addView(adView);
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(false)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {

                    @Override
                    @Deprecated
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                        if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                            setOwnNativeAds(context, frameLayout, pos);
                        }
                    }
                })
                .build();
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        adLoader.loadAd(adRequest);

    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {


        com.google.android.gms.ads.nativead.MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);


        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));


        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());


        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView()))
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

    }

    public void IronSourceMRECBanner(Activity context, final FrameLayout container) {
        IronSource.getAdvertiserId(context);
        IronSource.shouldTrackNetworkState(context, true);
        AudienceNetworkAds.initialize(context);
        IronSource.init(context, Paper.book().read(Prevalent.nativeAds));
        IronSource.setMetaData("Facebook_IS_CacheFlag", "IMAGE");
        banner = IronSource.createBanner(context, ISBannerSize.RECTANGLE);
        container.addView(banner);

        if (banner != null) {
            // set the banner listener
            banner.setBannerListener(new BannerListener() {
                @Override
                public void onBannerAdLoaded() {
                    Log.d(TAG, "onBannerAdLoaded");
                    container.setVisibility(View.VISIBLE);
                    // since banner container was "gone" by default, we need to make it visible as soon as the banner is ready
                }

                @Override
                public void onBannerAdLoadFailed(IronSourceError error) {
                    Log.d(TAG, "onBannerAdLoadFailed" + " " + error);
                    context.runOnUiThread(container::removeAllViews);
                }

                @Override
                public void onBannerAdClicked() {
                    Log.d(TAG, "onBannerAdClicked");
                }

                @Override
                public void onBannerAdScreenPresented() {
                    Log.d(TAG, "onBannerAdScreenPresented");
                }

                @Override
                public void onBannerAdScreenDismissed() {
                    Log.d(TAG, "onBannerAdScreenDismissed");
                }

                @Override
                public void onBannerAdLeftApplication() {
                    Log.d(TAG, "onBannerAdLeftApplication");
                }
            });

            // load ad into the created banner
            IronSource.loadBanner(banner);
        } else {
            Toast.makeText(context, "IronSource.createBanner returned null", Toast.LENGTH_LONG).show();
        }
    }

    public void appLovinNativeAds(Activity context, FrameLayout frameLayout) {
        MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(Objects.requireNonNull(Paper.book().read(Prevalent.nativeAds)), context);
        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(@Nullable MaxNativeAdView maxNativeAdView, MaxAd maxAd) {
                // Cleanup any pre-existing native ad to prevent memory leaks.
                if (nativeAd != null) {
                    nativeAdLoader.destroy(nativeAd);
                }

                // Save ad for cleanup.
                nativeAd = maxAd;

                // Add ad view to view.
                frameLayout.removeAllViews();
                frameLayout.addView(maxNativeAdView);
                super.onNativeAdLoaded(maxNativeAdView, maxAd);

            }

            @Override
            public void onNativeAdLoadFailed(String s, MaxError maxError) {
                super.onNativeAdLoadFailed(s, maxError);
//                frameLayout.setVisibility(View.GONE);
                Log.e("ContentValue", maxError.getMessage());
                int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                    setOwnNativeAds(context, frameLayout, pos);
                }
            }

            @Override
            public void onNativeAdClicked(MaxAd maxAd) {
                super.onNativeAdClicked(maxAd);
            }
        });


        nativeAdLoader.loadAd();


    }

    public void metaNativeAds(Activity context, FrameLayout container) {
        AudienceNetworkAds.initialize(context);
        String id = Objects.requireNonNull(Paper.book().read(Prevalent.nativeAds));
        fbNativeAd = new com.facebook.ads.NativeAd(context, id);

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                    setOwnNativeAds(context, container, pos);
                }
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (fbNativeAd == null || fbNativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(fbNativeAd, container);
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        };

        // Request an ad
        fbNativeAd.loadAd(
                fbNativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }

    private void inflateAd(com.facebook.ads.NativeAd nativeAd, FrameLayout container) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        NativeAdLayout nativeAdLayout = new NativeAdLayout(container.getContext());
        container.addView(nativeAdLayout);
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.fb_ad_unified, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(container.getContext(), nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
//        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView, nativeAdIcon, clickableViews);
    }

    public void appLovinMREC(Activity context, FrameLayout container) {
        AudienceNetworkAds.initialize(context);
        AppLovinSdk.getInstance(context).setMediationProvider("max");
        AppLovinSdk.initializeSdk(context);
        maxAdView = new MaxAdView(Paper.book().read(Prevalent.nativeAds), MaxAdFormat.MREC, context);
        maxAdView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                    setOwnNativeAds(context, container, pos);
                }
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });

        int widthPx = AppLovinSdkUtils.dpToPx(context, 300);
        int heightPx = AppLovinSdkUtils.dpToPx(context, 250);

        maxAdView.setLayoutParams(new FrameLayout.LayoutParams(widthPx, heightPx));

        // Set background or background color for MRECs to be fully functional
        maxAdView.setLayoutParams(new FrameLayout.LayoutParams(widthPx, heightPx));
        maxAdView.setBackgroundColor(Color.WHITE);
        container.addView(maxAdView);
        // Load the ad
        maxAdView.loadAd();
    }

    public void metaMREC(Activity context, FrameLayout frameLayout) {
        AudienceNetworkAds.initialize(context);
        com.facebook.ads.AdView adView1 = new com.facebook.ads.AdView(context, Paper.book().read(Prevalent.nativeAds), com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250);
        frameLayout.addView(adView1);
        adView1.loadAd();
        new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                int pos = new Random().nextInt(MyApp.ownAdsModels.size());
                if (MyApp.ownAdsModels.get(pos).getAdsTurnOnOrOff().equals("true")) {
                    setOwnNativeAds(context, frameLayout, pos);
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
    }


    public void setOwnBannerAds(Activity context, RelativeLayout container, int pos) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        BannerAdsLayoutBinding bannerAdsLayoutBinding = BannerAdsLayoutBinding.inflate(context.getLayoutInflater());
        container.addView(bannerAdsLayoutBinding.getRoot());
        ImageView bannerImg = bannerAdsLayoutBinding.adImg;
        String banImage = MyApp.ownAdsModels.get(pos).getBannerImg();
        String banUrl = MyApp.ownAdsModels.get(pos).getBannerUrl();
        Log.d("ContentValue", FilenameUtils.getExtension(banImage));

        switch (FilenameUtils.getExtension(banImage)) {
            case "jpeg":
            case "jpg":
            case "png":
                new Handler(Looper.getMainLooper()).post(() -> Glide.with(context.getApplicationContext()).load(ApiWebServices.base_url + "own_ads_images/" + banImage).into(bannerImg));
                bannerAdsLayoutBinding.adImg.setOnClickListener(view -> {
                    openWebPage(banUrl, context);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, banImage);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, banUrl);
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Banner Ads");
                    mFirebaseAnalytics.logEvent("Clicked_On_Banner_Img", bundle);

                });

                break;
            case "gif":
                new Handler(Looper.getMainLooper()).post(() -> Glide.with(context.getApplicationContext()).asGif().load(ApiWebServices.base_url + "own_ads_images/" + banImage).into(bannerImg));
                bannerAdsLayoutBinding.adImg.setOnClickListener(view -> {
                    openWebPage(banUrl, context);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, banImage);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, banUrl);
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Banner Ads");
                    mFirebaseAnalytics.logEvent("Clicked_On_Banner_Gif", bundle);
                });

                break;
            case "mp4":
//                bannerAdsLayoutBinding.bannerView.setOnClickListener(view -> {
//                    openWebPage(banUrl, context);
//                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, banImage);
//                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, banUrl);
//                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Banner Ads");
//                    mFirebaseAnalytics.logEvent("Clicked_On_Banner_video", bundle);
//                });
//                bannerAdsLayoutBinding.bannerView.setVideoURI(Uri.parse(ApiWebServices.base_url + "own_ads_images/" + banImage));
//                bannerAdsLayoutBinding.bannerContainer.setVisibility(View.VISIBLE);
//                bannerAdsLayoutBinding.adImg.setVisibility(View.GONE);
//                bannerAdsLayoutBinding.bannerView.setVolume(0f);
//                bannerAdsLayoutBinding.bannerView.setKeepScreenOn(true);
//                bannerAdsLayoutBinding.bannerButton.setOnClickListener(v -> {
//                    if (bannerAdsLayoutBinding.bannerView.getVolume() > 0) {
//                        bannerAdsLayoutBinding.bannerView.setVolume(0f);
//                        bannerAdsLayoutBinding.bannerButton.setImageResource(R.drawable.ic_baseline_volume_off_24);
//                    } else {
//                        bannerAdsLayoutBinding.bannerView.setVolume(1.0f);
//                        bannerAdsLayoutBinding.bannerButton.setImageResource(R.drawable.ic_baseline_volume_up_24);
//                    }
//                });
//                bannerAdsLayoutBinding.bannerView.setOnPreparedListener(bannerAdsLayoutBinding.bannerView::start);
//                bannerAdsLayoutBinding.bannerView.setOnCompletionListener(bannerAdsLayoutBinding.bannerView::restart);

                break;
        }

//        if (context != null) {
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                }
//            }, 0, 5000);
//        }

    }

    public void setOwnNativeAds(Activity context, FrameLayout container, int pos) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();

        NativeAdsLayoutBinding nativeAdsLayoutBinding = NativeAdsLayoutBinding.inflate(context.getLayoutInflater());
        container.addView(nativeAdsLayoutBinding.getRoot());
        ImageView bannerImg = nativeAdsLayoutBinding.adImg;
        String nativeImg = MyApp.ownAdsModels.get(pos).getNativeImg();
        String nativeUrl = MyApp.ownAdsModels.get(pos).getNativeUrl();
        Log.d("ContentValue", FilenameUtils.getExtension(nativeImg));
        switch (FilenameUtils.getExtension(nativeImg)) {
            case "jpeg":
            case "jpg":
            case "png":
                new Handler(Looper.getMainLooper()).post(() -> Glide.with(context.getApplicationContext()).load(ApiWebServices.base_url + "own_ads_images/" + nativeImg).into(bannerImg));
                nativeAdsLayoutBinding.adImg.setOnClickListener(view -> {
                    openWebPage(nativeUrl, context);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, nativeImg);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, nativeUrl);
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Native Ads");
                    mFirebaseAnalytics.logEvent("Clicked_On_Native_Img", bundle);
                });
                break;
            case "gif":
                new Handler(Looper.getMainLooper()).post(() -> Glide.with(context.getApplicationContext()).asGif().load(ApiWebServices.base_url + "own_ads_images/" + nativeImg).into(bannerImg));
                nativeAdsLayoutBinding.adImg.setOnClickListener(view -> {
                    openWebPage(nativeUrl, context);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, nativeImg);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, nativeUrl);
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Native Ads");
                    mFirebaseAnalytics.logEvent("Clicked_On_Native_Gif", bundle);
                });
                break;
            case "mp4":
//                nativeAdsLayoutBinding.nativeView.setOnClickListener(view -> {
//                    openWebPage(nativeUrl, context);
//                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, nativeImg);
//                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, nativeUrl);
//                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Native Ads");
//                    mFirebaseAnalytics.logEvent("Clicked_On_Native_video", bundle);
//                });
//                nativeAdsLayoutBinding.nativeView.setVideoURI(Uri.parse(ApiWebServices.base_url + "own_ads_images/" + nativeImg));
//                nativeAdsLayoutBinding.nativeContainer.setVisibility(View.VISIBLE);
//                nativeAdsLayoutBinding.adImg.setVisibility(View.GONE);
//                nativeAdsLayoutBinding.nativeView.setVolume(0f);
//                nativeAdsLayoutBinding.nativeView.setKeepScreenOn(true);
//                nativeAdsLayoutBinding.nativeButton.setOnClickListener(v -> {
//                    if (nativeAdsLayoutBinding.nativeView.getVolume() > 0) {
//                        nativeAdsLayoutBinding.nativeView.setVolume(0f);
//                        nativeAdsLayoutBinding.nativeButton.setImageResource(R.drawable.ic_baseline_volume_off_24);
//                    } else {
//                        nativeAdsLayoutBinding.nativeView.setVolume(1.0f);
//                        nativeAdsLayoutBinding.nativeButton.setImageResource(R.drawable.ic_baseline_volume_up_24);
//                    }
//                });
//                nativeAdsLayoutBinding.nativeView.setOnPreparedListener(nativeAdsLayoutBinding.nativeView::start);
//                nativeAdsLayoutBinding.nativeView.setOnCompletionListener(nativeAdsLayoutBinding.nativeView::restart);

// show video
                break;
        }
//        if (context != null) {
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                }
//            }, 0, 5000);
//        }

//        Glide.with(this)
//                .asGif()
//                .load(urlGif)
//                .into(iv_gif)

//        import org.apache.commons.io.FilenameUtils;

    }

    public void showOwnInterstitialAds(Activity context, int pos) {
        final int[] counter = {10};
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        dialog = new Dialog(context);
        InterstitialAdsLayoutBinding binding = InterstitialAdsLayoutBinding.inflate(context.getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ad_item_bg));
        dialog.setCancelable(false);
        String interstitialImg = MyApp.ownAdsModels.get(pos).getInterstitialImg();
        String interUrl = MyApp.ownAdsModels.get(pos).getInterstitialUrl();
        switch (FilenameUtils.getExtension(interstitialImg)) {
            case "jpeg":
            case "jpg":
            case "png":
                new Handler(Looper.getMainLooper()).post(() -> Glide.with(context.getApplicationContext()).load(ApiWebServices.base_url + "own_ads_images/" + interstitialImg).into(binding.adImg));
                binding.onClick.setOnClickListener(view -> {
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, interstitialImg);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, interUrl);
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Interstitial Ads");
                    mFirebaseAnalytics.logEvent("Clicked_On_Interstitial_Img", bundle);
                    openWebPage(interUrl, context);
                });
                break;
            case "gif":
                new Handler(Looper.getMainLooper()).post(() -> Glide.with(context.getApplicationContext()).asGif().load(ApiWebServices.base_url + "own_ads_images/" + interstitialImg).into(binding.adImg));
                binding.onClick.setOnClickListener(view -> {
                    openWebPage(interUrl, context);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, interstitialImg);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, interUrl);
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Interstitial Ads");
                    mFirebaseAnalytics.logEvent("Clicked_On_Interstitial_gif", bundle);

                });
                break;
            case "mp4":
//                binding.onClick.setOnClickListener(view -> {
//                    openWebPage(interUrl, context);
//                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, interstitialImg);
//                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, interUrl);
//                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Interstitial Ads");
//                    mFirebaseAnalytics.logEvent("Clicked_On_Interstitial_video", bundle);
//
//                });
//                binding.videoView.setVideoURI(Uri.parse(ApiWebServices.base_url + "own_ads_images/" + interstitialImg));
//                binding.videoContainer.setVisibility(View.VISIBLE);
//                binding.adImg.setVisibility(View.GONE);
//                binding.videoView.setVolume(0f);
//                binding.videoView.setKeepScreenOn(true);
//                binding.videoButton.setOnClickListener(v -> {
//                    if (binding.videoView.getVolume() > 0) {
//                        binding.videoView.setVolume(0f);
//                        binding.videoButton.setImageResource(R.drawable.ic_baseline_volume_off_24);
//                    } else {
//                        binding.videoView.setVolume(1.0f);
//                        binding.videoButton.setImageResource(R.drawable.ic_baseline_volume_up_24);
//                    }
//                });
//
//                binding.videoView.setOnPreparedListener(binding.videoView::start);
//                binding.videoView.setOnCompletionListener(binding.videoView::restart);
//

                break;
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            dialog.show();
            new Handler(Looper.getMainLooper()).post(() -> new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                    binding.countDown.setText(String.valueOf(counter[0]));
                    counter[0]--;
                }

                public void onFinish() {
                    binding.countDown.setVisibility(View.GONE);
                    binding.cancelAd.setVisibility(View.VISIBLE);
                }
            }.start());

        }, 3000);
        dialog.findViewById(R.id.cancel_ad).setOnClickListener(view -> {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getString("action", "").equals("")) {
            } else {
                preferences.edit().clear().apply();
            }
            dialog.dismiss();
//            binding.videoView.pause();
        });


    }

    @SuppressLint("QueryPermissionsNeeded")
    public void openWebPage(String url, Activity context) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
