package dailynews.localandglobalnews.models.BreakingNews;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import dailynews.localandglobalnews.utils.Repository;

public class NewsViewModel  extends AndroidViewModel {
    private final Repository repository;
    String id;

    public NewsViewModel(@NonNull Application application, String id) {
        super(application);
        repository = Repository.getInstance();
        this.id = id;
    }

    public LiveData<List<NewsModel>> getAllNews(){
        return repository.getNewsModelMutableLiveData(id);
    }
}
