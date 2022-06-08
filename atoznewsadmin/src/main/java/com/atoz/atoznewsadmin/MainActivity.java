package com.atoz.atoznewsadmin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.atoz.atoznewsadmin.databinding.ActivityMainBinding;
import com.atoz.atoznewsadmin.databinding.AdsUpdateLayoutBinding;
import com.atoz.atoznewsadmin.databinding.UploadNewsBinding;
import com.atoz.atoznewsadmin.models.AdsModel;
import com.atoz.atoznewsadmin.models.ApiInterface;
import com.atoz.atoznewsadmin.models.ApiWebServices;
import com.atoz.atoznewsadmin.models.MessageModel;
import com.atoz.atoznewsadmin.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    UploadNewsBinding uploadNewsBinding;
    Dialog dialog, loadingDialog;
    ActivityResultLauncher<String> launcher;
    String encodedImg, formattedDate, selectTime;
    Map<String, String> map = new HashMap<>();

    Call<MessageModel> call;
    ApiInterface apiInterface;


    String[] items = new String[]{"AdmobWithMeta", "IronSourceWithMeta", "AppLovinWithMeta", "Meta"};
    String[] item2 = new String[]{"Native", "MREC"};
    AutoCompleteTextView BannerTopNetworkName, BannerBottomNetworkName, InterstitialNetwork, NativeAdsNetworkName, RewardAdsNetwork, nativeType;
    EditText AppId, AppLovinSdkKey, BannerTop, BannerBottom, InterstitialAds, NativeAds, rewardAds;
    Button UploadAdsBtn;
    Dialog loading, adsUpdateDialog;
    String appId, appLovinSdkKey, bannerTopNetworkName, bannerTop, bannerBottomNetworkName,
            bannerBottom, interstitialNetwork, interstitialAds, nativeAdsNetworkName,
            nativeAds, nativeAdsType, rewardAd, rewardAdsNetwork;

    AdsUpdateLayoutBinding adsUpdateLayoutBinding;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadingDialog = Utils.loadingDialog(this);
        apiInterface = ApiWebServices.getApiInterface();
        binding.uploadNews.setOnClickListener(view -> setUploadNewsDialog("upload News"));
        binding.uploadCategory.setOnClickListener(view -> setUploadNewsDialog("Upload Category"));
        binding.uploadAds.setOnClickListener(view -> showUpdateAdsDialog());
        binding.showCat.setOnClickListener(view -> {
            Intent intent = new Intent(this, ShowCategory.class);
            startActivity(intent);
        });
        binding.showBreaking.setOnClickListener(view -> {
            Intent intent = new Intent(this, ShowNewsActivity.class);
            intent.putExtra("tableName", "breaking_news");
            startActivity(intent);
        });
        binding.showTrending.setOnClickListener(view -> {
            Intent intent = new Intent(this, ShowNewsActivity.class);
            intent.putExtra("tableName", "trending_news");
            startActivity(intent);
        });
        binding.showGadgets.setOnClickListener(view -> {
            Intent intent = new Intent(this, ShowNewsActivity.class);
            intent.putExtra("tableName", "gadgets_news");
            startActivity(intent);
        });

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {

            if (result != null) {
                try {
                    final InputStream inputStream = getContentResolver().openInputStream(result);
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    uploadNewsBinding.choseNewsImg.setImageBitmap(bitmap);
                    encodedImg = imageStore(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            }


        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUploadNewsDialog(String id) {
        dialog = new Dialog(this);
        uploadNewsBinding = UploadNewsBinding.inflate(getLayoutInflater());
        dialog.setContentView(uploadNewsBinding.getRoot());
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.item_bg));
        dialog.setCancelable(false);
        dialog.show();
        uploadNewsBinding.cancel.setOnClickListener(view -> dialog.dismiss());

        if (id.equals("Upload Category")) {
            uploadNewsBinding.newsTV.setText(id);
            uploadNewsBinding.radioGroup.setVisibility(View.GONE);
            uploadNewsBinding.textInputLayout.setVisibility(View.GONE);
            uploadNewsBinding.textInputLayout2.setVisibility(View.GONE);
        } else
            uploadNewsBinding.newsTV.setText(id);

        uploadNewsBinding.choseNewsImg.setOnClickListener(view -> launcher.launch("image/*"));

        uploadNewsBinding.upload.setOnClickListener(view -> {

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            formattedDate = df.format(c);
            SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String stime = mdformat.format(c);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTimeFormate = new SimpleDateFormat("HH:mm");
            Date currentDate = null;
            try {
                currentDate = currentTimeFormate.parse(stime);
            } catch (ParseException e) {

                e.printStackTrace();
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat CurrentfmtOut = new SimpleDateFormat("hh:mm aa");

            assert currentDate != null;
            selectTime = CurrentfmtOut.format(currentDate);

            String title = uploadNewsBinding.titleTv.getText().toString().trim();
            if (!id.equals("Upload Category")) {
                String url = uploadNewsBinding.titleTv.getText().toString().trim();
                String desc = uploadNewsBinding.titleTv.getText().toString().trim();
                boolean breakingNews = uploadNewsBinding.breakingNews.isChecked();
                boolean trendingNews = uploadNewsBinding.trendingNews.isChecked();
                boolean gadgetNews = uploadNewsBinding.gadgets.isChecked();
                if (encodedImg == null) {
                    loadingDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(title)) {
                    uploadNewsBinding.titleTv.setError("title Required");
                    uploadNewsBinding.titleTv.requestFocus();
                    loadingDialog.dismiss();
                } else if (TextUtils.isEmpty(url)) {
                    uploadNewsBinding.url.setError("Url Required");
                    uploadNewsBinding.url.requestFocus();
                    loadingDialog.dismiss();
                } else if (TextUtils.isEmpty(desc)) {
                    uploadNewsBinding.desc.setError("desc Required");
                    uploadNewsBinding.desc.requestFocus();
                    loadingDialog.dismiss();
                } else {
                    if (trendingNews) {
                        map.put("tableName", "trending_news");
                    } else if (breakingNews) {
                        map.put("tableName", "breaking_news");
                    } else if (gadgetNews) {
                        map.put("tableName", "gadget_news");
                    }
                    map.put("img", encodedImg);
                    map.put("title", title);
                    map.put("url", url);
                    map.put("desc", desc);
                    map.put("date", formattedDate);
                    map.put("time", selectTime);

                    call = apiInterface.uploadNews(map);
                    uploadData(call, dialog);

                }
            } else {
                if (encodedImg == null) {
                    loadingDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(title)) {
                    uploadNewsBinding.titleTv.setError("title Required");
                    uploadNewsBinding.titleTv.requestFocus();
                    loadingDialog.dismiss();
                } else {
                    map.put("img", encodedImg);
                    map.put("title", title);
                    map.put("date", formattedDate);
                    map.put("time", selectTime);
                    call = apiInterface.uploadNewsCategory(map);
                    uploadData(call, dialog);

                }
            }


        });


    }

    private void uploadData(Call<MessageModel> call, Dialog dialog) {
        loadingDialog.show();
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {

                Log.d("responseError", t.getMessage());
                loadingDialog.dismiss();
            }
        });
    }

    private void showUpdateAdsDialog() {

        adsUpdateDialog = new Dialog(this);
        adsUpdateLayoutBinding = AdsUpdateLayoutBinding.inflate(getLayoutInflater());
        adsUpdateDialog.setContentView(adsUpdateLayoutBinding.getRoot());
        adsUpdateDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        adsUpdateDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.item_bg));
        adsUpdateDialog.setCancelable(false);
        adsUpdateDialog.show();


        adsUpdateLayoutBinding.cancelId.setOnClickListener(v -> adsUpdateDialog.dismiss());
        BannerTopNetworkName = adsUpdateLayoutBinding.bannerTopNetworkName;
        BannerBottomNetworkName = adsUpdateLayoutBinding.bannerBottomNetworkName;
        InterstitialNetwork = adsUpdateLayoutBinding.interstitialNetwork;
        NativeAdsNetworkName = adsUpdateLayoutBinding.nativeAdsNetworkName;
        RewardAdsNetwork = adsUpdateLayoutBinding.rewardAdsNetwork;
        UploadAdsBtn = adsUpdateLayoutBinding.uploadIds;

        AppId = adsUpdateLayoutBinding.appId;
        AppLovinSdkKey = adsUpdateLayoutBinding.appLovinSdkKey;
        BannerTop = adsUpdateLayoutBinding.bannerTop;
        BannerBottom = adsUpdateLayoutBinding.bannerBottom;
        InterstitialAds = adsUpdateLayoutBinding.interstitialAds;
        NativeAds = adsUpdateLayoutBinding.nativeAds;
        nativeType = adsUpdateLayoutBinding.nativeAdsType;
        rewardAds = adsUpdateLayoutBinding.rewardAds;

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, items);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(MainActivity.this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, item2);
        nativeType.setAdapter(arrayAdapter2);
        BannerTopNetworkName.setAdapter(arrayAdapter);
        BannerBottomNetworkName.setAdapter(arrayAdapter);
        InterstitialNetwork.setAdapter(arrayAdapter);
        NativeAdsNetworkName.setAdapter(arrayAdapter);
        RewardAdsNetwork.setAdapter(arrayAdapter);

        apiInterface = ApiWebServices.getApiInterface();
        Call<List<AdsModel>> calls = apiInterface.fetchAds("A to Z News");
        calls.enqueue(new Callback<List<AdsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdsModel>> call, @NonNull Response<List<AdsModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        for (AdsModel ads : response.body()) {
                            AppId.setText(ads.getAppId());
                            AppLovinSdkKey.setText(ads.getAppLovinAppKey());
                            BannerTopNetworkName.setText(ads.getBannerTopAdNetwork());
                            BannerTop.setText(ads.getBannerTop());
                            BannerBottomNetworkName.setText(ads.getBannerBottomAdNetwork());
                            BannerBottom.setText(ads.getBannerBottom());
                            InterstitialNetwork.setText(ads.getInterstitalAdNetwork());
                            InterstitialAds.setText(ads.getInterstitial());
                            NativeAdsNetworkName.setText(ads.getNativeAdNetwork());
                            NativeAds.setText(ads.getNativeAd());
                            nativeType.setText(ads.getNativeType());
                            RewardAdsNetwork.setText(ads.getAppOpenAdNetwork());
                            rewardAds.setText(ads.getAppOpenAd());

                        }
                    }
                } else {
                    Log.e("adsError", response.message());
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<AdsModel>> call, @NonNull Throwable t) {
                Log.d("adsError", t.getMessage());
            }
        });


        UploadAdsBtn.setOnClickListener(view -> {
            appId = AppId.getText().toString().trim();
            appLovinSdkKey = AppLovinSdkKey.getText().toString().trim();
            bannerTopNetworkName = BannerTopNetworkName.getText().toString().trim();
            bannerTop = BannerTop.getText().toString().trim();
            bannerBottomNetworkName = BannerBottomNetworkName.getText().toString().trim();
            bannerBottom = BannerBottom.getText().toString().trim();
            interstitialNetwork = InterstitialNetwork.getText().toString().trim();
            interstitialAds = InterstitialAds.getText().toString().trim();
            nativeAdsNetworkName = NativeAdsNetworkName.getText().toString().trim();
            nativeAds = NativeAds.getText().toString().trim();
            nativeAdsType = nativeType.getText().toString().trim();
            rewardAdsNetwork = RewardAdsNetwork.getText().toString().trim();
            rewardAd = rewardAds.getText().toString().trim();

            if (TextUtils.isEmpty(appId)) {
                AppId.setError("App id is required");
                AppId.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(appLovinSdkKey)) {
                AppLovinSdkKey.setError("AppLovinSdkKey is required");
                AppLovinSdkKey.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(bannerTopNetworkName)) {
                BannerTopNetworkName.setError("BannerTopNetworkName is required");
                BannerTopNetworkName.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(bannerTop)) {
                BannerTop.setError("BannerTop is required");
                BannerTop.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(bannerBottomNetworkName)) {
                BannerBottomNetworkName.setError("BannerBottomNetworkName is required");
                BannerBottomNetworkName.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(bannerBottom)) {
                BannerBottom.setError("BannerBottom is required");
                BannerBottom.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(interstitialNetwork)) {
                InterstitialNetwork.setError("InterstitialNetwork is required");
                InterstitialNetwork.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(interstitialAds)) {
                InterstitialAds.setError("InterstitialAds is required");
                InterstitialAds.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(nativeAdsNetworkName)) {
                NativeAdsNetworkName.setError("NativeAdsNetworkName is required");
                NativeAdsNetworkName.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(nativeAds)) {
                NativeAds.setError("NativeAds is required");
                NativeAds.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(nativeAdsType)) {
                nativeType.setError("NativeType is required");
                nativeType.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(rewardAdsNetwork)) {
                RewardAdsNetwork.setError("rewardAdsNetwork is required");
                RewardAdsNetwork.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(rewardAd)) {
                rewardAds.setError("rewardAd is required");
                rewardAds.requestFocus();
                loading.dismiss();
            } else {
                loadingDialog.show();
                map.put("id", "A to Z News");
                map.put("appId", appId);
                map.put("appLovinSdkKey", appLovinSdkKey);
                map.put("bannerTop", bannerTop);
                map.put("bannerTopNetworkName", bannerTopNetworkName);
                map.put("bannerBottom", bannerBottom);
                map.put("bannerBottomNetworkName", bannerBottomNetworkName);
                map.put("interstitialAds", interstitialAds);
                map.put("interstitialNetwork", interstitialNetwork);
                map.put("nativeAds", nativeAds);
                map.put("nativeAdsNetworkName", nativeAdsNetworkName);
                map.put("nativeType", nativeAdsType);
                map.put("appOpenAd", rewardAd);
                map.put("appOpenAdNetwork", rewardAdsNetwork);
                call = apiInterface.updateAdIds(map);
                uploadData(call, adsUpdateDialog);
            }

        });

    }


    public String imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);

        byte[] imageBytes = stream.toByteArray();
        return android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}