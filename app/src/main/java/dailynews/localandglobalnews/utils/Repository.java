package dailynews.localandglobalnews.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.models.QuizModelList;
import dailynews.localandglobalnews.models.category.CatModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    public static Repository repository;
    private final MutableLiveData<QuizModelList> quizModelListMutableLiveData = new MutableLiveData<>();
    ApiInterface apiInterface;
    MutableLiveData<List<NewsModel>> breakingNewsModelMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<NewsModel>> trendingNewsModelMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<CatModel>> catModelMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<CatModel>> quizCatModelMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<NewsModel>> otherNewsMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<NewsModel>> catNewsItemMutableLiveData = new MutableLiveData<>();

    public Repository() {
        apiInterface = ApiWebServices.getApiInterface();
    }

    public static Repository getInstance() {
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }

    public MutableLiveData<List<NewsModel>> getNewsModelMutableLiveData(String id) {
        Call<List<NewsModel>> call = apiInterface.fetchOtherNews(id);
        call.enqueue(new Callback<List<NewsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<NewsModel>> call, @NonNull Response<List<NewsModel>> response) {
                if (response.isSuccessful()) {
                    breakingNewsModelMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NewsModel>> call, @NonNull Throwable t) {

            }
        });
        return breakingNewsModelMutableLiveData;
    }

    public MutableLiveData<List<NewsModel>> getTrendingNewsModelMutableLiveData(String id) {
        Call<List<NewsModel>> call = apiInterface.fetchOtherNews(id);
        call.enqueue(new Callback<List<NewsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<NewsModel>> call, @NonNull Response<List<NewsModel>> response) {
                if (response.isSuccessful()) {
                    trendingNewsModelMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NewsModel>> call, @NonNull Throwable t) {

            }
        });
        return trendingNewsModelMutableLiveData;
    }

    public MutableLiveData<List<CatModel>> getCatModelMutableLiveData(String id) {
        Call<List<CatModel>> call = apiInterface.fetchCategory(id);
        call.enqueue(new Callback<List<CatModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<CatModel>> call, @NonNull Response<List<CatModel>> response) {
                if (response.isSuccessful()) {
                    catModelMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CatModel>> call, @NonNull Throwable t) {

            }
        });
        return catModelMutableLiveData;
    }

    public MutableLiveData<List<CatModel>> getQuizCatModelMutableLiveData() {
        Call<List<CatModel>> call = apiInterface.fetchQuizCategory();
        call.enqueue(new Callback<List<CatModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<CatModel>> call, @NonNull Response<List<CatModel>> response) {
                if (response.isSuccessful()) {
                    quizCatModelMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CatModel>> call, @NonNull Throwable t) {

            }
        });
        return quizCatModelMutableLiveData;
    }

    public MutableLiveData<List<NewsModel>> getOtherNewsMutableLiveData(String id) {
        Call<List<NewsModel>> call = apiInterface.fetchOtherNews(id);
        call.enqueue(new Callback<List<NewsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<NewsModel>> call, @NonNull Response<List<NewsModel>> response) {
                if (response.isSuccessful()) {
                    otherNewsMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NewsModel>> call, @NonNull Throwable t) {

            }
        });
        return otherNewsMutableLiveData;
    }

    public MutableLiveData<List<NewsModel>> getCatNewsItemMutableLiveData(String id) {
        Log.d("contentValueId", id != null ? id : "null");
        Call<List<NewsModel>> call = apiInterface.fetchCatNewsItem(id);
        call.enqueue(new Callback<List<NewsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<NewsModel>> call, @NonNull Response<List<NewsModel>> response) {
                if (response.isSuccessful()) {
                    catNewsItemMutableLiveData.setValue(response.body());
                    Log.d("contentValueId", catModelMutableLiveData.getValue().get(0).getTitle() != null ? catModelMutableLiveData.getValue().get(0).getTitle() : "null");

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NewsModel>> call, @NonNull Throwable t) {

            }
        });
        return catNewsItemMutableLiveData;
    }

    public LiveData<QuizModelList> getQuizQuestions(String id) {
        Call<QuizModelList> call = apiInterface.fetchQuizQuestions(id);
        call.enqueue(new Callback<QuizModelList>() {
            @Override
            public void onResponse(@NonNull Call<QuizModelList> call, @NonNull Response<QuizModelList> response) {
                if (response.isSuccessful()) {
                    quizModelListMutableLiveData.setValue(response.body());
                } else {
                    Log.d("onResponse", response.message());

                }
            }

            @Override
            public void onFailure(@NonNull Call<QuizModelList> call, @NonNull Throwable t) {
                Log.d("onResponse error", t.getMessage());

            }
        });

        return quizModelListMutableLiveData;
    }

}
