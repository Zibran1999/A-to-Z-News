package dailynews.localandglobalnews.models.BreakingNews;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class NewsModelFactory implements ViewModelProvider.Factory {
    Application application;
    String id;

    public NewsModelFactory(Application application, String id) {
        this.application = application;
        this.id = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new NewsViewModel(application,id);
    }
}
