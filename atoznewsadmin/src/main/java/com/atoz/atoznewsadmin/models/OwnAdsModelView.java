package com.atoz.atoznewsadmin.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class OwnAdsModelView extends AndroidViewModel {
    private final OwnAdsRepository ownAdsRepository;
    String appId;

    public OwnAdsModelView(@NonNull Application application, String appId) {
        super(application);
        ownAdsRepository = OwnAdsRepository.getInstance();
        this.appId = appId;

    }

    public LiveData<List<OwnAdsModel>> getOwnAds() {
        return ownAdsRepository.fetchOwnAds(appId);
    }
}
