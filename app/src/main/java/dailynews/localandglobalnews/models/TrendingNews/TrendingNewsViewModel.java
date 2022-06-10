package dailynews.localandglobalnews.models.TrendingNews;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.utils.Repository;

public class TrendingNewsViewModel extends AndroidViewModel {
    private final Repository repository;
    String id;

    public TrendingNewsViewModel(@NonNull Application application, String id) {
        super(application);
        repository = Repository.getInstance();
        this.id = id;
    }

    public LiveData<List<NewsModel>> getAllNews(){
        return repository.getTrendingNewsModelMutableLiveData(id);
    }
}
