package com.atoz.atoznewsadmin.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnAdsRepository {

    private static ApiInterface apiInterface;
    private static OwnAdsRepository ownAdsRepository;

    private final MutableLiveData<List<OwnAdsModel>> ownAdsViewModelLiveData = new MutableLiveData<>();

    public OwnAdsRepository() {
        apiInterface = ApiWebServices.getApiInterface();
    }

    public static OwnAdsRepository getInstance() {
        if (ownAdsRepository == null) {
            ownAdsRepository = new OwnAdsRepository();
        }
        return ownAdsRepository;
    }


    public LiveData<List<OwnAdsModel>> fetchOwnAds(String appId) {
        Call<List<OwnAdsModel>> call = apiInterface.fetchOwnAds(appId);
        call.enqueue(new Callback<List<OwnAdsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<OwnAdsModel>> call, @NonNull Response<List<OwnAdsModel>> response) {
                if (response.isSuccessful()) {
                    ownAdsViewModelLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OwnAdsModel>> call, @NonNull Throwable t) {
                Log.d("onResponse error", t.getMessage());
            }
        });
        return ownAdsViewModelLiveData;


    }


}
