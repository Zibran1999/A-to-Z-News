package dailynews.localandglobalnews.models.catNewsItems;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dailynews.localandglobalnews.models.TrendingNews.TrendingNewsViewModel;

public class CatNewsItemModelFactory implements ViewModelProvider.Factory {
    Application application;
    String id;

    public CatNewsItemModelFactory(Application application, String id) {
        this.application = application;
        this.id = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new CatNewsItemViewModel(application,id);
    }
}
