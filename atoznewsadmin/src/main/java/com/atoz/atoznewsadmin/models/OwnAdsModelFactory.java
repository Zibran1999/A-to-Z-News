package com.atoz.atoznewsadmin.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class OwnAdsModelFactory implements ViewModelProvider.Factory {

    Application application;
    String appId;

    public OwnAdsModelFactory(Application application, String appId) {
        this.application = application;
        this.appId = appId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new OwnAdsModelView(application, appId);
    }
}
