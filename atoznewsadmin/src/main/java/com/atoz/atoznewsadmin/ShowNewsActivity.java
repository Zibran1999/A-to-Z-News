package com.atoz.atoznewsadmin;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atoz.atoznewsadmin.adapters.NewsAdapter;
import com.atoz.atoznewsadmin.databinding.ActivityShowNewsBinding;
import com.atoz.atoznewsadmin.databinding.UploadNewsBinding;
import com.atoz.atoznewsadmin.models.ApiInterface;
import com.atoz.atoznewsadmin.models.ApiWebServices;
import com.atoz.atoznewsadmin.models.MessageModel;
import com.atoz.atoznewsadmin.models.NewsModel;
import com.atoz.atoznewsadmin.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowNewsActivity extends AppCompatActivity implements NewsAdapter.NewsInterface {
    ActivityShowNewsBinding binding;
    List<NewsModel> newsModels;
    NewsAdapter newsAdapter;
    LinearLayoutManager layoutManager;
    ApiInterface apiInterface;
    UploadNewsBinding uploadNewsBinding;
    Dialog dialog, loadingDialog;
    ActivityResultLauncher<String> launcher;
    String encodedImg, formattedDate, selectTime;
    Map<String, String> map = new HashMap<>();
    Call<MessageModel> call;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingDialog = Utils.loadingDialog(this);
        apiInterface = ApiWebServices.getApiInterface();
        newsModels = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.newsRV.setLayoutManager(layoutManager);
        binding.newsRV.setAdapter(newsAdapter);
        key = getIntent().getStringExtra("key");
        fetchNews(key);
    }

    private void fetchNews(String key) {
        Call<List<NewsModel>> listCall = apiInterface.fetchNews(key);
        listCall.enqueue(new Callback<List<NewsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<NewsModel>> call, @NonNull Response<List<NewsModel>> response) {
                if (response.isSuccessful()) {
                    if (!Objects.requireNonNull(response.body()).isEmpty()) {
                        newsModels.clear();
                        newsModels.addAll(response.body());
                        newsAdapter.updateList(newsModels);
                        loadingDialog.dismiss();
                        Log.d("contentValue", newsModels.get(0).getTitle());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NewsModel>> call, @NonNull Throwable t) {
                loadingDialog.dismiss();
                Log.d("contentValue", t.getMessage());
            }
        });

    }

    @Override
    public void newsOnClicked(NewsModel newsModel) {

    }
}