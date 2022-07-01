package dailynews.localandglobalnews.utils;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.window.SplashScreen;

import androidx.annotation.NonNull;

import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dailynews.localandglobalnews.activities.HomeActivity;
import dailynews.localandglobalnews.activities.MainActivity;
import dailynews.localandglobalnews.models.AToZNewsAdsModel;
import dailynews.localandglobalnews.models.OwnAdsModel;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyApp extends Application {
    //3f96e32e-d1b4-4fe7-9a45-4cd0a7e6f5a1
    // df18d6f6-3528-4548-8851-1adf86bf5117
    private static final String ONESIGNAL_APP_ID = "df18d6f6-3528-4548-8851-1adf86bf5117";
    public static MyApp mInstance;
    public static List<OwnAdsModel> ownAdsModels;
    ApiInterface apiInterface;
    SharedPreferences.Editor editor;

    public MyApp() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Paper.init(mInstance);
        editor = PreferenceManager.getDefaultSharedPreferences(mInstance).edit();

        apiInterface = ApiWebServices.getApiInterface();
        // OneSignal Initialization
        OneSignal.initWithContext(this);

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.setNotificationOpenedHandler(new ExampleNotificationOpenedHandler());
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        fetchAds();
        fetchOwnAds();
    }


    private void fetchOwnAds() {
        ownAdsModels = new ArrayList<>();
        Call<List<OwnAdsModel>> call = apiInterface.fetchOwnAds("A To Z News");
        call.enqueue(new Callback<List<OwnAdsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<OwnAdsModel>> call, @NonNull Response<List<OwnAdsModel>> response) {
                if (response.isSuccessful()) {
                    if (!Objects.requireNonNull(response.body()).isEmpty()) {
                        ownAdsModels.addAll(response.body());
                        Log.d("contentValue", ownAdsModels.get(0).getBannerImg());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OwnAdsModel>> call, @NonNull Throwable t) {
                Log.d("onResponse error", t.getMessage());
            }
        });


    }


    private void fetchAds() {

        Call<List<AToZNewsAdsModel>> call = apiInterface.aTozNewsAdsFetch("A to Z News");
        call.enqueue(new Callback<List<AToZNewsAdsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<AToZNewsAdsModel>> call, @NonNull Response<List<AToZNewsAdsModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        for (AToZNewsAdsModel ads : response.body()) {
                            Log.d("checkIds",
                                    ads.getId()
                                            + "\n" + ads.getAppId()
                                            + "\n" + ads.getAppLovinAppKey()
                                            + "\n" + ads.getBannerTop()
                                            + "\n" + ads.getBannerTopAdNetwork()
                                            + "\n" + ads.getBannerBottom()
                                            + "\n" + ads.getBannerBottomAdNetwork()
                                            + "\n" + ads.getInterstitial()
                                            + "\n" + ads.getInterstitalAdNetwork()
                                            + "\n" + ads.getNativeAd()
                                            + "\n" + ads.getNativeAdNetwork()
                                            + "\n" + ads.getNativeType()
                                            + "\n" + ads.getAppOpenAd()
                                            + "\n" + ads.getAppOpenAdNetwork()
                            );

                            Paper.book().write(Prevalent.id, ads.getId());
                            Paper.book().write(Prevalent.appId, ads.getAppId());
                            Paper.book().write(Prevalent.appLovinId, ads.getAppLovinAppKey());
                            Paper.book().write(Prevalent.bannerTop, ads.getBannerTop());
                            Paper.book().write(Prevalent.bannerTopNetworkName, ads.getBannerTopAdNetwork());
                            Paper.book().write(Prevalent.bannerBottom, ads.getBannerBottom());
                            Paper.book().write(Prevalent.bannerBottomNetworkName, ads.getBannerBottomAdNetwork());
                            Paper.book().write(Prevalent.interstitialAds, ads.getInterstitial());
                            Paper.book().write(Prevalent.interstitialNetwork, ads.getInterstitalAdNetwork());
                            Paper.book().write(Prevalent.nativeAds, ads.getNativeAd());
                            Paper.book().write(Prevalent.nativeAdsNetworkName, ads.getNativeAdNetwork());
                            Paper.book().write(Prevalent.nativeAdsType, ads.getNativeType());
                            Paper.book().write(Prevalent.rewardAds, ads.getAppOpenAd());
                            Paper.book().write(Prevalent.rewardAdsNetwork, ads.getAppOpenAdNetwork());

                        }

                        try {
                            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                            Bundle bundle = ai.metaData;
                            String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                            Log.d(TAG, "Name Found: " + myApiKey);
                            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", Paper.book().read(Prevalent.appId));//you can replace your key APPLICATION_ID here
                            String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                            Log.d(TAG, "ReNamed Found: " + ApiKey);
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
                        } catch (NullPointerException e) {
                            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
                        }

                        try {
                            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                            Bundle bundle = ai.metaData;
                            String myApiKey = bundle.getString("applovin.sdk.key");
                            Log.d(TAG, "Name Found: " + myApiKey);
                            ai.metaData.putString("applovin.sdk.key", Paper.book().read(Prevalent.appLovinId));     //you can replace your key APPLICATION_ID here
                            String ApiKey = bundle.getString("applovin.sdk.key");
                            Log.d(TAG, "ReNamed Found: " + ApiKey);
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
                        } catch (NullPointerException e) {
                            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
                        }
                    }
                } else {
                    Log.e("adsError", response.message());
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<AToZNewsAdsModel>> call, @NonNull Throwable t) {
                Log.d("adsError", t.getMessage());
            }
        });


    }


    private class ExampleNotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {
            JSONObject data = result.getNotification().getAdditionalData();
            String activityToBeOpened, imgPos, cat_pos, cat_item_pos;

            if (data != null) {
                activityToBeOpened = data.optString("action", null);
                imgPos = data.optString("pos", null);
                cat_pos = data.optString("cat_pos", null);
                cat_item_pos = data.optString("cat_item_pos", null);
                editor.putString("pos", imgPos);
                editor.putString("action", activityToBeOpened);
                editor.putString("cat_pos", cat_pos);
                editor.putString("cat_item_pos", cat_item_pos);
                editor.apply();
                switch (activityToBeOpened) {
                    case "home":
                    case "cat":
                    case "bra":
                    case "tre":
                    case "gad": {
                        Intent intent = new Intent(MyApp.this, HomeActivity.class);
                        intent.putExtra("action", activityToBeOpened);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        break;
                    }

                }
            } else {
                Intent intent = new Intent(MyApp.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

}