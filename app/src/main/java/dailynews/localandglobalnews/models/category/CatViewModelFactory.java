package dailynews.localandglobalnews.models.category;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dailynews.localandglobalnews.models.BreakingNews.NewsViewModel;

public class CatViewModelFactory implements ViewModelProvider.Factory {
    Application application;
    String id;

    public CatViewModelFactory(Application application, String id) {
        this.application = application;
        this.id = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new CatViewModel(application,id);
    }
}
