package dailynews.localandglobalnews.utils;

import android.app.Application;
import android.content.Intent;

import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import dailynews.localandglobalnews.activities.HomeActivity;


public class MyApp extends Application {
    //3f96e32e-d1b4-4fe7-9a45-4cd0a7e6f5a1
    // df18d6f6-3528-4548-8851-1adf86bf5117
    private static final String ONESIGNAL_APP_ID = "df18d6f6-3528-4548-8851-1adf86bf5117";
    public static MyApp mInstance;
    // ApiInterface apiInterface;

    public MyApp() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //  Paper.init(mInstance);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.setNotificationOpenedHandler(new ExampleNotificationOpenedHandler());
        OneSignal.setAppId(ONESIGNAL_APP_ID);
//        apiInterface = ApiWebServices.getApiInterface();
        //  fetchAds();
    }

//    private void fetchAds() {
//        apiInterface = ApiWebServices.getApiInterface();
//        Call<List<AdsModel>> call = apiInterface.fetchAds("Product Review");
//        call.enqueue(new Callback<List<AdsModel>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<AdsModel>> call, @NonNull Response<List<AdsModel>> response) {
//                if (response.isSuccessful()) {
//                    if (response.body() != null) {
//                        for (AdsModel ads : response.body()) {
//                            Log.d("checkIds",
//                                    ads.getId()
//                                            + "\n" + ads.getAppId()
//                                            + "\n" + ads.getAppLovinAppKey()
//                                            + "\n" + ads.getBannerTop()
//                                            + "\n" + ads.getBannerTopAdNetwork()
//                                            + "\n" + ads.getBannerBottom()
//                                            + "\n" + ads.getBannerBottomAdNetwork()
//                                            + "\n" + ads.getInterstitial()
//                                            + "\n" + ads.getInterstitalAdNetwork()
//                                            + "\n" + ads.getNativeAd()
//                                            + "\n" + ads.getNativeAdNetwork()
//                                            + "\n" + ads.getNativeType()
//                                            + "\n" + ads.getRewardAd()
//                                            + "\n" + ads.getRewardAdNetwork()
//                            );
//
//                            Paper.book().write(Prevalent.id, ads.getId());
//                            Paper.book().write(Prevalent.appId, ads.getAppId());
//                            Paper.book().write(Prevalent.appLovinId, ads.getAppLovinAppKey());
//                            Paper.book().write(Prevalent.bannerTop, ads.getBannerTop());
//                            Paper.book().write(Prevalent.bannerTopNetworkName, ads.getBannerTopAdNetwork());
//                            Paper.book().write(Prevalent.bannerBottom, ads.getBannerBottom());
//                            Paper.book().write(Prevalent.bannerBottomNetworkName, ads.getBannerBottomAdNetwork());
//                            Paper.book().write(Prevalent.interstitialAds, ads.getInterstitial());
//                            Paper.book().write(Prevalent.interstitialNetwork, ads.getInterstitalAdNetwork());
//                            Paper.book().write(Prevalent.nativeAds, ads.getNativeAd());
//                            Paper.book().write(Prevalent.nativeAdsNetworkName, ads.getNativeAdNetwork());
//                            Paper.book().write(Prevalent.nativeAdsType, ads.getNativeType());
//                            Paper.book().write(Prevalent.rewardAds, ads.getRewardAd());
//                            Paper.book().write(Prevalent.rewardAdsNetwork, ads.getRewardAdNetwork());
//
//                        }
//
//                        // setting application keys of admob
//                        try {
//                            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
//                            Bundle bundle = ai.metaData;
//                            String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
//                            Log.d(TAG, "Name Found: " + myApiKey);
//                            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", Paper.book().read(Prevalent.appId));//you can replace your key APPLICATION_ID here
//                            String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
//                            Log.d(TAG, "ReNamed Found: " + ApiKey);
//                        } catch (PackageManager.NameNotFoundException e) {
//                            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
//                        } catch (NullPointerException e) {
//                            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
//                        }
//
//                        // setting application keys of applovin
//                        try {
//                            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
//                            Bundle bundle = ai.metaData;
//                            String myApiKey = bundle.getString("applovin.sdk.key");
//                            Log.d(TAG, "Name Found: " + myApiKey);
//                            ai.metaData.putString("applovin.sdk.key", Paper.book().read(Prevalent.appLovinId));     //you can replace your key APPLICATION_ID here
//                            String ApiKey = bundle.getString("applovin.sdk.key");
//                            Log.d(TAG, "ReNamed Found: " + ApiKey);
//                        } catch (PackageManager.NameNotFoundException e) {
//                            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
//                        } catch (NullPointerException e) {
//                            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
//                        }
//                    }
//                } else {
//                    Log.e("adsError", response.message());
//                }
//
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<AdsModel>> call, @NonNull Throwable t) {
//                Log.d("adsError", t.getMessage());
//            }
//        });
//
//
//    }

    private class ExampleNotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {
            Intent intent = new Intent(MyApp.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}