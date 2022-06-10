package dailynews.localandglobalnews.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import dailynews.localandglobalnews.adapters.NewsAdapter;
import dailynews.localandglobalnews.adapters.OtherNewsAdapter;
import dailynews.localandglobalnews.databinding.ActivityShowAllItemsBinding;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.models.BreakingNews.NewsModelFactory;
import dailynews.localandglobalnews.models.BreakingNews.NewsViewModel;
import dailynews.localandglobalnews.models.catNewsItems.CatNewsItemModelFactory;
import dailynews.localandglobalnews.models.catNewsItems.CatNewsItemViewModel;
import dailynews.localandglobalnews.utils.CommonMethods;

public class ShowAllItemsActivity extends AppCompatActivity implements OtherNewsAdapter.OtherNewsInterface, NewsAdapter.NewsInterface {
    ActivityShowAllItemsBinding binding;
    List<NewsModel> catItemNewsModelList = new ArrayList<>();
    List<NewsModel> breakingNewsModelList = new ArrayList<>();

    OtherNewsAdapter otherNewsAdapter;
    NewsAdapter newsAdapter;
    NewsViewModel newsViewModel;
    CatNewsItemViewModel catNewsItemViewModel;
    RecyclerView showAllItemRecyclerView;
    String key, id;
    Dialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowAllItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showAllItemRecyclerView = binding.showAllItemsRecyclerView;
        binding.backIcon.setOnClickListener(v -> onBackPressed());
        loading = CommonMethods.getLoadingDialog(ShowAllItemsActivity.this);
        loading.show();
        id = getIntent().getStringExtra("id");
        key = getIntent().getStringExtra("key");

        switch (key) {
            case "news":
                binding.activityTitle.setText(HtmlCompat.fromHtml(key, HtmlCompat.FROM_HTML_MODE_LEGACY));
                catNewsItemViewModel = new ViewModelProvider(this,
                        new CatNewsItemModelFactory(this.getApplication(), id)).get(CatNewsItemViewModel.class);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                showAllItemRecyclerView.setLayoutManager(layoutManager);
                otherNewsAdapter = new OtherNewsAdapter(this, this);
                showAllItemRecyclerView.setAdapter(otherNewsAdapter);
                fetCatNewItems();
                break;

            case "breakingNews":

                newsViewModel = new ViewModelProvider(ShowAllItemsActivity.this, new NewsModelFactory(
                        this.getApplication(),"breaking_news")).get(NewsViewModel.class);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                showAllItemRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                newsAdapter = new NewsAdapter(this,this,true);
                showAllItemRecyclerView.setAdapter(newsAdapter);
                fetchBreakingNews();

                break;

        }
    }

    private void fetCatNewItems() {
        catNewsItemViewModel.getCatNewsItems().observe(this, newsModels -> {
            if (!newsModels.isEmpty()) {
                catItemNewsModelList.clear();
                catItemNewsModelList.addAll(newsModels);
                otherNewsAdapter.updateList(catItemNewsModelList);
                loading.dismiss();
            }
        });
    }

    private void fetchBreakingNews() {
        newsViewModel.getAllNews().observe(ShowAllItemsActivity.this, newsModels -> {
            if (!newsModels.isEmpty()){
                breakingNewsModelList.clear();
                breakingNewsModelList.addAll(newsModels);
                newsAdapter.updateList(breakingNewsModelList);
                loading.dismiss();
            }
        });
    }
    @Override
    public void OnOtherNewsClicked(NewsModel newsModel) {
        Intent intent = new Intent(ShowAllItemsActivity.this, NewsDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("news", newsModel);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void newsOnClicked(NewsModel newsModel) {

    }
}