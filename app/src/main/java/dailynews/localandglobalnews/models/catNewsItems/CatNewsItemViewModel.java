package dailynews.localandglobalnews.models.catNewsItems;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.utils.Repository;

public class CatNewsItemViewModel extends AndroidViewModel {
    private final Repository repository;
    String id;

    public CatNewsItemViewModel(@NonNull Application application, String id) {
        super(application);
        repository = Repository.getInstance();
        this.id = id;
    }

    public LiveData<List<NewsModel>> getCatNewsItems(){
        Log.d("contentValueIdM", id != null ? id : "null");

        return repository.getCatNewsItemMutableLiveData(id);
    }
}
