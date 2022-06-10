package dailynews.localandglobalnews.models.others;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.utils.Repository;

public class OtherNewsViewModel extends AndroidViewModel {
    private final Repository repository;
    String id;

    public OtherNewsViewModel(@NonNull Application application, String id) {
        super(application);
        repository = Repository.getInstance();
        this.id = id;
    }

    public LiveData<List<NewsModel>> getAllOtherNews(){
        return repository.getOtherNewsMutableLiveData(id);
    }
}
